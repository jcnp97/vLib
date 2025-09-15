package asia.virtualmc.vLib.core.ray_trace;

import asia.virtualmc.vLib.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RayTraceManager {
    private final Map<UUID, RayTraceHandler<?>> activeHandlers = new ConcurrentHashMap<>();
    private final Map<UUID, Queue<Operation>> pendingOps = new ConcurrentHashMap<>();

    private sealed interface Operation permits RegisterOp, UnregisterOp {}
    private record RegisterOp(RayTraceHandler<?> handler) implements Operation {}
    private record UnregisterOp() implements Operation {}

    public RayTraceManager(@NotNull Main plugin) {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                UUID uuid = event.getPlayer().getUniqueId();
                RayTraceHandler<?> handler = activeHandlers.remove(uuid);
                if (handler != null) {
                    handler.cleanup(event.getPlayer());
                }
                pendingOps.remove(uuid);
            }
        }, plugin);
    }

    /**
     * Register a handler for a player.
     * - If no handler exists, register immediately.
     * - If a handler exists, queue the operation with priority rules.
     */
    public void register(Player player, RayTraceHandler<?> handler) {
        UUID uuid = player.getUniqueId();
        RayTraceHandler<?> current = activeHandlers.get(uuid);

        if (current == null) {
            // Immediate if no active handler
            activeHandlers.put(uuid, handler);
            return;
        }

        Queue<Operation> queue = pendingOps.computeIfAbsent(uuid, u -> new ArrayDeque<>());
        // Find last queued register, if any
        RegisterOp lastQueued = null;
        for (Operation op : queue) {
            if (op instanceof RegisterOp rop) {
                lastQueued = rop;
            }
        }

        if (lastQueued == null) {
            // No queued register → queue this one
            queue.add(new RegisterOp(handler));
        } else {
            // Compare priorities
            int diff = Integer.compare(handler.getPriority(), lastQueued.handler().getPriority());
            if (diff > 0) {
                // Replace queued with new one (higher priority wins)
                queue.remove(lastQueued);
                queue.add(new RegisterOp(handler));
            } else if (diff == 0) {
                // Same priority → override
                queue.remove(lastQueued);
                queue.add(new RegisterOp(handler));
            }
            // Lower priority is ignored
        }
    }

    /**
     * Unregister handler for a player.
     * Always queued FIFO.
     */
    public void unregister(Player player) {
        UUID uuid = player.getUniqueId();
        Queue<Operation> queue = pendingOps.computeIfAbsent(uuid, u -> new ArrayDeque<>());
        queue.add(new UnregisterOp());
    }

    /**
     * Called each tick to process queued operations and active handlers.
     */
    public void task() {
        Iterator<Map.Entry<UUID, RayTraceHandler<?>>> it = activeHandlers.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, RayTraceHandler<?>> entry = it.next();
            UUID uuid = entry.getKey();
            RayTraceHandler<?> handler = entry.getValue();
            Player player = Bukkit.getPlayer(uuid);

            // Process queued operations first (FIFO)
            Queue<Operation> ops = pendingOps.get(uuid);
            if (ops != null && !ops.isEmpty()) {
                while (!ops.isEmpty()) {
                    Operation op = ops.poll();
                    if (op instanceof UnregisterOp) {
                        handler.cleanup(player);
                        it.remove();
                        break; // stop processing further ops for this tick
                    } else if (op instanceof RegisterOp rop) {
                        handler.cleanup(player);
                        activeHandlers.put(uuid, rop.handler());
                        break; // only apply one register per tick
                    }
                }
                if (ops.isEmpty()) {
                    pendingOps.remove(uuid);
                }
            }

            // Skip if handler removed above
            if (!activeHandlers.containsKey(uuid)) continue;

            // Validate player state
            if (player == null || !player.isOnline()) {
                handler.cleanup(player);
                it.remove();
                continue;
            }

            if (!handler.isActive(player)) {
                handler.cleanup(player);
                it.remove();
                continue;
            }

            // Perform raytrace
            Object result = handler.rayTrace(player);
            callHandler(handler, player, result);
        }
    }

    public void disable() {
        for (Map.Entry<UUID, RayTraceHandler<?>> entry : activeHandlers.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            entry.getValue().cleanup(player);
        }
        activeHandlers.clear();
        pendingOps.clear();
    }

    @SuppressWarnings("unchecked")
    private <RayTrace> void callHandler(RayTraceHandler<RayTrace> handler, Player player, Object result) {
        handler.handle(player, (RayTrace) result);
    }
}
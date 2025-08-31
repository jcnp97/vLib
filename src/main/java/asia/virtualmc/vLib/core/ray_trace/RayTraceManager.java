package asia.virtualmc.vLib.core.ray_trace;

import asia.virtualmc.vLib.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RayTraceManager {
    private final Map<UUID, RayTraceHandler<?>> rayTraceCache = new ConcurrentHashMap<>();

    public RayTraceManager(@NotNull Main plugin) {
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                UUID uuid = event.getPlayer().getUniqueId();
                RayTraceHandler<?> handler = rayTraceCache.remove(uuid);
                if (handler != null) {
                    handler.cleanup(event.getPlayer());
                }
            }
        }, plugin);
    }

    public void register(Player player, RayTraceHandler<?> handler) {
        UUID uuid = player.getUniqueId();
        RayTraceHandler<?> current = rayTraceCache.get(uuid);

        if (current == null) {
            rayTraceCache.put(uuid, handler);
            return;
        }

        int priorityDifference = Integer.compare(handler.getPriority(), current.getPriority());
        if (priorityDifference >= 0) {
            // cleanup old handler if overridden
            current.cleanup(player);
            rayTraceCache.put(uuid, handler);
        }
    }

    public void unregister(Player player) {
        UUID uuid = player.getUniqueId();
        RayTraceHandler<?> handler = rayTraceCache.remove(uuid);
        if (handler != null) {
            handler.cleanup(player);
        }
    }

    public void task() {
        Iterator<Map.Entry<UUID, RayTraceHandler<?>>> it = rayTraceCache.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, RayTraceHandler<?>> entry = it.next();
            UUID uuid = entry.getKey();
            RayTraceHandler<?> handler = entry.getValue();

            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                handler.cleanup(player);
                it.remove();
                continue;
            }

            if (!rayTraceCache.containsKey(uuid)) {
                continue;
            }

            if (!handler.isActive(player)) {
                handler.cleanup(player);
                it.remove();
                continue;
            }

            Object result = handler.rayTrace(player);
            callHandler(handler, player, result);
        }
    }

    public void disable() {
        for (Map.Entry<UUID, RayTraceHandler<?>> entry : rayTraceCache.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            entry.getValue().cleanup(player);
        }
        rayTraceCache.clear();
    }

    @SuppressWarnings("unchecked")
    private <RayTrace> void callHandler(RayTraceHandler<RayTrace> handler, Player player, Object result) {
        handler.handle(player, (RayTrace) result);
    }
}
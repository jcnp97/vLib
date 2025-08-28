package asia.virtualmc.vLib.core.ray_trace;

import org.bukkit.entity.Player;

public interface RayTraceHandler<RayTrace> {
    /**
     * Perform the actual raytrace for this plugin.
     * Each plugin can decide whether it cares about blocks, entities, etc.,
     * and return its own result type.
     */
    RayTrace rayTrace(Player player);

    /**
     * Handle the result of the raytrace.
     */
    void handle(Player player, RayTrace result);

    /**
     * Whether this handler wants to remain active for the player.
     */
    boolean isActive(Player player);

    /**
     * Handler priority.
     */
    int getPriority();

    /**
     * Called once when this player is completely unregistered
     * from the RayTraceManager (logout, no longer active, etc.).
     * Use this to remove holograms, particles, etc.
     */
    void cleanup(Player player);
}

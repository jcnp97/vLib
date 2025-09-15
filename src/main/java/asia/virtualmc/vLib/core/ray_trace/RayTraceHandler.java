package asia.virtualmc.vLib.core.ray_trace;

import org.bukkit.entity.Player;

public interface RayTraceHandler<RayTrace> {
    /**
     * Perform the actual raytrace for this plugin.
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
     * from the RayTraceManager (logout, override, etc.).
     */
    void cleanup(Player player);
}
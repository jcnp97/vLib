package asia.virtualmc.vLib.integration.worldguard.utilities;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.integration.worldguard.events.WGEntryHandler;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class WorldGuardUtils {
    private static RegionContainer container;

    /**
     * Initializes the WorldGuard integration by validating the plugin's presence,
     * checking version compatibility, and registering the custom region entry handler.
     * <p>
     * If any of the checks fail, the integration is disabled and a console warning is printed.
     * </p>
     */
    public static void load() {
        Plugin plugin = Main.getInstance();
        if (!plugin.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            ConsoleUtils.severe("WorldGuard not found. Disabling integration hooks..");
            return;
        }

        String version = WorldGuard.getVersion();
        if (version.isEmpty()) {
            ConsoleUtils.severe("Couldn't identify WorldGuard version! Disabling WorldGuard integration..");
            return;
        }

        if (!version.startsWith("7.")) {
            ConsoleUtils.severe("Integration only works with 7.0.0 and above! Disabling WorldGuard integration..");
            return;
        }

        if (!WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(WGEntryHandler.factory, null)) {
            ConsoleUtils.severe("Could not register WorldGuard handler! Disabling WorldGuard integration..");
            return;
        }

        container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ConsoleUtils.info("Successfully hooked into: WorldGuard");
    }

    /**
     * Retrieves the first matching region name the player is currently in, from a given set of region names.
     *
     * @param player  the player whose location is used for region lookup
     * @param regions a set of region names (case-insensitive) to match against the regions at the player's location
     * @return the name of the first matching region (in lowercase), or {@code null} if none match
     */
    public static String getRegion(Player player, Set<String> regions) {
        if (container == null) {
            ConsoleUtils.severe("Trying to use worldguard module but it is disabled!");
            return null;
        }

        Location loc = player.getLocation();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(loc.getWorld()));

        if (regionManager == null) return null;
        ApplicableRegionSet regionSet = regionManager
                .getApplicableRegions(BukkitAdapter.asBlockVector(loc));

        for (ProtectedRegion region : regionSet) {
            String regionName = region.getId().toLowerCase();
            if (regions.contains(regionName)) {
                return regionName;
            }
        }

        return null;
    }

    /**
     * Checks whether the player is currently standing in any of the specified regions.
     *
     * @param player  the player whose location is checked
     * @param regions a set of region names (case-insensitive) to match against
     * @return {@code true} if the player is inside any of the provided regions, {@code false} otherwise
     */
    public static boolean isInRegion(Player player, Set<String> regions) {
        if (container == null) {
            ConsoleUtils.severe("Trying to use worldguard module but it is disabled!");
            return false;
        }

        Location loc = player.getLocation();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(loc.getWorld()));

        if (regionManager == null) return false;
        ApplicableRegionSet regionSet = regionManager
                .getApplicableRegions(BukkitAdapter.asBlockVector(loc));

        for (ProtectedRegion region : regionSet) {
            String regionName = region.getId().toLowerCase();
            if (regions.contains(regionName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the given player is currently located within a specific WorldGuard region.
     *
     * @param player     the player to check
     * @param regionName the name of the region to match (case-insensitive)
     * @return {@code true} if the player is inside the specified region, {@code false} otherwise
     *
     * <p>Logs a severe error and returns {@code false} if WorldGuard integration is disabled.</p>
     */
    public static boolean isInRegion(Player player, String regionName) {
        if (container == null) {
            ConsoleUtils.severe("Trying to use worldguard module but it is disabled!");
            return false;
        }

        Location loc = player.getLocation();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(loc.getWorld()));

        if (regionManager == null) return false;
        ApplicableRegionSet regionSet = regionManager
                .getApplicableRegions(BukkitAdapter.asBlockVector(loc));

        for (ProtectedRegion region : regionSet) {
            String id = region.getId().toLowerCase();
            if (id.equals(regionName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns the {@link RegionContainer} instance used for region queries.
     * <p>
     * If WorldGuard integration was not successfully loaded, logs a severe error
     * and returns {@code null}.
     * </p>
     *
     * @return the active {@link RegionContainer}, or {@code null} if integration is disabled
     */
    public static RegionContainer getInstance() {
        if (container == null) {
            ConsoleUtils.severe("Trying to use worldguard module but it is disabled!");
        }

        return container;
    }
}

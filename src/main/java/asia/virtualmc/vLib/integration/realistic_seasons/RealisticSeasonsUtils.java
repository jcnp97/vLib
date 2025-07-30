package asia.virtualmc.vLib.integration.realistic_seasons;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import me.casperge.realisticseasons.api.SeasonsAPI;
import me.casperge.realisticseasons.season.Season;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class RealisticSeasonsUtils {
    private static SeasonsAPI seasonsAPI;

    /**
     * Initializes the RealisticSeasons integration by checking if the plugin is loaded.
     * If found, retrieves and stores the {@link SeasonsAPI} instance; otherwise, logs a warning and skips setup.
     */
    public static void load() {
        Plugin plugin = Main.getInstance();
        if (plugin.getServer().getPluginManager().getPlugin("RealisticSeasons") == null) {
            ConsoleUtils.severe("RealisticSeasons not found. Disabling integration hooks..");
            return;
        }

        seasonsAPI = SeasonsAPI.getInstance();
        ConsoleUtils.info("RealisticSeasons found. Applying integration hooks..");
    }

    /**
     * Sets the current season for the specified world using the RealisticSeasons API.
     * If the API is not initialized, this method does nothing.
     *
     * @param world  The world to update the season for.
     * @param season The season to set in the given world.
     */
    public static void set(World world, Season season) {
        if (seasonsAPI == null) {
            ConsoleUtils.severe("Trying to use seasons module but it is disabled!");
            return;
        }
        seasonsAPI.setSeason(world, season);
    }

    /**
     * Retrieves the current season of the specified world as a custom {@link EnumsLib.Seasons} enum.
     * Returns {@code DISABLED} if the API is not initialized or the season is unrecognized.
     *
     * @param world The world to retrieve the season from.
     * @return The corresponding custom season enum, or DISABLED if unavailable.
     */
    public static EnumsLib.Seasons get(World world) {
        if (seasonsAPI == null) {
            ConsoleUtils.severe("Trying to use seasons module but it is disabled!");
            return null;
        }

        switch (seasonsAPI.getSeason(world)) {
            default -> { return EnumsLib.Seasons.DISABLED; }
            case Season.SPRING -> { return EnumsLib.Seasons.SPRING; }
            case Season.SUMMER -> { return EnumsLib.Seasons.SUMMER; }
            case Season.FALL -> { return EnumsLib.Seasons.FALL; }
            case Season.WINTER -> { return EnumsLib.Seasons.WINTER; }
        }
    }

    /**
     * Gets the cached instance of the RealisticSeasons {@link SeasonsAPI}, or null if not loaded.
     *
     * @return The current SeasonsAPI instance, or null if not initialized.
     */
    public static SeasonsAPI getInstance() {
        if (seasonsAPI == null) {
            ConsoleUtils.severe("Trying to use seasons module but it is disabled!");
        }
        return seasonsAPI;
    }
}

package asia.virtualmc.vLib.integration.realistic_seasons;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.plugin.Plugin;

public class RSUtils {
    private static SeasonsAPI seasonsAPI;

    public static void load() {
        Plugin plugin = Main.getInstance();
        if (plugin.getServer().getPluginManager().getPlugin("RealisticSeasons") == null) {
            ConsoleUtils.severe("RealisticSeasons not found. Disabling integration hooks..");
            return;
        }

        // Todo: Add seasonsAPI
        ConsoleUtils.info("RealisticSeasons found. Applying integration hooks..");
    }

    public static void set(World world, Season season) {
    }
}

package asia.virtualmc.vLib.integration;

import asia.virtualmc.vLib.integration.better_model.BMCommands;
import asia.virtualmc.vLib.integration.realistic_seasons.RealisticSeasonsUtils;
import asia.virtualmc.vLib.integration.skinsrestorer.SkinsRestorerUtils;
import asia.virtualmc.vLib.integration.ultimate_advancement.UAdvancementUtils;
import asia.virtualmc.vLib.integration.vault.EconomyUtils;
import asia.virtualmc.vLib.integration.vault.PermissionUtils;
import asia.virtualmc.vLib.integration.worldguard.utilities.WorldGuardUtils;

import java.util.Map;

public class IntegrationManager {

    public IntegrationManager(Map<String, Boolean> modules) {
        if (Boolean.TRUE.equals(modules.get("realistic_seasons"))) {
            RealisticSeasonsUtils.load();
        }

        if (Boolean.TRUE.equals(modules.get("vault"))) {
            EconomyUtils.load();
            PermissionUtils.load();
        }

        if (Boolean.TRUE.equals(modules.get("worldguard"))) {
            WorldGuardUtils.load();
        }

        if (Boolean.TRUE.equals(modules.get("skins_restorer"))) {
            SkinsRestorerUtils.load();
        }

        if (Boolean.TRUE.equals(modules.get("better_model"))) {
            BMCommands.load();
        }

        if (Boolean.TRUE.equals(modules.get("ultimate_advancement"))) {
            UAdvancementUtils.load();
        }

        if (Boolean.TRUE.equals(modules.get("hologram_lib"))) {
            // Todo: Add HologramLib
        }
    }
}

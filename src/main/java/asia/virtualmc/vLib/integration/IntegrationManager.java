package asia.virtualmc.vLib.integration;

import asia.virtualmc.vLib.integration.realistic_seasons.RealisticSeasonsUtils;
import asia.virtualmc.vLib.integration.vault.EconomyUtils;
import asia.virtualmc.vLib.integration.vault.PermissionUtils;

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

        // Todo: Add HologramLib & WorldGuard
    }
}

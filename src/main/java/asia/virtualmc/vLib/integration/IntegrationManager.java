package asia.virtualmc.vLib.integration;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.Registry;
import asia.virtualmc.vLib.integration.better_model.BMCommands;
import asia.virtualmc.vLib.integration.hologram_lib.HologramLibUtils;
import asia.virtualmc.vLib.integration.hologram_lib.PlayerHologramUtils;
import asia.virtualmc.vLib.integration.realistic_seasons.RealisticSeasonsUtils;
import asia.virtualmc.vLib.integration.skinsrestorer.SkinsRestorerUtils;
import asia.virtualmc.vLib.integration.ultimate_advancement.UAdvancementUtils;
import asia.virtualmc.vLib.integration.uni_dialog.UniDialogUtils;
import asia.virtualmc.vLib.integration.vault.EconomyUtils;
import asia.virtualmc.vLib.integration.vault.PermissionUtils;
import asia.virtualmc.vLib.integration.worldguard.utilities.WorldGuardUtils;
import com.maximde.hologramlib.HologramLib;

import java.util.Map;

public class IntegrationManager {

    public IntegrationManager() {
        enable();
    }

    public void enable() {
        Map<String, Boolean> modules = Registry.getModules();

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

        if (Boolean.TRUE.equals(modules.get("uni_dialogs"))) {
            UniDialogUtils.load();
        }

        if (Boolean.TRUE.equals(modules.get("hologram_lib"))) {
            HologramLib.onLoad(Main.getInstance());
            HologramLibUtils.load();
            PlayerHologramUtils.load();
        }
    }

    public void disable() {
        HologramLibUtils.clearAll();
    }
}

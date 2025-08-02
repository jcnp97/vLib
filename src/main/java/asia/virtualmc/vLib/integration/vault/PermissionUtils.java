package asia.virtualmc.vLib.integration.vault;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import net.milkbowl.vault2.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public class PermissionUtils {
    private static Permission permission;

    /**
     * Initializes the Vault permission integration.
     * Logs the result of the setup process and stores the active {@link Permission} provider.
     * This method should be called during plugin startup if Vault is present.
     */
    public static void load() {
        Plugin plugin = Main.getInstance();
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            ConsoleUtils.severe("Vault not found. Disabling integration hooks..");
            return;
        }

        ConsoleUtils.warning("Vault found! Attempting to get permission registration..");
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);

        if (rsp == null) {
            ConsoleUtils.severe("No permission provider was registered with Vault!");
            return;
        }

        permission = rsp.getProvider();
        ConsoleUtils.info("Successfully hooked into: " + permission.getName());
    }

    /**
     * Adds a permission node to the specified player if they do not already have it.
     * Logs the result of the operation. Does nothing if Vault or permission provider is not available.
     *
     * @param player The player to grant the permission to.
     * @param node   The permission node to add.
     */
    public static void add(@NotNull Player player, @NotNull String node) {
        if (permission == null) {
            ConsoleUtils.severe("Trying to use permission module but it is disabled!");
            return;
        }

        if (permission.playerHas(player, node)) {
            ConsoleUtils.severe(player.getName() + " already has " + node + " permission!");
            return;
        }

        boolean result = permission.playerAdd(player, node);
        if (result) {
            ConsoleUtils.info("Added " + node + " to " + player.getName());
        }
    }

    /**
     * Removes a permission node from the specified player if they currently have it.
     * Logs the result of the operation. Does nothing if Vault or permission provider is not available.
     *
     * @param player The player to remove the permission from.
     * @param node   The permission node to remove.
     */
    public static void remove(@NotNull Player player, @NotNull String node) {
        if (permission == null) {
            ConsoleUtils.severe("Trying to use permission module but it is disabled!");
            return;
        }

        if (!permission.playerHas(player, node)) {
            ConsoleUtils.severe(player.getName() + " does not have " + node + " permission!");
            return;
        }

        boolean result = permission.playerRemove(player, node);
        if (result) {
            ConsoleUtils.info("Removed " + node + " from " + player.getName());
        }
    }

    /**
     * Checks whether the specified player has the given permission node.
     * Returns false and logs a warning if the permission provider is not available.
     *
     * @param player The player to check.
     * @param node   The permission node to check for.
     * @return true if the player has the permission; false otherwise.
     */
    public static boolean has(@NotNull Player player, @NotNull String node) {
        if (permission == null) {
            ConsoleUtils.severe("Trying to use permission module but it is disabled!");
            return false;
        }

        return permission.playerHas(player, node);
    }

    /**
     * Returns the currently hooked {@link Permission} provider from Vault.
     *
     * @return The Permission instance, or null if not initialized.
     */
    public static Permission getPermission() {
        return permission;
    }
}

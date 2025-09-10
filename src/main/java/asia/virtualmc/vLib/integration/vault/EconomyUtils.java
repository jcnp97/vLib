package asia.virtualmc.vLib.integration.vault;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.digit.DecimalUtils;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import net.milkbowl.vault2.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class EconomyUtils {
    private static Economy economy;

    /**
     * Initializes the Vault economy integration by attempting to hook into a registered Economy provider.
     * Logs appropriate messages if Vault or the provider is missing.
     */
    public static void load() {
        Plugin plugin = Main.getInstance();
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            ConsoleUtils.severe("Vault not found. Disabling integration hooks..");
            return;
        }

        ConsoleUtils.warning("Vault found! Attempting to get economy registration..");
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) {
            ConsoleUtils.severe("No economy provider was registered with Vault!");
            return;
        }

        economy = rsp.getProvider();
        ConsoleUtils.info("Successfully hooked into: " + economy.getName());
    }

    /**
     * Adds the specified amount of money to a player's balance using Vault.
     *
     * @param player the player to deposit money to
     * @param value  the amount to deposit (double)
     * @return true if the transaction was successful, false otherwise
     */
    public static boolean add(@NotNull Player player, double value) {
        if (economy == null) {
            ConsoleUtils.severe("Trying to use economy module but it is disabled!");
            return false;
        }

        try {
            UUID uuid = player.getUniqueId();
            BigDecimal amount = DecimalUtils.doubleToBig(value);
            economy.deposit(Main.getPluginName(), uuid, amount);

            double balance = DecimalUtils.bigToDouble(economy.balance(Main.getPluginName(), uuid));
            MessageUtils.sendMessage(player, "You have received $" + amount +
                    ". You now have $" + balance + ".", EnumsLib.MessageType.GREEN);

            return true;

        } catch (Exception e) {
            ConsoleUtils.severe("An error occurred when trying to add money to " + player.getName());
            e.getMessage();
            return false;
        }
    }

    /**
     * Removes the specified amount of money from a player's balance using Vault.
     *
     * @param player the player to withdraw money from
     * @param value  the amount to withdraw (double)
     * @return true if the transaction was successful and the player had sufficient funds, false otherwise
     */
    public boolean remove(@NotNull Player player, double value) {
        if (economy == null) {
            ConsoleUtils.severe("Trying to use economy module but it is disabled!");
            return false;
        }

        UUID uuid = player.getUniqueId();
        BigDecimal amount = DecimalUtils.doubleToBig(value);
        if (amount.compareTo(economy.balance(Main.getPluginName(), uuid)) < 0) {
            return false;
        }

        try {
            economy.withdraw(Main.getPluginName(), uuid, amount);
            double balance = DecimalUtils.bigToDouble(economy.balance(Main.getPluginName(), uuid));

            MessageUtils.sendMessage(player, "<gold>$" + amount +
                    " <red>was taken from your balance. You now have <green>$" + balance + ".");

            return true;

        } catch (Exception e) {
            ConsoleUtils.severe("An error occurred when trying to remove money from " + player.getName());
            e.getMessage();
            return false;
        }
    }

    /**
     * Deducts a percentage-based tax from the player's balance, calculated from a given transaction value.
     *
     * @param player the player to deduct the tax from
     * @param tax    the percentage rate of tax (between 0 and 100)
     * @param value  the base value to calculate tax on
     * @return the actual tax amount paid, or -1 if payment failed or invalid parameters
     * @throws IllegalArgumentException if tax is out of bounds (not between 0.0 and 100.0)
     */
    public double tax(@NotNull Player player, double tax, double value) {
        if (economy == null) {
            ConsoleUtils.severe("Trying to use economy module but it is disabled!");
            return -1;
        }

        if (tax < 0 || tax > 100) {
            throw new IllegalArgumentException("Tax rate must be between 0.0 and 100.0");
        }

        double toPay = (tax / 100) * value;

        if (toPay <= 0) {
            return -1;
        }

        UUID uuid = player.getUniqueId();
        double balance = DecimalUtils.bigToDouble(economy.balance(Main.getPluginName(), uuid));
        if (toPay > balance) {
            return -1; // Can't pay tax if player doesn't have enough money
        }

        economy.withdraw(Main.getPluginName(), uuid, DecimalUtils.doubleToBig(toPay));
        MessageUtils.sendMessage(player, "<red>You have paid <gold>$" + toPay +
                " <red>in taxes.");

        return toPay;
    }

    /**
     * Retrieves the player's current balance using Vault.
     *
     * @param player the player whose balance is to be retrieved
     * @return the player's balance as a double, or 0 if economy is disabled
     */
    public double get(@NotNull Player player) {
        if (economy == null) {
            ConsoleUtils.severe("Trying to use economy module but it is disabled!");
            return 0;
        }

        return DecimalUtils.bigToDouble(economy.balance(Main.getPluginName(), player.getUniqueId()));
    }

    /**
     * Returns the current Vault Economy provider that is hooked, if any.
     *
     * @return the economy provider, or null if not initialized
     */
    public static Economy getEconomy() {
        return economy;
    }
}

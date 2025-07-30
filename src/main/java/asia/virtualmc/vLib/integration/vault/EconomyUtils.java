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

    public static void load() {
        Plugin plugin = Main.getInstance();
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
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

    public static boolean add(@NotNull Player player, double value) {
        if (economy == null) {
            ConsoleUtils.severe("Trying to use economy module but it is disabled!");
            return false;
        }

        try {
            UUID uuid = player.getUniqueId();
            BigDecimal amount = DecimalUtils.toBigDecimal(value);
            economy.deposit(Main.getPluginName(), uuid, amount);

            double balance = DecimalUtils.toDouble(economy.balance(Main.getPluginName(), uuid));
            MessageUtils.sendMessage(player, "You have received $" + amount +
                    ". You now have $" + balance + ".", EnumsLib.MessageType.GREEN);

            return true;

        } catch (Exception e) {
            ConsoleUtils.severe("An error occurred when trying to add money to " + player.getName());
            e.getMessage();
            return false;
        }
    }

    public boolean remove(@NotNull Player player, double value) {
        if (economy == null) {
            ConsoleUtils.severe("Trying to use economy module but it is disabled!");
            return false;
        }

        UUID uuid = player.getUniqueId();
        BigDecimal amount = DecimalUtils.toBigDecimal(value);
        if (amount.compareTo(economy.balance(Main.getPluginName(), uuid)) < 0) {
            return false;
        }

        try {
            economy.withdraw(Main.getPluginName(), uuid, amount);
            double balance = DecimalUtils.toDouble(economy.balance(Main.getPluginName(), uuid));

            MessageUtils.sendMessage(player, "<gold>$" + amount +
                    " <red>was taken from your balance. You now have <green>$" + balance + ".");

            return true;

        } catch (Exception e) {
            ConsoleUtils.severe("An error occurred when trying to remove money from " + player.getName());
            e.getMessage();
            return false;
        }
    }

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
        double balance = DecimalUtils.toDouble(economy.balance(Main.getPluginName(), uuid));
        if (toPay > balance) {
            return -1; // Can't pay tax if player doesn't have enough money
        }

        economy.withdraw(Main.getPluginName(), uuid, DecimalUtils.toBigDecimal(toPay));
        MessageUtils.sendMessage(player, "<red>You have paid <gold>$" + toPay +
                " <red>in taxes.");

        return toPay;
    }

    public double get(@NotNull Player player) {
        if (economy == null) {
            ConsoleUtils.severe("Trying to use economy module but it is disabled!");
            return 0;
        }

        return DecimalUtils.toDouble(economy.balance(Main.getPluginName(), player.getUniqueId()));
    }

    public static Economy getEconomy() {
        return economy;
    }
}

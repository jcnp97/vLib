package asia.virtualmc.vLib.utilities.messages;

import asia.virtualmc.vLib.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ConsoleUtils {

    /**
     * Sends a green info-level message to the console using the default plugin prefix.
     *
     * @param message The message to log.
     */
    public static void info(String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convert("<green>" + Main.getPrefix() + message));
    }

    /**
     * Sends a yellow warning-level message to the console using the default plugin prefix.
     *
     * @param message The message to log.
     */
    public static void warning(String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convert("<yellow>" + Main.getPrefix() + message));
    }

    /**
     * Sends a red severe-level message to the console using the default plugin prefix.
     *
     * @param message The message to log.
     */
    public static void severe(String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convert("<red>" + Main.getPrefix() + message));
    }

    /**
     * Sends a green info-level message to the console using a custom plugin prefix.
     *
     * @param prefix The prefix to prepend to the message.
     * @param message      The message to log.
     */
    public static void info(String prefix, String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convert("<green>" + prefix + message));
    }

    /**
     * Sends a yellow warning-level message to the console using a custom plugin prefix.
     *
     * @param prefix The prefix to prepend to the message.
     * @param message      The message to log.
     */
    public static void warning(String prefix, String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convert("<yellow>" + prefix + message));
    }

    /**
     * Sends a red severe-level message to the console using a custom plugin prefix.
     *
     * @param prefix The prefix to prepend to the message.
     * @param message      The message to log.
     */
    public static void severe(String prefix, String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.convert("<red>" + prefix + message));
    }
}

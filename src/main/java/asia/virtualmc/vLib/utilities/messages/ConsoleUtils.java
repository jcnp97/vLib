package asia.virtualmc.vLib.utilities.messages;

import asia.virtualmc.vLib.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ConsoleUtils {

    /**
     * Sends a green info-level message to the console using the default plugin prefix.
     *
     * @param message The message to log.
     */
    public static void info(String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.toComponent("<green>" + Main.getPrefix() + " " + message));
    }

    /**
     * Sends a yellow warning-level message to the console using the default plugin prefix.
     *
     * @param message The message to log.
     */
    public static void warning(String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.toComponent("<yellow>" + Main.getPrefix() + " " + message));
    }

    /**
     * Sends a red severe-level message to the console using the default plugin prefix.
     *
     * @param message The message to log.
     */
    public static void severe(String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.toComponent("<red>" + Main.getPrefix() + " " + message));
    }

    /**
     * Sends a green info-level message to the console using a custom plugin prefix.
     *
     * @param prefix The prefix to prepend to the message.
     * @param message      The message to log.
     */
    public static void info(String prefix, String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.toComponent("<green>" + prefix + " " + message));
    }

    /**
     * Sends a yellow warning-level message to the console using a custom plugin prefix.
     *
     * @param prefix The prefix to prepend to the message.
     * @param message      The message to log.
     */
    public static void warning(String prefix, String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.toComponent("<yellow>" + prefix + " " + message));
    }

    /**
     * Sends a red severe-level message to the console using a custom plugin prefix.
     *
     * @param prefix The prefix to prepend to the message.
     * @param message      The message to log.
     */
    public static void severe(String prefix, String message) {
        CommandSender console = Bukkit.getConsoleSender();
        console.sendMessage(AdventureUtils.toComponent("<red>" + prefix + " " + message));
    }

    /**
     * Logs each entry in the provided map using the {@code info()} method.
     * <p>
     * This method iterates through the map and prints both the key and value
     * for each entry, formatted as {@code key=<key>, value=<value>}.
     * It will only run if the map is not empty.
     * </p>
     *
     * @param map the map to debug and print entries from
     * @param <K> the type of keys in the map
     * @param <V> the type of values in the map
     */
    public static <K, V> void debugMap(Map<K, V> map) {
        if (!map.isEmpty()) {
            for (Map.Entry<K, V> entry : map.entrySet()) {
                info("Map debugging: key=" + entry.getKey() + ", value=" + entry.getValue());
            }
        }
    }

    /**
     * Logs each element in the given set for debugging purposes.
     *
     * @param set the set of objects to log
     */
    public static void debugSet(Set<Object> set) {
        if (!set.isEmpty()) {
            for (Object object : set) {
                info("Set debugging: " + object);
            }
        }
    }
}

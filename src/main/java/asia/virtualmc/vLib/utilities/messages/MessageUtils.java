package asia.virtualmc.vLib.utilities.messages;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageUtils {
    private static final String[] prefixes = new String[]{"<#6EFFB2>", "<#FCD05C>", "<#FF6262>"};

    /**
     * Loads custom prefixes from {@code messages.yml} if available.
     * This overrides the default color codes defined in {@link #prefixes}.
     *
     * @param plugin the plugin instance used to access the config file
     */
    public static void load(Main plugin) {
        YamlDocument yaml = YAMLUtils.getYaml(plugin, "messages.yml");
        if (yaml == null) {
            ConsoleUtils.severe("Unable to read messages.yml. Message prefixes are disabled.");
            return;
        }

        prefixes[0] = yaml.getString("prefix.info");
        prefixes[1] = yaml.getString("prefix.warning");
        prefixes[2] = yaml.getString("prefix.severe");
    }

    /**
     * Sends a styled green message to the specified player using Adventure component formatting.
     *
     * @param player  the player to send the message to
     * @param message the message content, excluding any color formatting
     */
    public static void sendMessage(@NotNull Player player, String message) {
        if (player.isOnline()) {
            player.sendMessage(AdventureUtils.toComponent("<#8BFFA9>" + message));
        }
    }

    /**
     * Sends a message to the specified player, with a color-coded prefix
     * based on the provided {@link EnumsLib.MessageType}.
     *
     * @param player  the player to send the message to
     * @param message the message content, excluding any color formatting
     * @param type    the type of message determining the color/prefix
     */
    public static void sendMessage(@NotNull Player player, String message, EnumsLib.MessageType type) {
        if (player.isOnline()) {
            switch (type) {
                case GREEN -> player.sendMessage(AdventureUtils.toComponent(prefixes[0] + message));
                case YELLOW -> player.sendMessage(AdventureUtils.toComponent(prefixes[1] + message));
                case RED -> player.sendMessage(AdventureUtils.toComponent(prefixes[2] + message));
            }
        }
    }

    /**
     * Broadcasts a formatted message to all players on the server.
     * Assumes the message already includes color codes or formatting.
     *
     * @param message the raw message to broadcast
     */
    public static void sendBroadcast(String message) {
        Bukkit.getServer().sendMessage(AdventureUtils.toComponent(message));
    }

    /**
     * Broadcasts a message to all players with a styled prefix based on {@link EnumsLib.MessageType}.
     *
     * @param message the message content, excluding any color formatting
     * @param type    the type of message determining the color/prefix
     */
    public static void sendBroadcast(String message, EnumsLib.MessageType type) {
        switch (type) {
            case GREEN -> Bukkit.getServer().sendMessage(AdventureUtils.toComponent(prefixes[0] + message));
            case YELLOW -> Bukkit.getServer().sendMessage(AdventureUtils.toComponent(prefixes[1] + message));
            case RED -> Bukkit.getServer().sendMessage(AdventureUtils.toComponent(prefixes[2] + message));
        }
    }

    /**
     * Returns the current array of color-coded message prefixes.
     *
     * @return an array of 3 strings representing info, warning, and severe prefixes
     */
    public static String[] getPrefixes() {
        return prefixes;
    }

    /**
     * Returns the color-coded prefix used for informational messages.
     *
     * @return the info message prefix (e.g., green styled)
     */
    public static String info() {
        return prefixes[0];
    }

    /**
     * Returns the color-coded prefix used for warning messages.
     *
     * @return the warning message prefix (e.g., yellow styled)
     */
    public static String warning() {
        return prefixes[1];
    }

    /**
     * Returns the color-coded prefix used for severe/error messages.
     *
     * @return the severe message prefix (e.g., red styled)
     */
    public static String severe() {
        return prefixes[2];
    }

    public static void sendCommand(Player player, String message, String command) {
        Component first = Component.text(message, NamedTextColor.GREEN)
                .clickEvent(ClickEvent.suggestCommand(command));
        player.sendMessage(first);
    }
}
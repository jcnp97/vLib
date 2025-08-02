package asia.virtualmc.vLib.utilities.messages;

import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ActionBarUtils {

    /**
     * Sends an action bar message to the specified player using Adventure components.
     *
     * @param player  The player to send the message to.
     * @param message The message string to toComponent and send as an action bar.
     */
    public static void send(Player player, String message) {
        Component component = AdventureUtils.toComponent(message);
        player.sendActionBar(component);
    }

    /**
     * Sends a stylized action bar message to a specific player based on the provided {@link EnumsLib.MessageType}.
     * Each message includes a unique icon and color prefix.
     * Does nothing if the player is offline.
     *
     * @param player  The player to receive the message.
     * @param message The message to display in the action bar.
     * @param type    The message type (e.g., RED, GREEN, YELLOW) that determines icon and color.
     */
    public static void send(@NotNull Player player, String message, EnumsLib.MessageType type) {
        if (player.isOnline()) {
            switch (type) {
                case GREEN -> player.sendActionBar(AdventureUtils.toComponent(MessageUtils.info() + message));
                case YELLOW -> player.sendActionBar(AdventureUtils.toComponent(MessageUtils.warning() + message));
                case RED -> player.sendActionBar(AdventureUtils.toComponent(MessageUtils.severe() + message));
            }
        }
    }

    /**
     * Sends a stylized action bar message to all online players using the given {@link EnumsLib.MessageType}.
     * Each message includes a unique icon and color prefix.
     *
     * @param message The message to display to all players.
     * @param type    The message type (e.g., RED, GREEN, YELLOW) that determines icon and color.
     */
    public static void sendAll(String message, EnumsLib.MessageType type) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            switch (type) {
                case GREEN -> player.sendActionBar(AdventureUtils.toComponent(MessageUtils.info() + message));
                case YELLOW -> player.sendActionBar(AdventureUtils.toComponent(MessageUtils.warning() + message));
                case RED -> player.sendActionBar(AdventureUtils.toComponent(MessageUtils.severe() + message));
            }
        }
    }
}

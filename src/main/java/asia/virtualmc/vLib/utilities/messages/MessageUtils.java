package asia.virtualmc.vLib.utilities.messages;

import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageUtils {

    public static void sendMessage(@NotNull Player player, String message) {
        if (player.isOnline()) {
            player.sendMessage(AdventureUtils.convert("<#8BFFA9>" + message));
        }
    }

    public static void sendMessage(@NotNull Player player, String message, EnumsLib.MessageType type) {
        if (player.isOnline()) {
            switch (type) {
                case RED -> player.sendMessage(AdventureUtils.convert("<white>ꐩ <red>" + message));
                case GREEN -> player.sendMessage(AdventureUtils.convert("<white>ꐪ <green>" + message));
                case YELLOW -> player.sendMessage(AdventureUtils.convert("<white>ꐫ <gold>" + message));
            }
        }
    }

    public static void sendBroadcast(String message) {
        Bukkit.getServer().sendMessage(AdventureUtils.convert(message));
    }

    public static void sendBroadcast(String message, EnumsLib.MessageType type) {
        switch (type) {
            case RED -> Bukkit.getServer().sendMessage(AdventureUtils.convert("<white>ꐩ <red>" + message));
            case GREEN -> Bukkit.getServer().sendMessage(AdventureUtils.convert("<white>ꐪ <green>" + message));
            case YELLOW -> Bukkit.getServer().sendMessage(AdventureUtils.convert("<white>ꐫ <gold>" + message));
        }
    }
}
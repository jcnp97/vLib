package asia.virtualmc.vLib.utilities.player;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {

    public static boolean isOnline(UUID uuid) {
        if (uuid == null) return false;
        Player player = Bukkit.getPlayer(uuid);
        return player != null && player.isOnline();
    }

    public static UUID getOnlineUUID(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null && player.isOnline()) {
            return player.getUniqueId();
        }

        return null;
    }

    public static String getName(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return offlinePlayer.getName();
    }

    public static UUID getOfflineUUID(String playerName) {
        return Bukkit.getOfflinePlayer(playerName).getUniqueId();
    }

    public static UUID getUUID(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null && player.isOnline()) {
            return player.getUniqueId();
        }

        return Bukkit.getOfflinePlayer(playerName).getUniqueId();
    }
}

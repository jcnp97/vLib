package asia.virtualmc.vLib.utilities.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerUtils {

    public static boolean isOnline(UUID uuid) {
        if (uuid == null) return false;
        Player player = Bukkit.getPlayer(uuid);
        return player != null && player.isOnline();
    }
}

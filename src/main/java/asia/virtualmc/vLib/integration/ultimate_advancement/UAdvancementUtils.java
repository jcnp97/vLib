package asia.virtualmc.vLib.integration.ultimate_advancement;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.bukkit.SoundUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class UAdvancementUtils {
    private static UltimateAdvancementAPI uapi;

    /**
     * Initializes the UltimateAdvancementAPI integration by checking if the plugin is present
     * and acquiring its API instance. Logs success or failure accordingly.
     */
    public static void load() {
        Plugin plugin = Main.getInstance();
        if (plugin.getServer().getPluginManager().getPlugin("UltimateAdvancementAPI") == null) {
            ConsoleUtils.severe("UltimateAdvancementAPI not found. Disabling integration hooks..");
            return;
        }

        uapi = UltimateAdvancementAPI.getInstance(plugin);
        ConsoleUtils.info("Successfully hooked into: UltimateAdvancementAPI");
    }

    /**
     * Displays a custom toast (advancement popup) to the given player with the specified item,
     * message, and sound. Uses {@link AdvancementFrameType#GOAL} as the frame type.
     * <p>
     * If the integration is not loaded or the player is offline, nothing happens.
     *
     * @param player    the player to display the toast to
     * @param item      the icon/item shown in the toast
     * @param message   the message displayed in the toast
     * @param soundName the name of the sound to play alongside the toast
     */
    public static void sendToast(Player player, ItemStack item, String message, String soundName) {
        if (uapi == null) {
            ConsoleUtils.severe("Trying to use ultimate-advancement module but it is disabled!");
            return;
        }

        if (player == null || !player.isOnline()) {
            return;
        }

        uapi.displayCustomToast(player, item, message, AdvancementFrameType.GOAL);
        SoundUtils.play(player, soundName);
    }
}

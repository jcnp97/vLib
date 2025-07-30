//package asia.virtualmc.vLib.utilities.messages;
//
//import com.fren_gor.ultimateAdvancementAPI.UltimateAdvancementAPI;
//import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.bukkit.plugin.Plugin;
//import org.jetbrains.annotations.NotNull;
//
//public class ToastUtils {
//
//    public static void sendToastMessage(@NotNull Plugin plugin, @NotNull Player player, Material material, int modelData) {
//        ItemStack icon = new ItemStack(material);
//
//        ItemMeta meta = icon.getItemMeta();
//        if (meta != null) {
//            meta.setCustomModelData(modelData);
//            icon.setItemMeta(meta);
//        }
//
//        UltimateAdvancementAPI.getInstance(plugin).displayCustomToast(player, icon, "Collection Log Updated", AdvancementFrameType.GOAL);
//        SoundUtils.playSound(player, "minecraft:cozyvanilla.collection_log_updated");
//    }
//}

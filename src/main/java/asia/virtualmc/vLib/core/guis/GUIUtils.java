package asia.virtualmc.vLib.core.guis;

import asia.virtualmc.vLib.utilities.messages.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class GUIUtils {

    public static ItemStack createButton(Material material, String displayName, int modelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component name = AdventureUtils.toComponent("<!i>" + displayName);
            meta.displayName(name);
            meta.setCustomModelData(modelData);

            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createButton(ItemStack item, String displayName) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component name = AdventureUtils.toComponent("<!i>" + displayName);
            meta.displayName(name);

            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createButton(Material material, String displayName, int modelData, List<String> list) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component name = AdventureUtils.toComponent("<!i>" + displayName);
            List<Component> lore = AdventureUtils.toComponent(list);

            meta.displayName(name);
            meta.lore(lore);
            meta.setCustomModelData(modelData);

            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createButton(ItemStack item, String displayName, List<String> list) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component name = AdventureUtils.toComponent("<!i>" + displayName);
            List<Component> lore = AdventureUtils.toComponent(list);

            meta.displayName(name);
            meta.lore(lore);

            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack getPrevious() {
        return createButton(Material.ARROW, "<green>Previous Page", 1);
    }

    public static ItemStack getNext() {
        return createButton(Material.ARROW, "<green>Next Page", 1);
    }
}

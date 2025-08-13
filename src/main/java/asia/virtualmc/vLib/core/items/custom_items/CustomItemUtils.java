package asia.virtualmc.vLib.core.items.custom_items;

import asia.virtualmc.vLib.core.items.ItemCoreUtils;
import asia.virtualmc.vLib.utilities.items.EnchantUtils;
import asia.virtualmc.vLib.utilities.items.ItemStackUtils;
import asia.virtualmc.vLib.utilities.items.PDCUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CustomItemUtils {

    public static Map<String, ItemStack> get(@NotNull Plugin plugin, @NotNull YamlDocument yaml) {
        Map<String, ItemStack> itemCache = new LinkedHashMap<>();
        String prefix = "[" + plugin.getName() + "]";
        NamespacedKey itemID = new NamespacedKey(plugin, "id");

        Section section = yaml.getSection("items");
        if (section == null) {
            ConsoleUtils.severe(prefix, "No `items` section found from " + yaml.getNameAsString());
            return itemCache;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String key : keys) {
            Section itemSection = section.getSection(key);

            ItemCoreUtils.CustomItem customItem = ItemCoreUtils.get(itemSection);
            Map<String, Integer> intMap = ItemCoreUtils.getInt(itemSection);
            Map<String, Double> doubleMap = ItemCoreUtils.getDouble(itemSection);
            Map<String, int[]> intArrayMap = ItemCoreUtils.getIntArray(itemSection);
            Map<String, Integer> enchants = ItemCoreUtils.getEnchants(itemSection);
            Map<String, String> stringMap = ItemCoreUtils.getString(itemSection);

            // modify lore
            customItem.lore = ItemCoreUtils.modifyLore(customItem.lore, doubleMap, intMap);

            // item creation
            ItemStack item = ItemStackUtils.create(customItem.material, customItem.name,
                    customItem.lore, customItem.modelData);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                // Apply PDC data
                PDCUtils.addString(meta, itemID, key.toLowerCase());
                PDCUtils.addIntMap(plugin, meta, intMap);
                PDCUtils.addDoubleMap(plugin, meta, doubleMap);
                PDCUtils.addIntArrayMap(plugin, meta, intArrayMap);
                PDCUtils.addStringMap(plugin, meta, stringMap);

                // Apply Enchantments
                EnchantUtils.add(meta, enchants);
            }

            item.setItemMeta(meta);
            itemCache.put(key.toLowerCase(), item.clone());
        }

        return itemCache;
    }
}

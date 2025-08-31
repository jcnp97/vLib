package asia.virtualmc.vLib.core.items.custom_items;

import asia.virtualmc.vLib.core.items.ItemCoreUtils;
import asia.virtualmc.vLib.integration.more_pdc.MorePDCUtils;
import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.items.EnchantUtils;
import asia.virtualmc.vLib.utilities.items.ItemStackUtils;
import asia.virtualmc.vLib.utilities.items.PDCUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class CustomItemUtils {

    public static Map<String, ItemStack> get(Plugin plugin, String fileName) {
        Map<String, ItemStack> cache = new LinkedHashMap<>();
        if (plugin == null || fileName == null) return cache;

        String prefix = "[" + plugin.getName() + "]";
        NamespacedKey itemID = new NamespacedKey(plugin, "id");
        YamlFileService.YamlFile file = YamlFileService.get(plugin, fileName);

        Section section = file.getSection("items");
        if (section == null) {
            ConsoleUtils.severe(prefix, "No `items` section found from " + file.getYaml().getName());
            return cache;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String key : keys) {
            Section keySection = section.getSection(key);

            ItemCoreUtils.CustomItem customItem = ItemCoreUtils.get(keySection);
            Map<String, Integer> intMap = ItemCoreUtils.getInt(keySection);
            Map<String, Double> doubleMap = ItemCoreUtils.getDouble(keySection);
            //Map<String, int[]> intArrayMap = ItemCoreUtils.getIntArray(keySection);
            Map<String, Integer> enchants = ItemCoreUtils.getEnchants(keySection);
            Map<String, String> stringMap = ItemCoreUtils.getString(keySection);
            Map<String, Long> longMap = ItemCoreUtils.getLong(keySection);
            Map<String, Set<String>> stringSet = ItemCoreUtils.getSetString(keySection);

            // modify lore
            customItem.lore = ItemCoreUtils.modifyLore(customItem.lore, doubleMap, intMap);

            // item creation
            ItemStack item = ItemStackUtils.create(customItem.material, customItem.name,
                    customItem.lore, customItem.modelData);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                // Apply PDC data
                try {
                    PDCUtils.addString(meta, itemID, key.toLowerCase());
                    PDCUtils.addIntMap(plugin, meta, intMap);
                    PDCUtils.addDoubleMap(plugin, meta, doubleMap);
                    PDCUtils.addLongMap(plugin, meta, longMap);
                    PDCUtils.addStringMap(plugin, meta, stringMap);
                    MorePDCUtils.addStringSet(plugin, meta, stringSet);

                    // Apply Enchantments
                    EnchantUtils.add(meta, enchants);
                } catch (Exception e) {
                    ConsoleUtils.severe(prefix, "An error occurred when trying to add PDC data to " + item.displayName());
                }
            }

            // Apply ItemMeta
            item.setItemMeta(meta);

            // Check for Data Components and Apply
            item = ItemCoreUtils.getDataComponent(plugin, keySection, item, key);

            cache.put(key.toLowerCase(), item.clone());
        }

        // Generate Models
        //generateModels(prefix, file);

        return cache;
    }

    private static void generateModels(String prefix, YamlFileService.YamlFile file) {
        Section section = file.getSection("model-generation");
        if (section == null) {
            ConsoleUtils.severe(prefix, "Section model-generation is not found! Skipping..");
            return;
        }

        boolean isEnable = section.getBoolean("enable");
        if (!isEnable) return;

        String format = section.getString("format");
        String modelPath = section.getString("model-path");
        String texturePath = section.getString("texture-path");
        String generatedPath = section.getString("generated-path");
    }
}

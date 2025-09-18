package asia.virtualmc.vLib.core.items.custom_items;

import asia.virtualmc.vLib.core.items.ItemCoreUtils;
import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.items.ItemStackUtils;
import asia.virtualmc.vLib.utilities.items.MaterialUtils;
import asia.virtualmc.vLib.utilities.items.PDCUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CustomResourceUtils {

    public static Map<String, ItemStack> get(Plugin plugin, String dirPath, List<String> rarityNames) {
        Map<String, ItemStack> cache = new LinkedHashMap<>();
        if (plugin == null || rarityNames == null || rarityNames.isEmpty() || dirPath == null) return cache;

        String prefix = "[" + plugin.getName() + "]";
        NamespacedKey itemID = new NamespacedKey(plugin, "id");

        // Retrieve directory where items are read.
        for (String rarityName : rarityNames) {
            String fileName = rarityName + ".yml";
            YamlFileService.YamlFile file = YamlFileService.get(plugin, dirPath + "/" + fileName);

            Section section = file.getSection("items");
            if (section == null) {
                ConsoleUtils.severe(prefix, "No `items` section found! Skipping " + fileName);
                continue;
            }

            Set<String> keys = section.getRoutesAsStrings(false);
            for (String key : keys) {
                Section keySection = section.getSection(key);

                String name = keySection.getString("name");
                Material material = MaterialUtils.getMaterial(keySection.getString("material"));
                List<String> lore = keySection.getStringList("lore");

                Map<String, Integer> intMap = ItemCoreUtils.getInt(keySection);
                Map<String, Double> doubleMap = ItemCoreUtils.getDouble(keySection);
                Map<String, String> stringMap = ItemCoreUtils.getString(keySection);

                // item creation
                int rarityId = intMap.get("rarity_id");
                ItemStack item = ItemStackUtils.create(material, name, lore, 0);
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    // Apply PDC data
                    try {
                        // add id:<keyName>
                        PDCUtils.addString(meta, itemID, key.toLowerCase() + "_" + rarityId);
                        PDCUtils.addIntMap(plugin, meta, intMap);
                        PDCUtils.addDoubleMap(plugin, meta, doubleMap);
                        PDCUtils.addStringMap(plugin, meta, stringMap);
                    } catch (Exception e) {
                        ConsoleUtils.severe(prefix, "An error occurred when trying to add PDC data to " + item.displayName());
                        continue;
                    }
                }

                // Apply ItemMeta
                item.setItemMeta(meta);

                // Check for Data Components and Apply
                item = ItemCoreUtils.getDataComponent(plugin, keySection, item, key);

                cache.put(key.toLowerCase() + "_" + rarityId, item.clone());
            }
        }

        // Generate Models
        //generateModels(prefix, settings);

        return cache;
    }

    private static void generateModels(String prefix, Section section) {
        Section modelSection = section.getSection("model-generation");
        boolean isEnable = modelSection.getBoolean("enable");
        if (!isEnable) return;

        String format = modelSection.getString("format");
        String modelPath = modelSection.getString("model-path");
        String texturePath = modelSection.getString("texture-path");
        String generatedPath = modelSection.getString("generated-path");
    }
}

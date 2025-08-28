package asia.virtualmc.vLib.core.items.custom_items;

import asia.virtualmc.vLib.core.items.ItemCoreUtils;
import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.items.ItemStackUtils;
import asia.virtualmc.vLib.utilities.items.MaterialUtils;
import asia.virtualmc.vLib.utilities.items.PDCUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.text.StringListUtils;
import asia.virtualmc.vLib.utilities.text.StringUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class CustomResourceUtils {

    public static class Resources {
        public Map<String, ItemStack> items;
        public Map<String, Integer> weight;
        public Map<String, Double> exp;
        public Map<String, Double> sell;

        public Resources(Map<String, ItemStack> items, Map<String, Integer> weight,
                         Map<String, Double> exp, Map<String, Double> sell) {
            this.items = items;
            this.weight = weight;
            this.exp = exp;
            this.sell = sell;
        }
    }

    public static Resources get(Plugin plugin, String settingsPath, String itemsPath) {
        Map<String, ItemStack> cache = new LinkedHashMap<>();
        if (plugin == null || settingsPath == null || itemsPath == null) return null;

        String prefix = "[" + plugin.getName() + "]";
        NamespacedKey itemID = new NamespacedKey(plugin, "id");

        // Retrieve settings.yml first.
        YamlFileService.YamlFile settingsFile = YamlFileService.get(plugin, settingsPath);
        Section settings = settingsFile.getSection("settings");
        if (settings == null) {
            ConsoleUtils.severe(prefix, "No `settings` section found from " + settingsFile.getYaml().getName());
            return null;
        }

        Material material = MaterialUtils.getMaterial(settings.getString("material"));
        Map<String, String> rarities = settingsFile.stringKeyStringMap(settings.getSection("rarities"), false);
        Map<String, String> tags = settingsFile.stringKeyStringMap(settings.getSection("rarity-tags"), false);
        Map<String, String> colors = settingsFile.stringKeyStringMap(settings.getSection("rarity-color"), false);
        Map<String, Double> exp = settingsFile.stringKeyDoubleMap(settings.getSection("exp"), false);
        Map<String, Integer> weight = settingsFile.stringKeyIntMap(settings.getSection("weight"), false);
        Map<String, Double> sellPrice = settingsFile.stringKeyDoubleMap(settings.getSection("sell-price"), false);

        // Retrieve directory where items are read.
        for (String rarityName : rarities.keySet()) {
            YamlFileService.YamlFile file = YamlFileService.get(plugin, itemsPath + "/" + rarityName + ".yml");

            Section section = file.getSection("items");
            if (section == null) {
                ConsoleUtils.severe(prefix, "No `items` section found! Skipping " + rarityName + ".yml");
                continue;
            }

            Set<String> keys = section.getRoutesAsStrings(false);
            for (String key : keys) {
                Section keySection = section.getSection(key);

                String name = keySection.getString("name");
                List<String> lore = keySection.getStringList("lore");

                Map<String, Integer> intMap = ItemCoreUtils.getInt(keySection);
                Map<String, Double> doubleMap = ItemCoreUtils.getDouble(keySection);
                Map<String, String> stringMap = ItemCoreUtils.getString(keySection);

                // modify name & lore
                name = StringUtils.replace(name, "{rarity_color}", colors.get(rarityName));
                lore = StringListUtils.replace(lore, "{rarity_tag}", tags.get(rarityName));

                // item creation
                int rarityId = intMap.get("rarity_id");
                ItemStack item = ItemStackUtils.create(material, name,
                        lore, 0);
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
        generateModels(prefix, settings);

        return new Resources(cache, weight, exp, sellPrice);
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

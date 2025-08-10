package asia.virtualmc.vLib.core.utilities;

import asia.virtualmc.vLib.utilities.items.ItemStackUtils;
import asia.virtualmc.vLib.utilities.java.ArrayUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemCoreUtils {


    public static class CustomItem {
        public String name;
        public Material material;
        public int modelData;
        public List<String> lore;

        /**
         * Constructs a CustomItem with name, material, model data, and lore.
         *
         * @param name display name
         * @param material item material
         * @param modelData custom model data value
         * @param lore lore lines
         */
        public CustomItem(String name, Material material, int modelData, List<String> lore) {
            this.name = name;
            this.material = material;
            this.modelData = modelData;
            this.lore = lore;
        }
    }

    /**
     * Builds a CustomItem from the given YAML Section.
     *
     * @param section the section containing "name", "material", "custom-model-data", and "lore"
     * @return a populated CustomItem or null on error
     */
    public static CustomItem get(@NotNull Section section) {
        try {
            String name = section.getString("name");
            Material material = ItemStackUtils.getMaterial(section.getString("material"));
            int modelData = section.getInt("custom-model-data");
            List<String> lore = section.getStringList("lore");
            return new CustomItem(name, material, modelData, lore);
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to retrieve CustomItem from section=" + section.getRoute() + ": " + e);
            return new CustomItem("", Material.PAPER, 1, new ArrayList<>());
        }
    }

    /**
     * Reads integer key/value pairs under "item-data.integer".
     *
     * @param section the base section
     * @return map of keys to integers; empty if none
     */
    public static Map<String, Integer> getInt(Section section) {
        Map<String, Integer> data = new HashMap<>();
        Section itemData = section.getSection("item-data.integer");
        if (itemData != null) {
            Set<String> keys = itemData.getRoutesAsStrings(false);
            for (String key : keys) {
                int value = itemData.getInt(key);
                data.put(key, value);
            }
        }

        return data;
    }

    /**
     * Reads double key/value pairs under "item-data.double".
     *
     * @param section the base section
     * @return map of keys to doubles; empty if none
     */
    public static Map<String, Double> getDouble(Section section) {
        Map<String, Double> data = new HashMap<>();
        Section itemData = section.getSection("item-data.double");
        if (itemData != null) {
            Set<String> keys = itemData.getRoutesAsStrings(false);
            for (String key : keys) {
                double value = itemData.getDouble(key);
                data.put(key, value);
            }
        }

        return data;
    }

    /**
     * Reads string key/value pairs under "item-data.string".
     *
     * @param section the base section
     * @return map of keys to strings; empty if none
     */
    public static Map<String, String> getString(Section section) {
        Map<String, String> data = new HashMap<>();
        Section itemData = section.getSection("item-data.string");
        if (itemData != null) {
            Set<String> keys = itemData.getRoutesAsStrings(false);
            for (String key : keys) {
                String value = itemData.getString(key);
                data.put(key, value);
            }
        }

        return data;
    }

    /**
     * Reads int-array entries under "item-data.array" (values as strings) and converts via ArrayUtils.toIntArray.
     *
     * @param section the base section
     * @return map of keys to int[]; empty if none
     */
    public static Map<String, int[]> getIntArray(Section section) {
        Map<String, int[]> data = new HashMap<>();
        Section itemData = section.getSection("item-data.array");
        if (itemData != null) {
            Set<String> keys = itemData.getRoutesAsStrings(false);
            for (String key : keys) {
                String value = itemData.getString(key);
                int[] array = ArrayUtils.toIntArray(value);
                data.put(key, array);
            }
        }

        return data;
    }

    /**
     * Retrieves enchantments and their levels from the "enchants" section as a map of names to integers.
     *
     * @param section the base YAML section containing the enchant data
     * @return a map of enchantment names to their levels; empty if none found
     */
    public static Map<String, Integer> getEnchants(Section section) {
        Map<String, Integer> data = new HashMap<>();
        Section itemData = section.getSection("enchants");
        if (itemData != null) {
            Set<String> keys = itemData.getRoutesAsStrings(false);
            for (String key : keys) {
                int level = itemData.getInt(key);
                data.put(key, level);
            }
        }

        return data;
    }

    /**
     * Replaces placeholders like "{key}" in lore with values from intMap then doubleMap.
     *
     * @param lore template lore lines
     * @param doubleMap placeholder -> double values
     * @param intMap placeholder -> int values
     * @return new lore list with placeholders resolved
     */
    public static List<String> modifyLore(List<String> lore, Map<String, Double> doubleMap, Map<String, Integer> intMap) {
        List<String> newLore = new ArrayList<>();

        for (String line : lore) {
            String processedLine = line;

            for (Map.Entry<String, Integer> entry : intMap.entrySet()) {
                processedLine = processedLine.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }

            for (Map.Entry<String, Double> entry : doubleMap.entrySet()) {
                processedLine = processedLine.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }

            newLore.add(processedLine);
        }

        return newLore;
    }
}

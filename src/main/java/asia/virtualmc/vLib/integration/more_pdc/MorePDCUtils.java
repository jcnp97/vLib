package asia.virtualmc.vLib.integration.more_pdc;

import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MorePDCUtils {

    /**
     * Adds a set of strings to the PersistentDataContainer of the given ItemMeta.
     *
     * @param meta ItemMeta to apply the data to.
     * @param key  NamespacedKey used to store the data.
     * @param set  Set of strings to store.
     */
    public static void addStringSet(@NotNull ItemMeta meta, NamespacedKey key, Set<String> set) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (set == null || set.isEmpty()) {
            return;
        }

        pdc.remove(key);
        pdc.set(key, DataType.asSet(DataType.STRING), set);
    }

    /**
     * Adds multiple string sets to the PersistentDataContainer of the given ItemMeta,
     * using the provided map where keys are converted into NamespacedKeys.
     *
     * @param plugin Plugin instance used for creating NamespacedKeys.
     * @param meta   ItemMeta to apply the data to.
     * @param map    Map of string keys and corresponding sets of strings to store.
     */
    public static void addStringSet(@NotNull Plugin plugin, @NotNull ItemMeta meta, Map<String, Set<String>> map) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (map == null || map.isEmpty()) {
            return;
        }

        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
            pdc.remove(key);
            pdc.set(key, DataType.asSet(DataType.STRING), entry.getValue());
        }
    }

    /**
     * Adds a map of string-to-string pairs to the PersistentDataContainer.
     *
     * @param meta ItemMeta to apply the data to.
     * @param key  NamespacedKey used to store the map.
     * @param map  Map of string key-value pairs to store.
     */
    public static void addStringMap(@NotNull ItemMeta meta, NamespacedKey key, Map<String, String> map) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (map == null || map.isEmpty()) {
            return;
        }

        pdc.remove(key);
        pdc.set(key, DataType.asMap(DataType.STRING, DataType.STRING), map);
    }

    /**
     * Adds a map of string-to-integer pairs to the PersistentDataContainer.
     *
     * @param meta ItemMeta to apply the data to.
     * @param key  NamespacedKey used to store the map.
     * @param map  Map of string keys with integer values to store.
     */
    public static void addStringIntMap(@NotNull ItemMeta meta, NamespacedKey key, Map<String, Integer> map) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (map == null || map.isEmpty()) {
            return;
        }

        pdc.remove(key);
        pdc.set(key, DataType.asMap(DataType.STRING, DataType.INTEGER), map);
    }

    /**
     * Adds a map of string-to-double pairs to the PersistentDataContainer.
     *
     * @param meta ItemMeta to apply the data to.
     * @param key  NamespacedKey used to store the map.
     * @param map  Map of string keys with double values to store.
     */
    public static void addStringDoubleMap(@NotNull ItemMeta meta, NamespacedKey key, Map<String, Double> map) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (map == null || map.isEmpty()) {
            return;
        }

        pdc.remove(key);
        pdc.set(key, DataType.asMap(DataType.STRING, DataType.DOUBLE), map);
    }

    /**
     * Adds a map of integer-to-integer pairs to the PersistentDataContainer.
     *
     * @param meta ItemMeta to apply the data to.
     * @param key  NamespacedKey used to store the map.
     * @param map  Map of integer keys with integer values to store.
     */
    public static void addIntMap(@NotNull ItemMeta meta, NamespacedKey key, Map<Integer, Integer> map) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (map == null || map.isEmpty()) {
            return;
        }

        pdc.remove(key);
        pdc.set(key, DataType.asMap(DataType.INTEGER, DataType.INTEGER), map);
    }

    /**
     * Adds a map of integer-to-string pairs to the PersistentDataContainer.
     *
     * @param meta ItemMeta to apply the data to.
     * @param key  NamespacedKey used to store the map.
     * @param map  Map of integer keys with string values to store.
     */
    public static void addIntStringMap(@NotNull ItemMeta meta, NamespacedKey key, Map<Integer, String> map) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (map == null || map.isEmpty()) {
            return;
        }

        pdc.remove(key);
        pdc.set(key, DataType.asMap(DataType.INTEGER, DataType.STRING), map);
    }

    /**
     * Adds a map of integer-to-double pairs to the PersistentDataContainer.
     *
     * @param meta ItemMeta to apply the data to.
     * @param key  NamespacedKey used to store the map.
     * @param map  Map of integer keys with double values to store.
     */
    public static void addIntDoubleMap(@NotNull ItemMeta meta, NamespacedKey key, Map<Integer, Double> map) {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (map == null || map.isEmpty()) {
            return;
        }

        pdc.remove(key);
        pdc.set(key, DataType.asMap(DataType.INTEGER, DataType.DOUBLE), map);
    }

    /**
     * Retrieves a string value from a stored string-to-string map in the PersistentDataContainer.
     *
     * @param item   ItemStack to check for data.
     * @param key    NamespacedKey used to retrieve the map.
     * @param mapKey Key inside the stored map to get the value for.
     * @return The string value, or an empty string if not found.
     */
    public static String getString(ItemStack item, NamespacedKey key, String mapKey) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return "";
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        Map<String, String> data = pdc.get(key, DataType.asMap(DataType.STRING, DataType.STRING));
        if (data != null && !data.isEmpty()) {
            return data.getOrDefault(mapKey, "");
        }

        return "";
    }

    /**
     * Retrieves an integer value from a stored string-to-integer map in the PersistentDataContainer.
     *
     * @param item   ItemStack to check for data.
     * @param key    NamespacedKey used to retrieve the map.
     * @param mapKey Key inside the stored map to get the value for.
     * @return The integer value, or 0 if not found.
     */
    public static int getInt(ItemStack item, NamespacedKey key, String mapKey) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        Map<String, Integer> data = pdc.get(key, DataType.asMap(DataType.STRING, DataType.INTEGER));
        if (data != null && !data.isEmpty()) {
            return data.getOrDefault(mapKey, 0);
        }

        return 0;
    }

    /**
     * Retrieves a double value from a stored string-to-double map in the PersistentDataContainer.
     *
     * @param item   ItemStack to check for data.
     * @param key    NamespacedKey used to retrieve the map.
     * @param mapKey Key inside the stored map to get the value for.
     * @return The double value, or 0.0 if not found.
     */
    public static double getDouble(ItemStack item, NamespacedKey key, String mapKey) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        Map<String, Double> data = pdc.get(key, DataType.asMap(DataType.STRING, DataType.DOUBLE));
        if (data != null && !data.isEmpty()) {
            return data.getOrDefault(mapKey, 0.0);
        }

        return 0;
    }

    /**
     * Checks if a given string exists in a stored string set in the PersistentDataContainer.
     *
     * @param item   ItemStack to check for data.
     * @param key    NamespacedKey used to retrieve the set.
     * @param setKey String value to check existence of.
     * @return True if the string exists in the set, false otherwise.
     */
    public static boolean hasStringFromSet(ItemStack item, NamespacedKey key, String setKey) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        Set<String> data = pdc.get(key, DataType.asSet(DataType.STRING));
        if (data != null && !data.isEmpty()) {
            return data.contains(setKey);
        }

        return false;
    }

    /**
     * Retrieves a set of strings stored in the PersistentDataContainer of the given ItemStack.
     *
     * @param item ItemStack to check for stored data.
     * @param key  NamespacedKey used to retrieve the set.
     * @return The stored set of strings, or an empty set if none exists or the item is invalid.
     */
    public static Set<String> getStringSet(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return new HashSet<>();
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        Set<String> data = pdc.get(key, DataType.asSet(DataType.STRING));
        if (data != null && !data.isEmpty()) {
            return data;
        }

        return new HashSet<>();
    }
}

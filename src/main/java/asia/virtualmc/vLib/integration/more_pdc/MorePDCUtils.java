package asia.virtualmc.vLib.integration.more_pdc;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MorePDCUtils {

    /**
     * Retrieves an integer value from a stored PDC map using the given key and map key.
     *
     * @param item    the {@link ItemStack} to read from
     * @param key     the {@link NamespacedKey} for the PDC entry
     * @param mapKey  the string key inside the stored map
     * @return the integer value if found, otherwise 0
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
     * Retrieves a double value from a stored PDC map using the given key and map key.
     *
     * @param item    the {@link ItemStack} to read from
     * @param key     the {@link NamespacedKey} for the PDC entry
     * @param mapKey  the string key inside the stored map
     * @return the double value if found, otherwise 0.0
     */
    public static double getDouble(ItemStack item, NamespacedKey key, String mapKey) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0.0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        Map<String, Double> data = pdc.get(key, DataType.asMap(DataType.STRING, DataType.DOUBLE));
        if (data != null && !data.isEmpty()) {
            return data.getOrDefault(mapKey, 0.0);
        }

        return 0.0;
    }

    /**
     * Retrieves a string value from a stored PDC map using the given key and map key.
     *
     * @param item    the {@link ItemStack} to read from
     * @param key     the {@link NamespacedKey} for the PDC entry
     * @param mapKey  the string key inside the stored map
     * @return the string value if found, otherwise an empty string
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
     * Retrieves a {@link Set} of strings stored in the item's PersistentDataContainer
     * under the specified key.
     *
     * @param item the item stack to read from
     * @param key  the namespaced key of the stored data
     * @return the set of strings found, or an empty set if none exist
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

    /**
     * Stores a map of string keys and integer values into an item's PDC.
     *
     * @param key  the {@link NamespacedKey} for the PDC entry
     * @param meta the {@link ItemMeta} to store the map in
     * @param map  the map of string keys and integer values to store
     */
    public static void addIntMap(ItemMeta meta, NamespacedKey key, Map<String, Integer> map) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, DataType.asMap(DataType.STRING, DataType.INTEGER), map);
    }

    /**
     * Stores a map of string keys and double values into an item's PDC.
     *
     * @param key  the {@link NamespacedKey} for the PDC entry
     * @param meta the {@link ItemMeta} to store the map in
     * @param map  the map of string keys and double values to store
     */
    public static void addDoubleMap(ItemMeta meta, NamespacedKey key, Map<String, Double> map) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, DataType.asMap(DataType.STRING, DataType.DOUBLE), map);
    }

    /**
     * Stores a map of string keys and string values into an item's PDC.
     *
     * @param key  the {@link NamespacedKey} for the PDC entry
     * @param meta the {@link ItemMeta} to store the map in
     * @param map  the map of string keys and string values to store
     */
    public static void addStringMap(ItemMeta meta, NamespacedKey key, Map<String, String> map) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, DataType.asMap(DataType.STRING, DataType.STRING), map);
    }

    /**
     * Overwrites the {@link Set} of strings in the item's PersistentDataContainer under the specified key.
     * If {@code set} is null or empty, the key is removed.
     *
     * @param key  the namespaced key to store the data under
     * @param meta the item meta to modify (must not be {@code null})
     * @param set  the set of strings to store
     */
    public static void addStringSet(ItemMeta meta, NamespacedKey key, Set<String> set) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data because ItemMeta is null.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.remove(key);
        if (set == null || set.isEmpty()) {
            return;
        }

        pdc.set(key, DataType.asSet(DataType.STRING), set);
    }

    /**
     * Adds a string value to a {@link Set} stored in an item's PersistentDataContainer (PDC).
     * Creates a new set if one does not exist for the given key.
     *
     * @param meta  the {@link ItemMeta} containing the PDC
     * @param key   the {@link NamespacedKey} for the PDC entry
     * @param value the string value to add to the set
     */
    public static boolean addStringToSet(ItemMeta meta, NamespacedKey key, String value) {
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Set<String> set = pdc.get(key, DataType.asSet(DataType.STRING));

        if (set == null) set = new HashSet<>();
        try {
            set.add(value);
            pdc.set(key, DataType.asSet(DataType.STRING), set);
            return true;
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to add a string value into PDC map of " + meta.getDisplayName() + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Adds or updates a key-value pair in a {@link Map} stored in an item's PersistentDataContainer (PDC).
     * Creates a new map if one does not exist for the given key.
     *
     * @param meta   the {@link ItemMeta} containing the PDC
     * @param key    the {@link NamespacedKey} for the PDC entry
     * @param mapKey the key inside the stored map
     * @param value  the value to associate with the map key
     */
    public static boolean addStringToMap(ItemMeta meta, NamespacedKey key, String mapKey, String value) {
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Map<String, String> map = pdc.get(key, DataType.asMap(DataType.STRING, DataType.STRING));

        if (map == null) map = new java.util.HashMap<>();
        try {
            map.put(mapKey, value);
            pdc.set(key, DataType.asMap(DataType.STRING, DataType.STRING), map);
            return true;
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to add a string value into PDC map of " + meta.getDisplayName() + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Removes a string value from a {@link Set} stored in an item's PersistentDataContainer (PDC).
     *
     * @param meta  the {@link ItemMeta} containing the PDC
     * @param key   the {@link NamespacedKey} for the PDC entry
     * @param value the string value to remove from the set
     * @return true if the value was successfully removed, false otherwise
     */
    public static boolean removeStringFromSet(ItemMeta meta, NamespacedKey key, String value) {
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Set<String> set = pdc.get(key, DataType.asSet(DataType.STRING));

        if (set == null || !set.contains(value)) return false;
        try {
            set.remove(value);
            pdc.set(key, DataType.asSet(DataType.STRING), set);
            return true;
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to remove a string value from PDC set of " + meta.getDisplayName() + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Removes an entry from a {@link Map} stored in an item's PersistentDataContainer (PDC) by its key.
     *
     * @param meta   the {@link ItemMeta} containing the PDC
     * @param key    the {@link NamespacedKey} for the PDC entry
     * @param mapKey the key inside the stored map to remove
     * @return true if the key-value pair was successfully removed, false otherwise
     */
    public static boolean removeStringFromMap(ItemMeta meta, NamespacedKey key, String mapKey) {
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        Map<String, String> map = pdc.get(key, DataType.asMap(DataType.STRING, DataType.STRING));

        if (map == null || !map.containsKey(mapKey)) return false;
        try {
            map.remove(mapKey);
            pdc.set(key, DataType.asMap(DataType.STRING, DataType.STRING), map);
            return true;
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to remove a key-value from PDC map of " + meta.getDisplayName() + ": " + e.getMessage());
        }
        return false;
    }
}

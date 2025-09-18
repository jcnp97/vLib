package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PDCUtils {

    /**
     * Retrieves the {@link PersistentDataContainer} from the provided ItemStack.
     *
     * @param item the item to retrieve the PDC from
     * @return the persistent data container, or null if the item has no meta
     */
    public static PersistentDataContainer get(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        return meta != null ? meta.getPersistentDataContainer() : null;
    }

    /**
     * Checks if the specified {@link NamespacedKey} exists in the item's PDC.
     *
     * @param item the item to check
     * @param key  the key to check for
     * @return true if the key exists, false otherwise
     */
    public static boolean has(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key);
    }

    /**
     * Retrieves an integer value from the item's PDC using the specified key.
     * Returns 0 if the value is not found or invalid.
     *
     * @param item the item to retrieve the value from
     * @param key  the key associated with the value
     * @return the integer value or 0 if not present
     */
    public static int getInt(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(key, PersistentDataType.INTEGER, 0);
    }

    /**
     * Retrieves a double value from the item's PDC using the specified key.
     * Returns 0.0 if the value is not found or invalid.
     *
     * @param item the item to retrieve the value from
     * @param key  the key associated with the value
     * @return the double value or 0.0 if not present
     */
    public static double getDouble(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0.0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(key, PersistentDataType.DOUBLE, 0.0);
    }

    /**
     * Retrieves a stored long value from the {@link PersistentDataContainer} of the given {@link ItemStack}.
     * <p>
     * If the item is air or has no item meta, this method will return {@code 0}.
     *
     * @param item the {@link ItemStack} to read the data from (must not be null)
     * @param key  the {@link NamespacedKey} used to access the stored value
     * @return the stored long value, or {@code 0} if not present
     */
    public static long getLong(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return 0;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(key, PersistentDataType.LONG, (long) 0);
    }

    /**
     * Retrieves a string value from the item's PDC using the specified key.
     * Returns an empty string if the value is not found or invalid.
     *
     * @param item the item to retrieve the value from
     * @param key  the key associated with the value
     * @return the string value or empty string if not present
     */
    public static String getString(ItemStack item, NamespacedKey key) {
        if (item == null || item.getType() == Material.AIR || !item.hasItemMeta()) return "";
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.getOrDefault(key, PersistentDataType.STRING, "");
    }

    /**
     * Adds or updates an integer value in the given {@link ItemMeta}'s PDC.
     *
     * @param meta  the item meta to update
     * @param key   the key to store the value under
     * @param value the integer value to store
     */
    public static void addInt(ItemMeta meta, NamespacedKey key, int value) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.INTEGER, value);
    }

    /**
     * Adds or updates a double value in the given {@link ItemMeta}'s PDC.
     *
     * @param meta  the item meta to update
     * @param key   the key to store the value under
     * @param value the double value to store
     */
    public static void addDouble(ItemMeta meta, NamespacedKey key, double value) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.DOUBLE, value);
    }

    /**
     * Adds or updates a string value in the given {@link ItemMeta}'s PDC.
     *
     * @param meta  the item meta to update
     * @param key   the key to store the value under
     * @param value the string value to store
     */
    public static void addString(ItemMeta meta, NamespacedKey key, String value) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.STRING, value);
    }

    /**
     * Adds a map of integer values to an item's PersistentDataContainer (PDC).
     *
     * @param plugin the plugin instance used to create the {@link NamespacedKey}
     * @param meta the {@link ItemMeta} to store the data in
     * @param map the map of string keys and integer values to store
     */
    public static void addIntMap(@NotNull Plugin plugin, @NotNull ItemMeta meta, Map<String, Integer> map) {
        if (map.isEmpty()) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
            pdc.set(key, PersistentDataType.INTEGER, entry.getValue());
        }
    }

    /**
     * Adds a map of double values to an item's PersistentDataContainer (PDC).
     *
     * @param plugin the plugin instance used to create the {@link NamespacedKey}
     * @param meta the {@link ItemMeta} to store the data in
     * @param map the map of string keys and double values to store
     */
    public static void addDoubleMap(@NotNull Plugin plugin, @NotNull ItemMeta meta, Map<String, Double> map) {
        if (map.isEmpty()) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
            pdc.set(key, PersistentDataType.DOUBLE, entry.getValue());
        }
    }

    /**
     * Stores a map of {@link String} keys and {@link Long} values into the {@link PersistentDataContainer}
     * of the given {@link ItemMeta}.
     * <p>
     * Each map entry is converted into a {@link NamespacedKey} using the provided plugin as the namespace.
     * If the map is empty, this method does nothing.
     *
     * @param plugin the plugin instance used to create {@link NamespacedKey}s (must not be null)
     * @param meta   the {@link ItemMeta} where data will be stored (must not be null)
     * @param map    a map of string keys and long values to store
     */
    public static void addLongMap(@NotNull Plugin plugin, @NotNull ItemMeta meta, Map<String, Long> map) {
        if (map.isEmpty()) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
            pdc.set(key, PersistentDataType.LONG, entry.getValue());
        }
    }

    /**
     * Adds a map of string values to an item's PersistentDataContainer (PDC).
     *
     * @param plugin the plugin instance used to create the {@link NamespacedKey}
     * @param meta the {@link ItemMeta} to store the data in
     * @param map the map of string keys and string values to store
     */
    public static void addStringMap(@NotNull Plugin plugin, @NotNull ItemMeta meta, Map<String, String> map) {
        if (map.isEmpty()) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
            pdc.set(key, PersistentDataType.STRING, entry.getValue());
        }
    }

    /**
     * Adds a map of string keys and integer array values to an item's PersistentDataContainer (PDC).
     *
     * @param plugin the plugin instance used to create the {@link NamespacedKey}
     * @param meta   the {@link ItemMeta} to store the data in
     * @param map    the map of string keys and integer array values to store
     */
    public static void addIntArrayMap(@NotNull Plugin plugin, @NotNull ItemMeta meta, Map<String, int[]> map) {
        if (map.isEmpty()) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (Map.Entry<String, int[]> entry : map.entrySet()) {
            NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
            pdc.set(key, PersistentDataType.INTEGER_ARRAY, entry.getValue());
        }
    }
}

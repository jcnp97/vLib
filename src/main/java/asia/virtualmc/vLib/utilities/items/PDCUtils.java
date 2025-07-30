package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PDCUtils {

    /**
     * Retrieves the {@link PersistentDataContainer} from the provided ItemStack.
     *
     * @param item the item to retrieve the PDC from
     * @return the persistent data container, or null if the item has no meta
     */
    public static PersistentDataContainer get(ItemStack item) {
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            return meta.getPersistentDataContainer();
        }

        return null;
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
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        return pdc.has(key);
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
}

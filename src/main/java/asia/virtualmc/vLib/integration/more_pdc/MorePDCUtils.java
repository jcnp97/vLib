package asia.virtualmc.vLib.integration.more_pdc;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Map;

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
     * Stores a map of string keys and integer values into an item's PDC.
     *
     * @param key  the {@link NamespacedKey} for the PDC entry
     * @param meta the {@link ItemMeta} to store the map in
     * @param map  the map of string keys and integer values to store
     */
    public static void addIntMap(NamespacedKey key, ItemMeta meta, Map<String, Integer> map) {
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
    public static void addDoubleMap(NamespacedKey key, ItemMeta meta, Map<String, Double> map) {
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
    public static void addStringMap(NamespacedKey key, ItemMeta meta, Map<String, String> map) {
        if (meta == null) {
            ConsoleUtils.severe("Unable to add PDC data on " + meta.getDisplayName() + " because meta is NULL.");
            return;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, DataType.asMap(DataType.STRING, DataType.STRING), map);
    }
}

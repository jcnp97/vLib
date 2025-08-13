package asia.virtualmc.vLib.utilities.bukkit;

import asia.virtualmc.vLib.utilities.items.PDCUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

public class InventoryUtils {

    /**
     * Creates a snapshot of all items in a player's inventory that contain the specified PDC key.
     *
     * @param player the player whose inventory is checked
     * @param key    the PDC key to filter items by
     * @return a map of inventory slot index to cloned matching ItemStack
     */
    public static Map<Integer, ItemStack> getSnapshot(@NotNull Player player,
                                                      @NotNull NamespacedKey key) {
        Map<Integer, ItemStack> snapshot = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !item.hasItemMeta()) continue;

            if (PDCUtils.has(item, key)) {
                snapshot.put(i, item.clone());
            }
        }

        return snapshot;
    }

    /**
     * Creates a snapshot of items in a player's inventory that contain a specific integer value under the given PDC key.
     *
     * @param player the player whose inventory is checked
     * @param key    the PDC key to read integer values from
     * @param ids    the set of allowed integer IDs
     * @return a map of slot index to cloned ItemStacks matching the filter
     */
    public static Map<Integer, ItemStack> getSnapshot(@NotNull Player player,
                                                      @NotNull NamespacedKey key,
                                                      @NotNull Set<Integer> ids) {
        Map<Integer, ItemStack> snapshot = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !item.hasItemMeta()) continue;

            if (ids.contains(PDCUtils.getInt(item, key))) {
                snapshot.put(i, item.clone());
            }
        }

        return snapshot;
    }

    /**
     * Creates a snapshot of items in a player's inventory that contain a specific string value under the given PDC key.
     *
     * @param player the player whose inventory is checked
     * @param key    the PDC key to read string values from
     * @param ids    the set of allowed string IDs
     * @return a map of slot index to cloned ItemStacks matching the filter
     */
    public static Map<Integer, ItemStack> getStringSnapshot(@NotNull Player player,
                                                            @NotNull NamespacedKey key,
                                                            @NotNull Set<String> ids) {
        Map<Integer, ItemStack> snapshot = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !item.hasItemMeta()) continue;

            if (ids.contains(PDCUtils.getString(item, key))) {
                snapshot.put(i, item.clone());
            }
        }

        return snapshot;
    }

    /**
     * Creates a snapshot of items in a player's inventory that match any of the provided materials.
     *
     * @param player    the player whose inventory is checked
     * @param materials the set of allowed {@link Material} types
     * @return a map of slot index to cloned matching ItemStacks
     */
    public static Map<Integer, ItemStack> getSnapshot(@NotNull Player player, @NotNull Set<Material> materials) {
        Map<Integer, ItemStack> snapshot = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            if (materials.contains(item.getType())) {
                snapshot.put(i, item.clone());
            }
        }

        return snapshot;
    }

    /**
     * Creates a full snapshot of a player's inventory (slots 0–35).
     *
     * @param player the player whose inventory is snapshotted
     * @return a map of slot index to cloned ItemStacks for all items
     */
    public static Map<Integer, ItemStack> getSnapshot(@NotNull Player player) {
        Map<Integer, ItemStack> snapshot = new HashMap<>();

        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            snapshot.put(i, item.clone());
        }

        return snapshot;
    }

    /**
     * Compares the current contents of a player's inventory against a previously captured snapshot.
     *
     * @param player   the player whose inventory is compared
     * @param snapshot the snapshot to compare against
     * @return true if all items in the snapshot match current items at same slots, false otherwise
     */
    public static boolean compare(@NotNull Player player,
                                  @NotNull Map<Integer, ItemStack> snapshot) {
        for (Map.Entry<Integer, ItemStack> entry : snapshot.entrySet()) {
            ItemStack current = player.getInventory().getItem(entry.getKey());
            ItemStack snapshotItem = entry.getValue();

            if (current == null || !current.equals(snapshotItem)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a snapshot of items in a player's inventory that pass the given condition.
     *
     * @param player    the player whose inventory is scanned
     * @param condition a predicate that returns true for items to include
     * @return a map of slot index to cloned matching ItemStacks
     */
    public static Map<Integer, ItemStack> createSnapshot(@NotNull Player player,
                                                         Predicate<ItemStack> condition) {

        Map<Integer, ItemStack> snapshot = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null) continue;

            if (condition.test(item)) {
                snapshot.put(i, item.clone());
            }
        }

        return snapshot;
    }

    /**
     * Retrieves a map of inventory slots and corresponding {@link ItemStack}s from the player's main inventory
     * where the item's PersistentDataContainer (PDC) string value for the given key matches the specified value.
     *
     * @param player the {@link Player} whose inventory will be checked
     * @param key    the {@link NamespacedKey} used to identify the PDC entry
     * @param value  the string value to match against
     * @return a map of slot indices to matching {@link ItemStack}s
     */
    public static Map<Integer, ItemStack> get(@NotNull Player player,
                                              @NotNull NamespacedKey key,
                                              @NotNull String value) {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !item.hasItemMeta()) continue;

            if (PDCUtils.getString(item, key).equals(value)) {
                inventory.put(i, item);
            }
        }

        return inventory;
    }

    /**
     * Retrieves a map of inventory slots and corresponding {@link ItemStack}s from the player's main inventory
     * where the item contains a PersistentDataContainer (PDC) entry for the specified key.
     *
     * @param player the {@link Player} whose inventory will be checked
     * @param key    the {@link NamespacedKey} used to identify the PDC entry
     * @return a map of slot indices to {@link ItemStack}s containing the given key
     */
    public static Map<Integer, ItemStack> get(@NotNull Player player,
                                              @NotNull NamespacedKey key) {
        Map<Integer, ItemStack> inventory = new HashMap<>();
        for (int i = 0; i < 36; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item == null || !item.hasItemMeta()) continue;

            if (PDCUtils.has(item, key)) {
                inventory.put(i, item);
            }
        }

        return inventory;
    }
}

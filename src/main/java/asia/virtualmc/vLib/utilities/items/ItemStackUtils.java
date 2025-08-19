package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.utilities.messages.AdventureUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class ItemStackUtils {

    /**
     * Creates a new {@link ItemStack} with the specified {@link Material}, display name, lore, and custom model data.
     *
     * @param material     the material type of the item
     * @param displayName  the display name to set, formatted using Adventure components
     * @param lore         the lore lines to apply, each converted to Adventure component
     * @param modelData    the custom model data to apply
     * @return a cloned {@link ItemStack} with the provided metadata, or {@code null} if the item meta could not be retrieved
     */
    @NotNull
    public static ItemStack create(Material material, String displayName,
                                   List<String> lore, int modelData) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(AdventureUtils.toComponent(displayName));
            if (modelData > 0) meta.setCustomModelData(modelData);
            if (lore != null) meta.lore(AdventureUtils.toComponent(lore));

            item.setItemMeta(meta);
            return item.clone();
        }

        return new ItemStack(material);
    }

    /**
     * Sets the custom model data value on the given {@link ItemStack}.
     *
     * @param item       the {@link ItemStack} to modify (must not be null)
     * @param modelData  the custom model data value to apply
     * @return a cloned {@link ItemStack} with the updated model data, or the original item if its meta is null
     */
    public static ItemStack setModelData(@NotNull ItemStack item, int modelData) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(modelData);
            item.setItemMeta(meta);
            return item.clone();
        }

        return item;
    }

    /**
     * Applies a unique key to the given {@link ItemStack} using the plugin's {@link NamespacedKey},
     * making it unstackable with other similar items.
     *
     * @param plugin  the plugin instance used to generate the {@link NamespacedKey}
     * @param item    the {@link ItemStack} to which the unique key will be applied
     * @return a cloned {@link ItemStack} with a unique identifier applied to its {@link PersistentDataContainer}
     */
    public static ItemStack applyUniqueKey(@NotNull Plugin plugin, @NotNull ItemStack item) {
        NamespacedKey key = new NamespacedKey(plugin, "unique_id");
        ItemStack clonedItem = item.clone();
        ItemMeta meta = clonedItem.getItemMeta();

        if (meta == null) return clonedItem;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.INTEGER, (int) (Math.random() * Integer.MAX_VALUE));
        clonedItem.setItemMeta(meta);

        return clonedItem;
    }

    /**
     * Returns the remaining durability of a {@link Damageable} {@link ItemStack}.
     *
     * @param item  the item to check
     * @return the remaining durability, or 0 if the item is null or not damageable
     */
    public static int getDurability(ItemStack item) {
        if (item == null) return 0;

        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable damageable) {
            int maxDurability = item.getType().getMaxDurability();
            return maxDurability - damageable.getDamage();
        }

        return 0;
    }

    /**
     * Gives the specified amount of an {@link ItemStack} to a {@link Player}.
     * If the player's inventory is full, the remaining items will be dropped at the player's location.
     *
     * @param player  the player to give the item to
     * @param item    the item to give
     * @param amount  the total amount of the item to give
     */
    public static void give(Player player, ItemStack item, int amount) {
        if (item == null || amount <= 0) return;

        PlayerInventory inventory = player.getInventory();
        Location dropLocation = player.getLocation();

        while (amount > 0) {
            int stackSize = Math.min(item.getMaxStackSize(), amount);
            ItemStack stackToGive = item.clone();
            stackToGive.setAmount(stackSize);

            HashMap<Integer, ItemStack> leftover = inventory.addItem(stackToGive);
            if (!leftover.isEmpty()) {
                for (ItemStack leftoverItem : leftover.values()) {
                    player.getWorld().dropItemNaturally(dropLocation, leftoverItem);
                }
            }

            amount -= stackSize;
        }
    }
}

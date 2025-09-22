package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.services.bukkit.ComponentService;
import asia.virtualmc.vLib.utilities.messages.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Item;
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
    @Deprecated
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
    @Deprecated
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
     * Gives an item to a player.
     * If the player's inventory is full, excess items are dropped at the player's location.
     *
     * @param player the player to receive the item
     * @param item the item to give (cannot be null)
     * @param amount the total number of items to give
     * @return true if items were processed successfully, false if item is null or amount is invalid
     */
    public static boolean give(Player player, ItemStack item, int amount) {
        if (item == null || amount <= 0) return false;

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

        return true;
    }

    /**
     * Drops a cloned item at a given location.
     *
     * @param item the item to drop (cannot be null)
     * @param location the location where the item should be dropped
     * @param amount the amount of the item to drop
     */
    public static void drop(ItemStack item, Location location, int amount) {
        if (item == null || location == null) return;

        World world = location.getWorld();
        if (world == null) return;

        ItemStack drop = item.clone();
        drop.setAmount(amount);
        world.dropItemNaturally(location, drop);
    }

    /**
     * Drops a clone of the given {@link ItemStack} naturally at the specified {@link Location}.
     *
     * @param item     the item to drop
     * @param location the location where the item should be dropped
     * @return the dropped {@link Item}, or {@code null} if the item or location is invalid
     */
    public static Item drop(ItemStack item, Location location) {
        if (item == null || location == null) return null;

        World world = location.getWorld();
        if (world == null) return null;

        return world.dropItemNaturally(location, item.clone());
    }

    /**
     * Sets a custom item model on the given {@link ItemStack} using a namespaced key string.
     *
     * @param item      the item to modify
     * @param itemModel the resource location string of the model to apply
     */
    public static void setItemModel(ItemStack item, String itemModel) {
        if (item == null || itemModel == null || itemModel.isEmpty()) return;

        NamespacedKey key = NamespacedKey.fromString(itemModel);
        if (key == null) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.setItemModel(key);
        item.setItemMeta(meta);
    }

    /**
     * Creates an {@link ItemStack} with a custom display name and a specified
     * item model (resource pack model override).
     * <p>
     * This uses the modern item component API introduced in Minecraft 1.20.5+, where
     * {@code item_model} replaces the older {@code customModelData} integer.
     * The provided {@code itemModel} must be a valid namespaced key string
     * (e.g. {@code "mynamespace:my_model"}).
     *
     * @param material   the base {@link Material} of the item
     * @param displayName the text to display as the item's custom name
     * @param itemModel   the resource location string of the model to apply
     * @return a new {@link ItemStack} with the specified properties
     */
    public static ItemStack getItemStack(Material material, String displayName, String itemModel) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component name = AdventureUtils.toComponent("<!i>" + displayName);
            meta.displayName(name);
            meta.setItemModel(NamespacedKey.fromString(itemModel));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Creates an {@link ItemStack} with a custom display name, model, and lore.
     * <p>
     * This method uses the modern item component API introduced in Minecraft 1.20.5+,
     * where {@code item_model} is defined by a namespaced key instead of the legacy
     * {@code customModelData} integer. The provided {@code itemModel} must be a valid
     * resource location string (e.g. {@code "mynamespace:my_item"}).
     *
     * @param material    the base {@link Material} of the item
     * @param displayName the text to display as the item's custom name
     * @param itemModel   the resource location string of the model to apply
     * @param lore        a list of lore lines to display under the item's name
     * @return a new {@link ItemStack} with the specified properties
     */
    public static ItemStack getItemStack(Material material, String displayName,
                                         String itemModel, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(AdventureUtils.toComponent("<!i>" + displayName));
            meta.setItemModel(NamespacedKey.fromString(itemModel));
            meta.lore(AdventureUtils.toComponent(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}

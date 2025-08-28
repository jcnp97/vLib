package asia.virtualmc.vLib.services.bukkit;

import asia.virtualmc.vLib.utilities.messages.AdventureUtils;
import io.papermc.paper.datacomponent.item.UseCooldown;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Utility service for building and modifying {@link ItemStack} objects
 * with Adventure and Bukkit API components.
 */
public class ComponentService {

    /**
     * Builder-style wrapper for {@link ItemStack} and its {@link ItemMeta}.
     * Allows fluent modifications before applying changes with {@link #build()}.
     */
    public static class DataComponent {
        private final ItemStack item;
        private final ItemMeta meta;

        public DataComponent(@NotNull ItemStack item) {
            this.item = item.clone();
            this.meta = this.item.getItemMeta();
        }

        /**
         * Applies the modified meta back to the item and returns it.
         *
         * @return the built {@link ItemStack} with changes applied
         */
        public ItemStack build() {
            item.setItemMeta(meta);
            return item;
        }

        /**
         * Gets the wrapped item.
         *
         * @return the current {@link ItemStack}
         */
        public ItemStack getItem() {
            return item;
        }

        /**
         * Gets a clone of the current {@link ItemMeta}.
         *
         * @return a cloned item meta
         */
        public ItemMeta getMeta() {
            return meta.clone();
        }

        /**
         * Sets the display name of the item.
         *
         * @param name the display name to set
         * @return this DataComponent for chaining
         */
        public DataComponent setDisplayName(String name) {
            meta.displayName(AdventureUtils.toComponent(name));
            return this;
        }

        /**
         * Sets the lore of the item.
         *
         * @param lore the lore lines to set
         * @return this DataComponent for chaining
         */
        public DataComponent setLore(List<String> lore) {
            meta.lore(AdventureUtils.toComponent(lore));
            return this;
        }

        /**
         * Sets the custom item model by namespace key.
         *
         * @param itemModel the namespaced key of the model
         * @return this DataComponent for chaining
         */
        public DataComponent setItemModel(String itemModel) {
            NamespacedKey key = NamespacedKey.fromString(itemModel);
            if (key != null) meta.setItemModel(key);
            return this;
        }

        /**
         * Enable/disable if the item should be unbreakable.
         *
         * @return this DataComponent for chaining
         */
        public DataComponent setUnbreakable(boolean enable) {
            meta.setUnbreakable(enable);
            return this;
        }

        /**
         * Sets the maximum stack size for the item.
         *
         * @param amount the stack size
         * @return this DataComponent for chaining
         */
        public DataComponent setMaxStackSize(int amount) {
            if (amount == 0) return this;
            meta.setMaxStackSize(amount);
            return this;
        }

        /**
         * Sets the tooltip style using a namespace key.
         *
         * @param tooltipId the namespaced key of the tooltip style
         * @return this DataComponent for chaining
         */
        public DataComponent setTooltipStyle(String tooltipId) {
            NamespacedKey key = NamespacedKey.fromString(tooltipId);
            if (key != null) meta.setTooltipStyle(key);
            return this;
        }

        /**
         * Adds an enchantment to the item.
         *
         * @param enchant          the enchantment
         * @param level            the enchantment level
         * @param ignoreMaxLevel   whether to allow levels beyond the max
         * @return this DataComponent for chaining
         */
        public DataComponent addEnchant(Enchantment enchant, int level, boolean ignoreMaxLevel) {
            if (enchant == null) return this;
            meta.addEnchant(enchant, level, ignoreMaxLevel);
            return this;
        }

        /**
         * Sets whether the item should have the enchantment glint effect.
         *
         * @param enable true to enable glint, false to disable
         * @return this DataComponent for chaining
         */
        public DataComponent setGlint(boolean enable) {
            if (enable) meta.setEnchantmentGlintOverride(true);
            return this;
        }

        /**
         * Sets how enchantable the item is.
         *
         * @param value enchantability value
         * @return this DataComponent for chaining
         */
        public DataComponent setEnchantable(int value) {
            if (value > 0) meta.setEnchantable(value);
            meta.setEnchantable(1);
            return this;
        }

        /**
         * Adds item flags to hide or show item attributes.
         *
         * @param flags the item flags
         * @return this DataComponent for chaining
         */
        public DataComponent addItemFlags(List<String> flags) {
            if (flags == null || flags.isEmpty()) return this;
            for (String flag : flags) {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            }

            return this;
        }

        public DataComponent useCooldown(NamespacedKey key, float seconds) {
            if (key == null || seconds == 0) return this;
            UseCooldownComponent cooldown = meta.getUseCooldown();
            cooldown.setCooldownGroup(key);
            cooldown.setCooldownSeconds(seconds);
            meta.setUseCooldown(cooldown);

            return this;
        }
    }

    /**
     * Creates a new {@link DataComponent} from an item.
     *
     * @param item the item to wrap
     * @return the DataComponent
     */
    public static DataComponent get(@NotNull ItemStack item) {
        return new DataComponent(item);
    }

    /**
     * Creates a new {@link DataComponent} from a material.
     *
     * @param material the material to create
     * @return the DataComponent
     */
    public static DataComponent get(@NotNull Material material) {
        return new DataComponent(new ItemStack(material));
    }

    /**
     * Creates an item with display name, lore, and item model applied.
     *
     * @param material   the item material
     * @param displayName the display name
     * @param lore       the lore lines
     * @param itemModel  the item model key
     * @return the built {@link ItemStack}
     */
    public static ItemStack get(Material material, String displayName, List<String> lore, String itemModel) {
        return new DataComponent(new ItemStack(material))
                .setDisplayName(displayName)
                .setLore(lore)
                .setItemModel(itemModel)
                .build();
    }
}

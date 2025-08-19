//package asia.virtualmc.vLib.utilities.items;
//
//import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
//import org.bukkit.NamespacedKey;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.inventory.ItemFlag;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//import org.jetbrains.annotations.NotNull;
//
//public class DataComponentUtils {
//
//    public static class DataComponent {
//        private final ItemStack item;
//        private final ItemMeta meta;
//
//        public DataComponent(@NotNull ItemStack item) {
//            this.item = item.clone();
//            this.meta = this.item.getItemMeta();
//        }
//
//        public ItemStack build() {
//            item.setItemMeta(meta);
//            return item;
//        }
//
//        public ItemStack getItem() {
//            return item;
//        }
//
//        public ItemMeta getMeta() {
//            return meta;
//        }
//
//        public DataComponent setItemModel(String itemModel) {
//            NamespacedKey key = NamespacedKey.fromString(itemModel);
//            if (key != null) meta.setItemModel(key);
//            return this;
//        }
//
//        public DataComponent setUnbreakable() {
//            meta.setUnbreakable(true);
//            return this;
//        }
//
//        public DataComponent setMaxStackSize(int amount) {
//            meta.setMaxStackSize(amount);
//            return this;
//        }
//
//        public DataComponent setTooltipStyle(String tooltipId) {
//            meta.setTooltipStyle(NamespacedKey.fromString(tooltipId));
//            return this;
//        }
//
//        public DataComponent addEnchant(Enchantment enchant, int level, boolean ignoreMaxLevel) {
//            if (enchant == null) return this;
//            meta.addEnchant(enchant, level, ignoreMaxLevel);
//            return this;
//        }
//
//        public DataComponent setGlint(boolean enable) {
//            meta.setEnchantmentGlintOverride(enable);
//            return this;
//        }
//
//        public DataComponent setEnchantable(int value) {
//            meta.setEnchantable(value);
//            return this;
//        }
//
//        public DataComponent addItemFlags(ItemFlag flag) {
//            meta.addItemFlags(flag);
//            return this;
//        }
//    }
//
//    public static DataComponent getDataComponent(@NotNull ItemStack item) {
//        return new DataComponent(item);
//    }
//}

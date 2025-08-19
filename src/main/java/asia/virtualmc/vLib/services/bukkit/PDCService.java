package asia.virtualmc.vLib.services.bukkit;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class PDCService {

    public static class PDC {
        private final ItemStack item;
        private final ItemMeta meta;
        private final PersistentDataContainer pdc;

        public PDC(@NotNull ItemStack item) {
            this.item = item.clone();
            this.meta = this.item.getItemMeta();
            this.pdc = this.meta.getPersistentDataContainer();
        }

        public ItemStack build() {
            item.setItemMeta(meta);
            return item;
        }

        public ItemStack getItem() {
            return item;
        }

        public ItemMeta getMeta() {
            return meta.clone();
        }


        public PDC addInt(@NotNull NamespacedKey key, int value) {
            pdc.set(key, PersistentDataType.INTEGER, value);
            return this;
        }

        public PDC addString(@NotNull NamespacedKey key, @NotNull String value) {
            pdc.set(key, PersistentDataType.STRING, value);
            return this;
        }

        public PDC addDouble(@NotNull NamespacedKey key, double value) {
            pdc.set(key, PersistentDataType.DOUBLE, value);
            return this;
        }

        public PDC addIntMap(@NotNull Plugin plugin, Map<String, Integer> map) {
            if (map.isEmpty()) return this;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
                pdc.set(key, PersistentDataType.INTEGER, entry.getValue());
            }
            return this;
        }

        public PDC addStringMap(@NotNull Plugin plugin, Map<String, String> map) {
            if (map.isEmpty()) return this;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
                pdc.set(key, PersistentDataType.STRING, entry.getValue());
            }
            return this;
        }

        public PDC addDoubleMap(@NotNull Plugin plugin, Map<String, Double> map) {
            if (map.isEmpty()) return this;
            for (Map.Entry<String, Double> entry : map.entrySet()) {
                NamespacedKey key = new NamespacedKey(plugin, entry.getKey());
                pdc.set(key, PersistentDataType.DOUBLE, entry.getValue());
            }
            return this;
        }
    }

    public static PDC get(@NotNull ItemStack item) {
        return new PDC(item);
    }
}

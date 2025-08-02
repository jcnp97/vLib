package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.utilities.messages.AdventureUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MetaUtils {

    /**
     * Represents simplified item metadata for display purposes.
     */
    public static class Item {
        public String name;
        public List<String> lore;
        public int modelData;

        public Item(String name, List<String> lore, int modelData) {
            this.name = name;
            this.lore = lore;
            this.modelData = modelData;
        }
    }

    /**
     * Extracts a simplified {@link Item} metadata wrapper from the provided {@link ItemStack}.
     *
     * @param item the item to extract metadata from
     * @return an {@link Item} containing the display name, lore, and model data
     */
    public static Item get(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            String name = getDisplayName(item);
            return new Item(name, null, 0);
        }

        return new Item(getDisplayName(item), meta.getLore(), meta.getCustomModelData());
    }

    /**
     * Retrieves the display name of the provided {@link ItemStack}.
     * If no display name is set, it generates one based on the item's material name.
     *
     * @param item the item to read the display name from
     * @return the display name, or a formatted fallback name if not set
     */
    public static String getDisplayName(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return Arrays.stream(item.getType().toString().toLowerCase().split("_"))
                    .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                    .collect(Collectors.joining(" "));
        }

        return meta.getDisplayName();
    }

    /**
     * Applies a simplified {@link Item} metadata wrapper to the given {@link ItemMeta},
     * including Adventure-style components for name and lore.
     *
     * @param meta  the {@link ItemMeta} to apply to
     * @param item  the {@link Item} metadata to apply
     * @return a cloned {@link ItemMeta} with the applied values
     */
    public static ItemMeta apply(@NotNull ItemMeta meta, @NotNull Item item) {
        meta.displayName(AdventureUtils.toComponent(item.name));
        meta.lore(AdventureUtils.toComponent(item.lore));
        meta.setCustomModelData(item.modelData);

        return meta.clone();
    }
}

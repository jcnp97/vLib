package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.utilities.messages.AdventureUtils;
import asia.virtualmc.vLib.utilities.text.StringUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TextComponentUtils {

    /**
     * Wrapper class holding display name and lore as Adventure Components.
     */
    public static class TextComponent {
        public Component name;
        public List<Component> lore;

        public TextComponent(Component name, List<Component> lore) {
            this.name = name;
            this.lore = lore;
        }
    }

    /**
     * Gets the display name and lore of an item as a TextComponent.
     *
     * @param item the ItemStack to read from
     * @return a TextComponent containing the name and lore
     */
    public static TextComponent get(@NotNull ItemStack item) {
        return new TextComponent(getName(item), getLore(item));
    }

    /**
     * Creates a TextComponent from string values.
     *
     * @param name the display name string
     * @param lore the lore string list
     * @return a TextComponent with converted components
     */
    public static TextComponent get(String name, List<String> lore) {
        return new TextComponent(AdventureUtils.toComponent(name), AdventureUtils.toComponent(lore));
    }

    /**
     * Gets the display name of an item.
     * Falls back to formatted material name if none exists.
     *
     * @param item the ItemStack to read from
     * @return the name as a Component
     */
    public static Component getName(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return AdventureUtils.toComponent(StringUtils.format(item.getType().toString()));
        }

        return meta.displayName();
    }

    /**
     * Gets the raw display name of the given {@link ItemStack}.
     * If the item has no display name, the material type is returned instead.
     *
     * @param item the item to read from
     * @return the display name as a string, or the formatted material type if none exists
     */
    public static String getRawName(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return StringUtils.format(item.getType().toString());
        }

        return AdventureUtils.toString(meta.displayName());
    }

    /**
     * Gets the lore of an item.
     *
     * @param item the ItemStack to read from
     * @return a list of lore Components, or empty if none
     */
    public static List<Component> getLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return new ArrayList<>();
        }

        return item.lore();
    }

    /**
     * Gets the raw lore of the given {@link ItemStack}.
     * If the item has no lore, an empty list is returned.
     *
     * @param item the item to read from
     * @return the lore as a list of strings, or an empty list if none exists
     */
    public static List<String> getRawLore(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return new ArrayList<>();
        }

        return AdventureUtils.toString(item.lore());
    }

    /**
     * Applies a TextComponent (name and lore) to an ItemMeta.
     *
     * @param meta the ItemMeta to apply changes to
     * @param text the TextComponent with name and lore
     */
    public static void apply(@NotNull ItemMeta meta, @NotNull TextComponent text) {
        meta.displayName(text.name);
        meta.lore(text.lore);
    }

    /**
     * Applies string-based name and lore to an ItemMeta.
     * Automatically converts strings into Components.
     *
     * @param meta the ItemMeta to apply changes to
     * @param name the display name string
     * @param lore the lore string list
     */
    public static void apply(@NotNull ItemMeta meta, String name, List<String> lore) {
        TextComponent text = get(name, lore);
        apply(meta, text);
    }
}

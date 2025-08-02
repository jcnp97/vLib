package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.string.SplitUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantUtils {

    /**
     * Represents a simplified enchantment with its name, Bukkit enchantment type, and level.
     */
    public static class Enchant {
        public String name;
        public Enchantment enchantment;
        public int level;

        public Enchant(String name, Enchantment enchantment, int level) {
            this.name = name;
            this.enchantment = enchantment;
            this.level = level;
        }
    }

    /**
     * Converts a string in the format "ENCHANT_NAME:LEVEL" into an {@link Enchant} object.
     * If the string is invalid or the enchantment name is not recognized, returns {@code null}.
     *
     * @param string the string representing the enchantment and level
     * @return the parsed {@link Enchant}, or {@code null} if invalid
     */
    public static Enchant toEnchantment(String string) {
        String[] parts = SplitUtils.split(string, ":");
        if (parts.length != 2) {
            return null;
        }

        Enchantment enchant = Enchantment.getByName(parts[0]);
        int level = IntegerUtils.toInt(parts[1]);
        if (enchant != null) {
            return new Enchant(parts[0], enchant, level);
        }

        return null;
    }

    /**
     * Adds the given {@link Enchant} to the provided {@link ItemMeta}, allowing unsafe levels.
     * Logs a severe error if the enchantment is null or its level is non-positive.
     *
     * @param meta    the {@link ItemMeta} to apply the enchantment to
     * @param enchant the enchantment to apply; if null or invalid, logs an error and does nothing
     */
    public static void add(ItemMeta meta, Enchant enchant) {
        String itemName = meta.hasDisplayName() ? meta.getDisplayName() : "Unnamed Item";

        if (enchant == null || enchant.enchantment == null || enchant.level <= 0) {
            ConsoleUtils.severe("Invalid enchantment or level for item: " + itemName);
            return;
        }

        try {
            meta.addEnchant(enchant.enchantment, enchant.level, true);
        } catch (Exception e) {
            ConsoleUtils.severe("Failed to add enchantment to item: " + itemName + " | " + e.getMessage());
        }
    }
}

package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class EnchantUtils {
    private static final Registry<@NotNull Enchantment> registry = RegistryAccess
            .registryAccess()
            .getRegistry(RegistryKey.ENCHANTMENT);

    /**
     * Converts a string into an {@link Enchantment} from the Minecraft registry.
     *
     * @param string the enchantment name (without "minecraft:" prefix)
     * @return the corresponding {@link Enchantment}, or {@code null} if not found
     */
    @Nullable
    public static Enchantment toEnchantment(@NotNull String string) {
        return registry.get(RegistryKey.ENCHANTMENT.typedKey("minecraft" + string.toLowerCase()));
    }

    /**
     * Adds a single enchantment to the given {@link ItemMeta}.
     *
     * @param meta        the item meta to apply the enchantment to
     * @param enchantName the enchantment name (without "minecraft:" prefix)
     * @param level       the enchantment level
     */
    public static void add(@NotNull ItemMeta meta, String enchantName, int level) {
        Enchantment enchant = toEnchantment(enchantName);
        if (enchant == null) {
            ConsoleUtils.severe("Unable to add enchantment into " + meta.getDisplayName() + " because " + enchantName + " is null!");
            return;
        }

        meta.addEnchant(enchant, level, true);
    }

    /**
     * Adds multiple enchantments to the given {@link ItemMeta} using enchantment names as keys.
     *
     * @param meta     the item meta to apply the enchantments to
     * @param enchants a map of enchantment names to their levels
     */
    public static void add(@NotNull ItemMeta meta, Map<String, Integer> enchants) {
        for (Map.Entry<String, Integer> entry : enchants.entrySet()) {
            Enchantment enchant = toEnchantment(entry.getKey());
            if (enchant == null) {
                ConsoleUtils.severe("Unable to add enchantment into " + meta.getDisplayName() + " because " + entry.getKey() + "is invalid!");
                continue;
            }

            meta.addEnchant(enchant, entry.getValue(), true);
        }
    }
}
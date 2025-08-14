package asia.virtualmc.vLib.core.player_data;

import asia.virtualmc.vLib.core.items.ItemCoreUtils;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.items.ItemStackUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InnateTraitUtils {

    /**
     * Represents an innate trait with its name, assigned slot, associated item,
     * and a map of effect values.
     */
    public static class InnateTrait {
        public String traitName;
        public int slot;
        public ItemStack item;
        public Map<String, Double> effects;

        public InnateTrait(String traitName, int slot, ItemStack item, Map<String, Double> effects) {
            this.traitName = traitName;
            this.slot = slot;
            this.item = item;
            this.effects = effects;
        }
    }

    /**
     * Loads innate traits from a YAML configuration file and stores them in a map.
     * <p>
     * Each trait entry must contain valid item data, slot position, and effect values.
     * If configuration is invalid or missing, errors are logged and the trait is skipped.
     * </p>
     *
     * @param plugin   The plugin instance used to locate the file.
     * @return A map of trait keys to {@link InnateTrait} objects, or {@code null} if loading fails.
     */
    public static Map<String, InnateTrait> load(@NotNull Plugin plugin) {
        Map<String, InnateTrait> traitCache = new HashMap<>();
        String prefix = "[" + plugin.getName() + "]";
        String fileName = "innate-traits.yml";

        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileName);
        if (yaml == null) {
            ConsoleUtils.severe(prefix, "Couldn't find " + fileName + ". Skipping innate traits creation..");
            return null;
        }

        Section section = yaml.getSection("traitList");
        if (section == null) {
            ConsoleUtils.severe(prefix, "Looks like " + fileName + " is empty. Skipping innate traits creation..");
            return null;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String key : keys) {
            ItemCoreUtils.CustomItem customItem = ItemCoreUtils.get(section.getSection(key));
            int slot = section.getInt(key + ".slot");
            Map<String, Double> values = YAMLUtils.getDoubleMap(yaml, "traitList." + key + ".effects", true);

            if (customItem.material == null || customItem.name == null) {
                ConsoleUtils.severe(prefix, "Invalid configuration for trait name: " + key);
                continue;
            }

            ItemStack item = ItemStackUtils.create(customItem.material, customItem.name, customItem.lore, customItem.modelData);
            ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                ConsoleUtils.severe("Could not retrieve item meta for trait name: " + key);
                continue;
            }

            traitCache.put(key, new InnateTrait(key, slot, item.clone(), values));
        }

        return traitCache;
    }
}

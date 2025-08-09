package asia.virtualmc.vLib.core.guis;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.items.ItemStackUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GUIConfig {
    private static ItemStack invisibleItem;
    private static final Map<String, String> guiUnicodes = new HashMap<>();

    /**
     * Loads the GUI configuration from {@code skills-core/default-gui.yml}.
     * <p>Initializes the invisible GUI item and GUI title unicode mappings.</p>
     * <p><b>Warning:</b> This method is for library use only and should not be called directly by plugins.</p>
     */
    @Internal
    public static void load() {
        YamlDocument yaml = YAMLUtils.getYaml(Main.getInstance(), "skills-core/default-gui.yml");
        if (yaml == null) {
            ConsoleUtils.severe("Couldn't find skills-core/default-gui.yml or it was empty!");
            return;
        }

        int modelData = yaml.getInt("invisible_item.model_data");
        String materialName = yaml.getString("invisible_item.material");
        Material material = Material.valueOf(materialName);
        ItemStack item = ItemStackUtils.create(material, "", new ArrayList<>(), modelData);
        if (item != null) {
            invisibleItem = item.clone();
        }

        Section section = yaml.getSection("gui_titles");
        if (section != null) {
            Set<String> keys = yaml.getRoutesAsStrings(false);
            for (String key : keys) {
                String unicode = section.getString(key);
                guiUnicodes.put(key, unicode);
            }
        }
    }

    /**
     * Returns a cloned instance of the invisible GUI item configured in the YAML file.
     *
     * @return a clone of the invisible {@link ItemStack}, or {@code null} if not initialized
     */
    public static ItemStack getItem() {
        if (invisibleItem == null) {
            ConsoleUtils.severe("Invisible item is NULL! Using default material..");
            return new ItemStack(Material.PAPER);
        }

        return invisibleItem.clone();
    }

    /**
     * Retrieves the unicode string mapped to the specified GUI title key.
     *
     * @param title the title key to look up
     * @return the corresponding unicode string, or an empty string if the key is null
     */
    public static String get(String title) {
        String guiTitle = guiUnicodes.get(title);
        if (guiTitle == null) {
            return "";
        }

        return guiTitle;
    }
}

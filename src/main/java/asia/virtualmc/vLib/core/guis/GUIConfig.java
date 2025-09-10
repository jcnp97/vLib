package asia.virtualmc.vLib.core.guis;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.services.bukkit.ComponentService;
import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUIConfig {
    private static String invisibleItemModel;
    private static final Map<String, String> unicodes = new HashMap<>();

    @Internal
    public static void load() {
        YamlFileService.YamlFile file = YamlFileService.get(Main.getInstance(), "skills-core/default-gui.yml");
        Section section = file.getSection("settings");

        invisibleItemModel = section.getString("invisible-item-model");
        unicodes.putAll(file.stringKeyStringMap(section.getSection("titles"), false));
    }

    public static String getItemModel() {
        return (invisibleItemModel != null) ? invisibleItemModel : "cozyvanilla_guiitems:invisible_item";
    }

    public static ItemStack getItem(String name) {
        return ComponentService.get(Material.PAPER, name, new ArrayList<>(), getItemModel());
    }

    public static String get(String title) {
        String guiTitle = unicodes.get(title);
        if (guiTitle == null) {
            return "";
        }

        return guiTitle;
    }
}

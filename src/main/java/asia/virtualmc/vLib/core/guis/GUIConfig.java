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
    private static String invisibleModel;
    private static String leftClickAnimModel;
    private static String rightClickAnimModel;
    private static final Map<String, String> unicodes = new HashMap<>();

    @Internal
    public static void load() {
        YamlFileService.YamlFile file = YamlFileService.get(Main.getInstance(), "skills-core/default-gui.yml");
        Section section = file.getSection("settings");

        invisibleModel = section.getString("invisible-model");
        leftClickAnimModel = section.getString("left-click-model");
        rightClickAnimModel = section.getString("right-click-model");

        unicodes.putAll(file.stringKeyStringMap(section.getSection("titles"), false));
    }

    public static String getInvisibleModel() {
        return (invisibleModel != null) ? invisibleModel : "cozyvanilla_guiitems:invisible_item";
    }

    public static String getLeftClickAnim() {
        return (leftClickAnimModel != null) ? leftClickAnimModel : "cozyvanilla_guiitems:left_click_anim_menu";
    }

    public static String getRightClickAnim() {
        return (rightClickAnimModel != null) ? rightClickAnimModel : "cozyvanilla_guiitems:right_click_anim_menu";
    }

    public static ItemStack getInvisibleItem(String name) {
        return ComponentService.get(Material.PAPER, name, new ArrayList<>(), getInvisibleModel());
    }

    public static ItemStack getLeftClickItem(String name) {
        return ComponentService.get(Material.PAPER, name, new ArrayList<>(), getLeftClickAnim());
    }

    public static ItemStack getRightClickItem(String name) {
        return ComponentService.get(Material.PAPER, name, new ArrayList<>(), getRightClickAnim());
    }

    public static String getMenu(String title) {
        String guiTitle = unicodes.get(title);
        if (guiTitle == null) {
            return "";
        }

        return guiTitle;
    }
}

package asia.virtualmc.vLib.core.utilities;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.core.guis.GUIItemUtils;
import asia.virtualmc.vLib.services.bukkit.ComponentService;
import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.digit.DecimalUtils;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgressBarUtils {
    private static final Map<Integer, String> unicodes = new HashMap<>();

    /**
     * Loads progress bar unicode characters from the messages.yml file into memory.
     * <p>
     * This method reads the "progress-bar" section of messages.yml and stores each key-value pair
     * into the {@code unicodes} map, where the key is parsed as an integer.
     * </p>
     * <p>
     * If the file or section is missing, the method logs an error and skips loading.
     * </p>
     *
     * @Internal This method is only for library-use and should not be called by external plugins.
     */
    @Internal
    public static void load() {
        unicodes.clear();

        YamlFileService.YamlFile yaml = YamlFileService.get(Main.getInstance(), "messages.yml");
        Section section = yaml.getSection("progress-bar");
        if (section == null) {
            ConsoleUtils.severe("Unable to read progress-bar section. Skipping progress bar creation..");
            return;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String key : keys) {
            unicodes.put(IntegerUtils.toInt(key), section.getString(key));
        }

        ConsoleUtils.info("Loaded " + unicodes.size() + " items into progress bar.");
    }

    /**
     * Retrieves the appropriate progress bar unicode based on the given percentage value
     * where the maximum is always assumed to be 100.0.
     *
     * @param value the progress percentage (0.0 to 100.0)
     * @return the unicode string for the progress bar
     */
    public static String get(double value) {
        return get(value, 100.0);
    }

    /**
     * Retrieves the appropriate progress bar unicode based on the given value and custom maximum.
     * <p>
     * Dynamically adjusts based on the number of entries in {@code unicodes}.
     * - If {@code value} is less than or equal to 0, returns index 0.
     * - If {@code value} is greater than or equal to {@code maxValue}, returns the last index.
     * - Otherwise, calculates the index proportionally between 0 and maxValue.
     * </p>
     *
     * @param value    the current progress value
     * @param maxValue the maximum progress value
     * @return the unicode string for the progress bar
     */
    public static String get(double value, double maxValue) {
        if (unicodes.isEmpty()) return "DISABLED";
        if (maxValue <= 0) {
            ConsoleUtils.severe("Invalid maxValue for ProgressBarUtils#get: " + maxValue);
            return unicodes.get(0) + getPercentage(0, 0);
        }

        int maxIndex = unicodes.size() - 1;
        if (value <= 0) return unicodes.get(0) + getPercentage(0, 0);
        if (value >= maxValue) return unicodes.get(maxIndex) + getPercentage(maxIndex, 1);

        double percentage = value / maxValue;
        int index = (int) Math.ceil(percentage * maxIndex);

        if (index < 0) index = 0;
        if (index > maxIndex) index = maxIndex;

        return unicodes.getOrDefault(index, unicodes.get(0)) + getPercentage(index, percentage);
    }

    private static String getPercentage(int index, double percentage) {
        String color = "<gray>";
        if (index >= 0 && index < 4) {
            color = "<#f70500>";
        } else if (index >= 4 && index < 7) {
            color = "<#f7a000>";
        } else if (index >= 7 && index < 10) {
            color = "<#f7f200>";
        } else if (index >= 10) {
            color = "<#00f200>";
        }

        return " <gray>(" + color + DecimalUtils.format(percentage * 100) + "%<gray>)";
    }

    public static StaticPane getAsItem(StaticPane pane, double progress, int count, int posX, int posY, String displayName, String itemModel, List<String> lore) {
        ComponentService.DataComponent data = ComponentService.get(Material.PAPER);

        int[] additions = GUIItemUtils.getBarItem(progress, count);
        for (int x = 0; x < count; x++) {
            ItemStack item = data.setDisplayName(displayName).setLore(lore).setItemModel(itemModel + "_" + additions[x]).build();
            GuiItem guiItem = new GuiItem(item.clone());
            pane.addItem(guiItem, posX, posY);
            posX++;
        }

        return pane;
    }
}

package asia.virtualmc.vLib.core.utilities;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.HashMap;
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

        YamlDocument yaml = YAMLUtils.getYaml(Main.getInstance(), "messages.yml");
        if (yaml == null) {
            ConsoleUtils.severe("Unable to read messages.yml. Skipping progress bar creation..");
            return;
        }

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
            return unicodes.get(0);
        }

        int maxIndex = unicodes.size() - 1;
        if (value <= 0) return unicodes.get(0);
        if (value >= maxValue) return unicodes.get(maxIndex);

        double percentage = value / maxValue;
        int index = (int) Math.ceil(percentage * maxIndex);

        if (index < 0) index = 0;
        if (index > maxIndex) index = maxIndex;

        return unicodes.getOrDefault(index, unicodes.get(0));
    }
}

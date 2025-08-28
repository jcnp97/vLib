package asia.virtualmc.vLib.core.utilities;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LevelTagUtils {
    private static final Map<Integer, String> unicodes = new HashMap<>();

    @Internal
    public static void load() {
        unicodes.clear();

        YamlFileService.YamlFile yaml = YamlFileService.get(Main.getInstance(), "messages.yml");
        Section section = yaml.getSection("level-tags");
        if (section == null) {
            ConsoleUtils.severe("Unable to read level-tags section. Skipping progress bar creation..");
            return;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String key : keys) {
            unicodes.put(IntegerUtils.toInt(key), section.getString(key));
        }

        ConsoleUtils.info("Loaded " + unicodes.size() + " items into level tags.");
    }

    public static String get(int number) {
        if (number <= 0 || number > 200) return unicodes.get(1);
        return unicodes.get(number);
    }
}

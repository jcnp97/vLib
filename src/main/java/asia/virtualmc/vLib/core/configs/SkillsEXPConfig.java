package asia.virtualmc.vLib.core.configs;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SkillsEXPConfig {

    /**
     * Loads the experience table from {@code experience-table.yml} in the given plugin's data folder.
     * <p>If the file is missing or invalid, falls back to default table.</p>
     *
     * @param plugin the plugin whose data folder contains the configuration file (must not be null)
     * @return a map where the key is the level and the value is the required experience for that level
     */
    public static Map<Integer, Integer> get(@NotNull Plugin plugin) {
        YamlDocument yaml = YAMLUtils.getYaml(plugin, "experience-table.yml");
        String prefix = "[" + plugin.getName() + "]";
        Map<Integer, Integer> expTable = new HashMap<>();

        if (yaml == null) {
            ConsoleUtils.severe(prefix, "File experience-table.yml not found! Using default experience table..");
            yaml = YAMLUtils.getYaml(Main.getInstance(), "skills-core/default-experience.yml");

            if (yaml == null) {
                ConsoleUtils.severe("File default-experience.yml from vLib not found! Experience table is disabled.");
                return expTable;
            }
        }

        Set<String> keys = yaml.getRoutesAsStrings(false);
        for (String key : keys) {
            int level = IntegerUtils.toInt(key);
            int experience = IntegerUtils.toInt(yaml.getString(key));
            expTable.put(level, experience);
        }

        return expTable;
    }
}

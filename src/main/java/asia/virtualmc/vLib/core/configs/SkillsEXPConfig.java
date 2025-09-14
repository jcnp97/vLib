package asia.virtualmc.vLib.core.configs;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.java.HashSetUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
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
    public static Map<Integer, Integer> get(@NotNull Plugin plugin, String fileName) {
        String prefix = "[" + plugin.getName() + "]";
        Map<Integer, Integer> expTable = new HashMap<>();

        YamlFileService.YamlFile file = YamlFileService.get(plugin, fileName);
        Section levelSection = file.getSection("levels");
        if (levelSection == null) {
            ConsoleUtils.severe(prefix, "File " + fileName + " not found! Using default exp table..");
            file = YamlFileService.get(Main.getInstance(), "skills-core/default-experience.yml");
            levelSection = file.getSection("levels");
        }

        Set<String> keys = levelSection.getRoutesAsStrings(false);
        for (String key : keys) {
            int level = IntegerUtils.toInt(key);
            int experience = IntegerUtils.toInt(levelSection.getString(key));
            expTable.put(level, experience);
        }

        // Virtual Levels
        List<String> levels = file.getYaml().getStringList("virtual-levels");
        if (levels != null && !levels.isEmpty()) {
            for (String level : levels) {
                String[] parts = level.split(";");
                Set<Integer> virtualLevels = HashSetUtils.getRange(parts[0]);
                int experience = IntegerUtils.toInt(parts[1]);

                for (int virtualLevel : virtualLevels) {
                    expTable.put(virtualLevel, experience);
                }
            }
        }

        ConsoleUtils.debugMap(expTable);
        return expTable;
    }
}

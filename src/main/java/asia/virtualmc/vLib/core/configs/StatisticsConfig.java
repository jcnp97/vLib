package asia.virtualmc.vLib.core.configs;

import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class StatisticsConfig {

    public static Set<String> get(@NotNull Plugin plugin) {
        String fileName = "statistics.yml";

        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileName);
        if (yaml == null) {
            ConsoleUtils.severe(fileName + " not found! Skipping statistics creation..");
            return new HashSet<>();
        }

        return new HashSet<>(yaml.getStringList("statistics"));
    }
}

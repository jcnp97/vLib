package asia.virtualmc.vLib.core.configs;

import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class StatisticsConfig {

    public static Map<String, Double> get(@NotNull Plugin plugin, @NotNull String fileName) {
        Map<String, Double> cache = new HashMap<>();
        String prefix = "[" + plugin.getName() + "]";

        YamlFileService.YamlFile file = YamlFileService.get(plugin, fileName);
        Section section = file.getSection("statistics");
        if (section == null) {
            ConsoleUtils.severe(prefix, "Section `statistics` is not found! Skipping statistics creation..");
            return cache;
        }

        return file.stringKeyDoubleMap(section, false);
    }
}

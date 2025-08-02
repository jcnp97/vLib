package asia.virtualmc.vLib;

import asia.virtualmc.vLib.integration.IntegrationManager;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ClassRegistry {
    private static final Map<String, Boolean> modules = new HashMap<>();

    public ClassRegistry(@NotNull Main plugin) {
        YamlDocument yaml = YAMLUtils.getYaml(plugin, "config.yml");
        if (yaml == null) {
            ConsoleUtils.severe("Unable to load modules due to missing config.yml!");
            return;
        }

        modules.putAll(YAMLUtils.getBooleanMap(yaml, "modules", false));
        if (!modules.isEmpty()) {
            new IntegrationManager(new HashMap<>(modules));
            new CommandManager(new HashMap<>(modules));
        }

        // Reloadable
        MessageUtils.load(plugin);
    }
}

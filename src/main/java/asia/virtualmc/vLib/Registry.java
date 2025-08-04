package asia.virtualmc.vLib;

import asia.virtualmc.vLib.integration.IntegrationManager;
import asia.virtualmc.vLib.storage.StorageManager;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Registry {
    private static final Map<String, Boolean> modules = new HashMap<>();
    private StorageManager storageManager;
    private IntegrationManager integrationManager;
    private CommandManager commandManager;

    public Registry(@NotNull Main plugin) {
        YamlDocument yaml = YAMLUtils.getYaml(plugin, "config.yml");
        if (yaml == null) {
            ConsoleUtils.severe("Unable to load modules due to missing config.yml!");
            return;
        }

        modules.putAll(YAMLUtils.getBooleanMap(yaml, "modules", false));
        if (!modules.isEmpty()) {
            this.storageManager = new StorageManager();
            this.integrationManager = new IntegrationManager();
            this.commandManager = new CommandManager();
        }

        // Reloadable
        MessageUtils.load(plugin);
    }

    public void disable() {
        storageManager.disable();
        integrationManager.disable();
        commandManager.disable();
    }

    public static Map<String, Boolean> getModules() {
        return new HashMap<>(modules);
    }
}

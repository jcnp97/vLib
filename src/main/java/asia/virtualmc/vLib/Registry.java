package asia.virtualmc.vLib;

import asia.virtualmc.vLib.core.guis.GUIConfig;
import asia.virtualmc.vLib.core.ray_trace.RayTraceManager;
import asia.virtualmc.vLib.core.utilities.LevelTagUtils;
import asia.virtualmc.vLib.core.utilities.ProgressBarUtils;
import asia.virtualmc.vLib.integration.IntegrationManager;
import asia.virtualmc.vLib.listeners.ServerJoinListener;
import asia.virtualmc.vLib.storage.StorageManager;
import asia.virtualmc.vLib.tasks.TaskManager;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Registry {
    private static final Map<String, Boolean> modules = new HashMap<>();
    private final Main plugin;
    private StorageManager storageManager;
    private IntegrationManager integrationManager;
    private Commands commandManager;
    private RayTraceManager rayTraceManager;
    private TaskManager taskManager;

    public Registry(@NotNull Main plugin) {
        this.plugin = plugin;
        YamlDocument yaml = YAMLUtils.getYaml(plugin, "config.yml");
        if (yaml == null) {
            ConsoleUtils.severe("Unable to load modules due to missing config.yml!");
            return;
        }

        modules.putAll(YAMLUtils.getBooleanMap(yaml, "modules", false));
        if (!modules.isEmpty()) {
            this.storageManager = new StorageManager();
            this.integrationManager = new IntegrationManager();
            this.rayTraceManager = new RayTraceManager(plugin);
            this.commandManager = new Commands();
        }

        // Miscellaneous
        this.taskManager = new TaskManager(this);

        // Reloadable
        MessageUtils.load(plugin);
        GUIConfig.load();
        ProgressBarUtils.load();
        LevelTagUtils.load();

        // Listeners
        new ServerJoinListener(plugin);
    }

    public void disable() {
        storageManager.disable();
        integrationManager.disable();
        commandManager.disable();
        taskManager.disable();
        rayTraceManager.disable();
    }

    public Main getMain() { return plugin; }
    public StorageManager getStorageManager() { return storageManager; }
    public RayTraceManager getRayTraceManager() { return rayTraceManager; }
    public static boolean getModule(String module) { return modules.containsKey(module); }
    public static Map<String, Boolean> getModules() { return new HashMap<>(modules); }
}

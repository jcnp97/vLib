package asia.virtualmc.vLib.tasks;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.Registry;
import asia.virtualmc.vLib.core.ray_trace.RayTraceManager;
import asia.virtualmc.vLib.integration.hologram_lib.PlayerHologramUtils;
import asia.virtualmc.vLib.storage.StorageManager;
import asia.virtualmc.vLib.utilities.paper.TaskUtils;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TaskManager {
    private final Main plugin;
    private final StorageManager storageManager;
    private final RayTraceManager rayTraceManager;
    private final Set<ScheduledTask> taskCache = new HashSet<>();

    public TaskManager(@NotNull Registry registry) {
        this.plugin = registry.getMain();
        this.storageManager = registry.getStorageManager();
        this.rayTraceManager = registry.getRayTraceManager();
        enable();
    }

    public void enable() {
        startSync();
    }

    public void disable() {
        cancelAll();
    }

    private void startSync() {
        taskCache.add(TaskUtils.repeating(plugin,
                storageManager::task, 600));

        if (Registry.getModule("ray_trace")) {
            taskCache.add(TaskUtils.repeating(plugin,
                    rayTraceManager::task, 0.5));
        }
    }

    private void startAsync() {

    }

    private void cancelAll() {
        for (ScheduledTask task : taskCache) {
            task.cancel();
        }
        taskCache.clear();
    }
}

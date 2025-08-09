
package asia.virtualmc.vLib.utilities.paper;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SyncUtils {

    /**
     * Runs a task synchronously on the main thread first, then schedules a follow-up task asynchronously with the result.
     * This is the opposite of AsyncUtils.runAsyncThenSync().
     * Use this for workflows like:
     * - Gathering data from Bukkit API or main thread state (main thread)
     * - Processing or storing that data externally without blocking the server (async thread)
     *
     * @param plugin        The plugin instance used to schedule the async follow-up task.
     * @param syncTask      A Supplier that returns the result of a synchronous operation on the main thread.
     *                      This will be executed immediately if already on main thread, or scheduled if not.
     * @param asyncCallback A Consumer that accepts the result returned by syncTask and runs asynchronously.
     *                      This is where you can perform IO operations or heavy processing without blocking the server.
     * @param <T>           The type of result produced by the sync task and consumed by the async callback.
     */
    public static <T> void runSyncThenAsync(Plugin plugin, Supplier<T> syncTask, Consumer<T> asyncCallback) {
        if (Bukkit.isPrimaryThread()) {
            T result;
            try {
                result = syncTask.get();
            } catch (Exception e) {
                ConsoleUtils.severe("Sync task failed: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // Schedule async callback
            plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
                try {
                    asyncCallback.accept(result);
                } catch (Exception e) {
                    ConsoleUtils.severe("Async callback failed: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> {
                T result;
                try {
                    result = syncTask.get();
                } catch (Exception e) {
                    ConsoleUtils.severe("Sync task failed: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }

                // Schedule async callback
                plugin.getServer().getAsyncScheduler().runNow(plugin, task -> {
                    try {
                        asyncCallback.accept(result);
                    } catch (Exception e) {
                        ConsoleUtils.severe("Async callback failed: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    /**
     * Runs the given task synchronously on the main server thread.
     * This ensures the task executes on the main thread regardless of the calling thread,
     * making it safe for Bukkit API usage and shared state modification.
     *
     * If called from the main thread, the task executes immediately.
     * If called from another thread, the task is scheduled to run on the main thread.
     *
     * @param plugin   The plugin instance used to schedule the task if not already on main thread.
     * @param syncTask A Runnable containing the logic to be executed on the main thread.
     */
    public static void runSync(Plugin plugin, Runnable syncTask) {
        if (Bukkit.isPrimaryThread()) {
            try {
                syncTask.run();
            } catch (Exception e) {
                ConsoleUtils.severe("Sync task failed: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    syncTask.run();
                } catch (Exception e) {
                    ConsoleUtils.severe("Sync task failed: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }
}
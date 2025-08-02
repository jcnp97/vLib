package asia.virtualmc.vLib.utilities.paper;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskUtils {

    /**
     * Runs a repeating task using the Paper GlobalRegionScheduler at a fixed interval.
     *
     * @param plugin  the plugin instance running the task
     * @param task    the task to execute
     * @param interval the interval between executions in seconds
     * @return the scheduled repeating task
     */
    public static ScheduledTask repeating(Plugin plugin, Runnable task, double interval) {
        long intervalTicks = (long) (interval * 20);

        return plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
                plugin,
                t -> task.run(),
                intervalTicks,
                intervalTicks
        );
    }

    /**
     * Runs a repeating task using the Paper GlobalRegionScheduler with a fixed interval,
     * and cancels it automatically after the specified duration.
     *
     * @param plugin   the plugin instance running the task
     * @param task     the task to execute
     * @param interval the interval between executions in seconds
     * @param duration the total duration before the task is cancelled, in seconds
     * @return the scheduled repeating task
     */
    public static ScheduledTask repeating(Plugin plugin, Runnable task, double interval, double duration) {
        long intervalTicks = (long) (interval * 20);
        long durationTicks = (long) (duration * 20);

        ScheduledTask repeating = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
                plugin,
                t -> task.run(),
                intervalTicks,
                intervalTicks
        );

        plugin.getServer().getGlobalRegionScheduler().runDelayed(
                plugin,
                t -> repeating.cancel(),
                durationTicks
        );

        return repeating;
    }

    /**
     * Runs a repeating task asynchronously using the Paper AsyncScheduler at a fixed interval.
     *
     * @param plugin   the plugin instance running the task
     * @param task     the task to execute
     * @param interval the interval between executions in seconds
     * @return the scheduled repeating async task
     */
    public static ScheduledTask repeatingAsync(Plugin plugin, Runnable task, double interval) {
        long intervalMillis = (long) (interval * 1000);

        return plugin.getServer().getAsyncScheduler().runAtFixedRate(
                plugin,
                scheduledTask -> task.run(),
                intervalMillis,
                intervalMillis,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * Runs a repeating task using the Paper GlobalRegionScheduler at a fixed interval,
     * and cancels it automatically after executing the specified number of times.
     *
     * @param plugin   the plugin instance running the task
     * @param task     the task to execute
     * @param interval the interval between executions in seconds
     * @param count    the number of times the task should execute before cancelling
     * @return the scheduled repeating task
     */
    public static ScheduledTask repeating(Plugin plugin, Runnable task, double interval, int count) {
        long intervalTicks = (long) (interval * 20);
        AtomicInteger executionCount = new AtomicInteger(0);

        return plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(
                plugin,
                scheduledTask -> {
                    // increment and run
                    if (executionCount.incrementAndGet() >= count) {
                        task.run();
                        scheduledTask.cancel();
                    } else {
                        task.run();
                    }
                },
                intervalTicks,
                intervalTicks
        );
    }
}
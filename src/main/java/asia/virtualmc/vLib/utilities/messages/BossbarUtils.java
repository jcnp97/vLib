package asia.virtualmc.vLib.utilities.messages;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class BossbarUtils {

    /**
     * Converts a custom {@link EnumsLib.BossBarColor} to an Adventure {@link net.kyori.adventure.bossbar.BossBar.Color}.
     *
     * @param color The custom color enum to toComponent.
     * @return The corresponding BossBar.Color value.
     */
    public static BossBar.Color getColor(EnumsLib.BossBarColor color) {
        return switch (color) {
            case BLUE -> BossBar.Color.BLUE;
            case GREEN -> BossBar.Color.GREEN;
            case PINK -> BossBar.Color.PINK;
            case PURPLE -> BossBar.Color.PURPLE;
            case RED -> BossBar.Color.RED;
            case WHITE -> BossBar.Color.WHITE;
            case YELLOW -> BossBar.Color.YELLOW;
        };
    }

    /**
     * Creates a new {@link net.kyori.adventure.bossbar.BossBar} with the given message, color, and progress.
     * The message is toComponented using AdventureUtils, and progress is clamped between 0.0 and 1.0.
     *
     * @param message  The text to display in the boss bar.
     * @param color    The custom color to use.
     * @param progress The progress value between 0.0 and 1.0.
     * @return A configured BossBar instance.
     */
    public static BossBar get(String message, EnumsLib.BossBarColor color, float progress) {
        Component component = AdventureUtils.toComponent(message);
        float progressLimit = Math.max(0.0f, Math.min(progress, 1.0f));

        return BossBar.bossBar(
                component,
                progressLimit,
                getColor(color),
                BossBar.Overlay.PROGRESS
        );
    }

    /**
     * Updates the name and progress of an existing {@link net.kyori.adventure.bossbar.BossBar}.
     * The message is re-Componented and progress clamped between 0.0 and 1.0.
     *
     * @param bossBar  The BossBar to modify.
     * @param message  The new message to display.
     * @param progress The new progress value (clamped between 0.0 and 1.0).
     */
    public static void modify(BossBar bossBar, String message, float progress) {
        Component component = AdventureUtils.toComponent(message);
        float progressLimit = Math.max(0.0f, Math.min(progress, 1.0f));

        bossBar.name(component);
        bossBar.progress(progressLimit);
    }

    /**
     * Shows a {@link net.kyori.adventure.bossbar.BossBar} to the specified player for a set duration (in seconds).
     * Automatically hides the boss bar after the duration expires.
     *
     * @param player   The player to show the boss bar to.
     * @param bossBar  The BossBar instance to display.
     * @param duration The duration in seconds before the boss bar is hidden.
     */
    public static void show(Player player, BossBar bossBar, double duration) {
        player.showBossBar(bossBar);
        player.getServer().getScheduler().runTaskLater(Main.getInstance(), task -> {
            player.hideBossBar(bossBar);
        }, (long) (duration * 20L));
    }
}

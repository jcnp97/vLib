package asia.virtualmc.vLib.utilities.messages;

import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class TitleUtils {

    /**
     * Sends a title and subtitle to the specified player using default display timings.
     * Converts both strings to Adventure components and shows the title if the player is online.
     *
     * @param player   The player to receive the title.
     * @param title    The main title text.
     * @param subtitle The subtitle text.
     */
    public static void send(@NotNull Player player, String title, String subtitle) {
        if (player.isOnline()) {
            Title fullTitle = Title.title(AdventureUtils.toComponent(title),
                    AdventureUtils.toComponent(subtitle));
            player.showTitle(fullTitle);
        }
    }

    /**
     * Sends a title and subtitle to the specified player with a custom stay duration.
     * Fade-in and fade-out durations are set to 0.
     * Converts both strings to Adventure components and shows the title if the player is online.
     *
     * @param player   The player to receive the title.
     * @param title    The main title text.
     * @param subtitle The subtitle text.
     * @param duration The duration (in milliseconds) the title should stay visible.
     */
    public static void send(@NotNull Player player, String title, String subtitle, long duration) {
        if (player.isOnline()) {
            Title.Times TITLE_TIMES = Title.Times.times(Duration.ZERO, Duration.ofMillis(duration), Duration.ZERO);
            Title fullTitle = Title.title(AdventureUtils.toComponent(title),
                    AdventureUtils.toComponent(subtitle), TITLE_TIMES);
            player.showTitle(fullTitle);
        }
    }

    /**
     * Sends a title and subtitle to all online players using default display timings.
     *
     * @param title    The main title text.
     * @param subtitle The subtitle text.
     */
    public static void sendAll(String title, String subtitle) {
        Title fullTitle = Title.title(
                AdventureUtils.toComponent(title),
                AdventureUtils.toComponent(subtitle)
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(fullTitle);
        }
    }

    /**
     * Sends a title and subtitle to all online players with a custom stay duration.
     * Fade-in and fade-out durations are set to 0.
     *
     * @param title    The main title text.
     * @param subtitle The subtitle text.
     * @param duration The duration (in milliseconds) the title should stay visible.
     */
    public static void sendAll(String title, String subtitle, long duration) {
        Title.Times times = Title.Times.times(
                Duration.ZERO,
                Duration.ofMillis(duration),
                Duration.ZERO
        );

        Title fullTitle = Title.title(
                AdventureUtils.toComponent(title),
                AdventureUtils.toComponent(subtitle),
                times
        );

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.showTitle(fullTitle);
        }
    }
}

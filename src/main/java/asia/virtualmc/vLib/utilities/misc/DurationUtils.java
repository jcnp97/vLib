package asia.virtualmc.vLib.utilities.misc;

import java.time.Duration;

public class DurationUtils {

    /**
     * Serializes a {@link Duration} into a storable long value (seconds).
     * If the Duration is null, returns 0.
     *
     * @param duration the Duration to serialize, may be null
     * @return duration in seconds as a long, or 0 if duration is null
     */
    public static long serialize(Duration duration) {
        return (duration == null) ? 0L : duration.getSeconds();
    }

    /**
     * Deserializes a stored seconds value into a {@link Duration}.
     * If the value is 0, returns null.
     *
     * @param seconds the seconds to deserialize
     * @return the corresponding Duration, or null if seconds is 0
     */
    public static Duration deserialize(long seconds) {
        return (seconds == 0L) ? null : Duration.ofSeconds(seconds);
    }

    /**
     * Formats a {@link Duration} into a human-readable string.
     * <p>
     * Example outputs:
     * <ul>
     *   <li>{@code "1h 30m 15s left"}</li>
     *   <li>{@code "45m 10s left"}</li>
     *   <li>{@code "20s left"}</li>
     *   <li>{@code text} if the duration is {@code null}, zero, or negative</li>
     * </ul>
     *
     * @param text     the fallback text to display (e.g., {@code "Ready"}) if duration is invalid or expired
     * @param duration the {@link Duration} to format; may be {@code null}
     * @return a formatted string representing the duration, or {@code text} if invalid or expired
     */
    public static String format(String text, Duration duration) {
        if (duration == null || duration.isZero() || duration.isNegative()) {
            return text;
        }

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s ");

        if (sb.isEmpty()) {
            return text;
        }

        sb.append("left");
        return sb.toString().trim();
    }
}

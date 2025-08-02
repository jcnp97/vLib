package asia.virtualmc.vLib.utilities.misc;

import java.time.Duration;
import java.time.Instant;

/**
 * Utility class for working with {@link Instant} time operations.
 */
public class InstantUtils {

    /**
     * Returns the current system time as an {@link Instant}.
     *
     * @return the current time
     */
    public static Instant getPresent() {
        return Instant.now();
    }

    /**
     * Returns an {@link Instant} representing the current time plus the specified number of seconds.
     *
     * @param seconds the number of seconds to add
     * @return the future time
     */
    public static Instant getFuture(long seconds) {
        return Instant.now().plusSeconds(seconds);
    }

    /**
     * Checks if the specified time has already passed (expired).
     *
     * @param time the time to check
     * @return true if the specified time is before the current time; false otherwise
     */
    public static boolean hasExpired(Instant time) {
        return Instant.now().isAfter(time);
    }

    /**
     * Checks if the specified time is still in the future.
     *
     * @param time the time to check
     * @return true if the specified time is after the current time; false otherwise
     */
    public static boolean isFuture(Instant time) {
        if (time == null) return false;

        return Instant.now().isBefore(time);
    }

    /**
     * Returns the number of seconds remaining until the specified time.
     * Returns 0 if the time has already passed.
     *
     * @param time the future time
     * @return the remaining seconds, or 0 if expired
     */
    public static long getRemainingSeconds(Instant time) {
        return Math.max(0, time.getEpochSecond() - Instant.now().getEpochSecond());
    }

    /**
     * Checks if the specified time is within a range of seconds from now.
     *
     * @param time         the target time
     * @param secondsRange the range in seconds (± range from now)
     * @return true if the time is within the range; false otherwise
     */
    public static boolean isWithin(Instant time, long secondsRange) {
        Instant now = Instant.now();
        return !now.isBefore(time.minusSeconds(secondsRange)) && !now.isAfter(time.plusSeconds(secondsRange));
    }

    /**
     * Converts a future {@link Instant} into a human-readable string showing
     * the time difference from the current moment in hours, minutes, or seconds.
     *
     * @param futureTime the future instant to compare with current time
     * @return a readable string (e.g., "2h 15m", "3m", or "45s")
     */
    public static String toReadableRemaining(Instant futureTime) {
        if (futureTime == null) {
            return "-";
        }

        Duration duration = Duration.between(Instant.now(), futureTime);

        long seconds = duration.getSeconds();
        if (seconds <= 0) return "0s";

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        if (hours > 0) return String.format("%dh %dm", hours, minutes);
        if (minutes > 0) return String.format("%dm", minutes);
        return String.format("%ds", secs);
    }

    /**
     * Returns the percentage of time elapsed between the start time and the future time,
     * using only second‐resolution.  Clamped to [0.0,100.0].
     */
    public static double getPercent(Instant startTime, Instant futureTime) {
        if (startTime == null || futureTime == null) {
            return 0.0;
        }
        Instant now = Instant.now();
        long totalSecs   = futureTime.getEpochSecond() - startTime.getEpochSecond();
        long elapsedSecs = now.getEpochSecond()    - startTime.getEpochSecond();

        if (totalSecs <= 0) {
            return 100.0;
        }
        double pct = (elapsedSecs / (double) totalSecs) * 100.0;
        return Math.max(0.0, Math.min(100.0, pct));
    }

    /**
     * Returns the epoch‐second equivalent, or null if the Instant is null.
     */
    public static Long serialize(Instant time) {
        return (time == null) ? null : time.getEpochSecond();
    }

    /**
     * Returns the Instant equivalent, or null if the Long value is null.
     */
    public static Instant deserialize(Long time) {
        if (time == null) {
            return null;
        }
        return Instant.ofEpochSecond(time);
    }

    /**
     * Returns a new Instant that lies partway between now and the given future Instant.
     * A multiplier of 0.5 will yield a point exactly halfway.
     *
     * @param futureTime the target Instant (must be after Instant.now())
     * @param multiplier a factor (e.g. 0.5 for halfway, 1.5 for 150%)
     * @return now + round((futureTime - now) × multiplier) in seconds
     * @throws IllegalArgumentException if futureTime is null or not in the future
     */
    public static Instant multiplyFuture(Instant futureTime, double multiplier) {
        if (futureTime == null) {
            throw new IllegalArgumentException("futureTime cannot be null");
        }
        Instant now = Instant.now();
        if (!futureTime.isAfter(now)) {
            throw new IllegalArgumentException("futureTime must be after now");
        }

        long remainingSeconds = futureTime.getEpochSecond() - now.getEpochSecond();
        long scaledSeconds    = Math.round(remainingSeconds * multiplier);
        return now.plusSeconds(scaledSeconds);
    }

    /**
     * Converts a duration in seconds into a human-readable format.
     *
     * @param seconds The total number of seconds to convert.
     * @return A string in the format of "Xd Xh Xm Xs", omitting units with zero values.
     */
    public static String getReadableTime(long seconds) {
        Duration duration = Duration.ofSeconds(seconds);

        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long secs = duration.getSeconds() % 60;

        StringBuilder formatted = new StringBuilder();
        if (days > 0) formatted.append(days).append("d ");
        if (hours > 0) formatted.append(hours).append("h ");
        if (minutes > 0) formatted.append(minutes).append("m ");
        if (secs > 0 || formatted.isEmpty()) formatted.append(secs).append("s");

        return formatted.toString().trim();
    }
}
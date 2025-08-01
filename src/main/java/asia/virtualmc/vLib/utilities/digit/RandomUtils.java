package asia.virtualmc.vLib.utilities.digit;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
    private static final Random random = new Random();

    /**
     * Returns whether a random chance succeeds based on a percentage.
     *
     * @param chance The chance percentage (0.0 to 100.0).
     * @return true if the random value is below the chance, false otherwise.
     */
    public static boolean get(double chance) {
        if (chance <= 0.0) return false;
        if (chance >= 100.0) return true;
        return random.nextDouble() * 100 < chance;
    }

    /**
     * Returns a random integer using {@link java.util.Random#nextInt()}.
     *
     * @return A randomly generated int.
     */
    public static int getInt() {
        return random.nextInt();
    }

    /**
     * Returns a random integer between the specified min (inclusive) and max (inclusive).
     * If min and max are equal, returns min.
     *
     * @param min The lower bound.
     * @param max The upper bound.
     * @return A random int between min and max (inclusive).
     */
    public static int getInt(int min, int max) {
        if (min == max) return min;
        return random.nextInt(Math.min(min, max), Math.max(min, max) + 1);
    }

    /**
     * Returns a random integer between 0 (inclusive) and max (inclusive).
     *
     * @param max The upper bound.
     * @return A random int between 0 and max (inclusive).
     */
    public static int getInt(int max) {
        return getInt(0, max);
    }

    /**
     * Returns a random double between 0.0 (inclusive) and 1.0 (exclusive).
     *
     * @return A random double in the range [0.0, 1.0).
     */
    public static double getDouble() {
        return random.nextDouble();
    }

    /**
     * Returns a random double between the specified min (inclusive) and max (exclusive).
     * If min and max are equal, returns min.
     *
     * @param min The lower bound.
     * @param max The upper bound.
     * @return A random double in the range [min, max).
     */
    public static double getDouble(double min, double max) {
        if (min == max) return min;
        return min + (max - min) * random.nextDouble();
    }

    /**
     * Returns a random string from the given list.
     *
     * @param list The list of strings to choose from.
     * @return A randomly selected string, or null if the list is null or empty.
     */
    public static String getString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        int index = ThreadLocalRandom.current().nextInt(list.size());
        return list.get(index);
    }
}

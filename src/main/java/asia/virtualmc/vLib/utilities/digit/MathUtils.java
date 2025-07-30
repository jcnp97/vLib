package asia.virtualmc.vLib.utilities.digit;

public class MathUtils {

    /**
     * Calculates the sum of all elements in a double array.
     * Returns 0.0 if the array is null or empty.
     *
     * @param array The array of doubles to sum.
     * @return The total sum of all values in the array, or 0.0 if null/empty.
     */
    public static double sum(double[] array) {
        if (array == null || array.length == 0) return 0.0;

        double sum = 0.0;
        for (double value : array) {
            sum += value;
        }
        return sum;
    }

    /**
     * Calculates the average (mean) value of a double array.
     * Returns 0.0 if the array is null or empty.
     *
     * @param array The array of doubles to average.
     * @return The average of all values in the array, or 0.0 if null/empty.
     */
    public static double average(double[] array) {
        if (array == null || array.length == 0) return 0.0;

        double sum = 0.0;
        for (double value : array) {
            sum += value;
        }
        return sum / array.length;
    }

    /**
     * Calculates the percentage of currentValue out of maxValue as a double.
     * Returns a value between 0.00 and 100.0.
     *
     * @param currentValue the current value
     * @param maxValue     the maximum value
     * @return percentage (0.00 to 100.0)
     */
    public static double percent(double currentValue, double maxValue) {
        if (maxValue <= 0.0) return 0.0;
        double result = (currentValue / maxValue) * 100.0;
        return Math.min(100.0, Math.max(0.0, result));
    }

    /**
     * Calculates the percentage of currentValue out of maxValue using integer values.
     * Returns a double between 0.00 and 100.0.
     *
     * @param currentValue the current integer value
     * @param maxValue     the maximum integer value
     * @return percentage (0.00 to 100.0)
     */
    public static double percent(int currentValue, int maxValue) {
        if (maxValue <= 0) return 0.0;
        double result = ((double) currentValue / maxValue) * 100.0;
        return Math.min(100.0, Math.max(0.0, result));
    }

    /**
     * Multiplies the given integer by the specified multiplier and returns
     * the result rounded to the nearest integer.
     *
     * @param value      the integer value to multiply
     * @param multiplier the multiplier to apply
     * @return the rounded result of (value * multiplier)
     */
    public static int multiply(int value, double multiplier) {
        return (int) Math.round(value * multiplier);
    }

    /**
     * Multiplies the given double by the specified multiplier and returns
     * the result rounded to the nearest integer.
     *
     * @param value      the double value to multiply
     * @param multiplier the multiplier to apply
     * @return the rounded result of (value * multiplier)
     */
    public static double multiply(double value, double multiplier) {
        return Math.round(value * multiplier);
    }

    /**
     * Multiplies the given value based on a percentage-like multiplier.
     * For example, a multiplier of 25 means +25% (1.25x), and -25 means -25% (0.75x).
     *
     * @param value      the base value to multiply
     * @param multiplier the percentage modifier (positive for increase, negative for decrease)
     * @return the resulting value after applying the percentage multiplier
     */
    public static double percentMultiply(double value, double multiplier) {
        double factor = 1.0 + (multiplier / 100.0);
        return value * factor;
    }

    /**
     * Multiplies the given integer value based on a percentage-like multiplier,
     * and returns the result rounded to the nearest integer.
     * For example, a multiplier of 25 means +25% (1.25x), and -25 means -25% (0.75x).
     *
     * @param value      the base integer value to multiply
     * @param multiplier the percentage modifier (positive for increase, negative for decrease)
     * @return the resulting integer value after applying the percentage multiplier, rounded
     */
    public static int percentMultiply(int value, double multiplier) {
        double factor = 1.0 + (multiplier / 100.0);
        return (int) Math.round(value * factor);
    }
}

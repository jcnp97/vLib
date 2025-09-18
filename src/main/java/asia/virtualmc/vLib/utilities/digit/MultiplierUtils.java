package asia.virtualmc.vLib.utilities.digit;

public class MultiplierUtils {
    /**
     * Calculates the percentage portion of the given integer value and always rounds up.
     * For example, value=50 and multiplier=1 (1%), the raw result is 0.5, which is rounded up to 1.
     *
     * @param value      the base integer value
     * @param multiplier the percentage (e.g. 1 = 1%, 100 = 100%)
     * @return the percentage portion of the value, rounded up as an integer
     */
    public static int getCeil(int value, double multiplier) {
        double result = value * (multiplier / 100.0);
        return (int) Math.ceil(result);
    }

    /**
     * Calculates the percentage portion of the given double value and always rounds up.
     * For example, value=50.0 and multiplier=1 (1%), the raw result is 0.5, which is rounded up to 1.0.
     *
     * @param value      the base double value
     * @param multiplier the percentage (e.g. 1 = 1%, 100 = 100%)
     * @return the percentage portion of the value, rounded up as a double
     */
    public static double getCeil(double value, double multiplier) {
        double result = value * (multiplier / 100.0);
        return Math.ceil(result);
    }

    /**
     * Calculates the percentage portion of the given long value and always rounds up.
     * For example, value=50 and multiplier=1 (1%), the raw result is 0.5, which is rounded up to 1.
     *
     * @param value      the base long value
     * @param multiplier the percentage (e.g. 1 = 1%, 100 = 100%)
     * @return the percentage portion of the value, rounded up as a long
     */
    public static long getCeil(long value, double multiplier) {
        double result = value * (multiplier / 100.0);
        return (long) Math.ceil(result);
    }
}

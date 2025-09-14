package asia.virtualmc.vLib.utilities.digit;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtils {

    /**
     * Rounds the given double value to two decimal places and returns it
     * in a simplified form (removing unnecessary trailing zeros).
     * <p>Examples:</p>
     * <ul>
     *     <li>2.8173819 → 2.82</li>
     *     <li>2.00 → 2</li>
     *     <li>1.90 → 1.9</li>
     * </ul>
     *
     * @param value the original double value
     * @return the rounded and simplified double value
     */
    public static double format(double value) {
        BigDecimal bd = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
        return bd.stripTrailingZeros().doubleValue();
    }

    /**
     * Formats a double value to the specified number of decimal places.
     * Uses {@link RoundingMode#HALF_UP} for rounding and strips trailing zeros.
     *
     * @param value    the double value to format
     * @param decimals the number of decimal places to keep
     * @return the formatted double with trailing zeros removed
     */
    public static double format(double value, int decimals) {
        BigDecimal bd = BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP);
        return bd.stripTrailingZeros().doubleValue();
    }

    /**
     * Returns the value rounded to the specified number of decimal places.
     *
     * @param value The original double value to round.
     * @param decimals The number of decimal places to keep.
     * @return The value rounded to the specified decimal places.
     */
    public static double precise(double value, int decimals) {
        String format = "%." + decimals + "f";
        return Double.parseDouble(String.format(format, value));
    }

    /**
     * Returns the value rounded to the specified number of decimal places.
     *
     * @param value The original float value to round.
     * @param decimals The number of decimal places to keep.
     * @return The value rounded to the specified decimal places.
     */
    public static float precise(float value, int decimals) {
        String format = "%." + decimals + "f";
        return Float.parseFloat(String.format(format, value));
    }

    /**
     * Converts a BigDecimal to a double value by rounding it to two decimal places first.
     * If the input is null, it will return 0.0 to avoid NullPointerException.
     *
     * @param value the BigDecimal to convert
     * @return the rounded double value with two decimal precision
     */
    public static double bigToDouble(BigDecimal value) {
        if (value == null) return 0.0;
        return value.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Converts a double value to a BigDecimal by first rounding it to two decimal places.
     *
     * @param value the double to convert
     * @return the BigDecimal with two decimal places
     */
    public static BigDecimal doubleToBig(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Attempts to parse a String into a float.
     *
     * @param input the string to parse
     * @return the parsed float, or null if invalid
     */
    public static float toFloat(String input) {
        if (input == null) {
            return 0;
        }
        try {
            return Float.parseFloat(input.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Attempts to parse a String into a double.
     * <p>
     * If the input is {@code null}, empty, or not a valid number,
     * this method will safely return {@code 0.0}.
     * </p>
     *
     * @param input the string to parse
     * @return the parsed double, or {@code 0.0} if invalid
     */
    public static double toDouble(String input) {
        if (input == null || input.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(input.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * Converts a string representation of a number into a {@code double}.
     * <p>
     * The input string may contain:
     * <ul>
     *   <li>Comma separators (e.g., "1,000,000.00")</li>
     *   <li>A decimal point</li>
     *   <li>An optional leading minus sign for negative values (e.g., "-123.45")</li>
     * </ul>
     * <p>
     * Any string containing invalid characters (letters, symbols other than {@code ,} or {@code .}),
     * or an incorrectly formatted number will cause a {@link NumberFormatException}.
     *
     * @param value the string to convert
     * @return the numeric {@code double} value represented by the string
     * @throws NumberFormatException if the input string is {@code null}, empty, or not a valid number
     */
    public static double stringToDouble(String value) {
        if (value == null || value.isEmpty()) {
            throw new NumberFormatException("Input string is null or empty");
        }

        boolean isNegative = false;
        if (value.startsWith("-")) {
            isNegative = true;
            value = value.substring(1);
        }

        if (!value.matches("[0-9.,]+")) {
            throw new NumberFormatException("Invalid characters in input: " + value);
        }

        value = value.replace(",", "");
        try {
            double result = Double.parseDouble(value);
            return isNegative ? -result : result;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Unable to parse double from input: " + value);
        }
    }
}

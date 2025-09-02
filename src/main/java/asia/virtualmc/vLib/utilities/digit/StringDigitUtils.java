package asia.virtualmc.vLib.utilities.digit;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;

import java.math.BigDecimal;

public class StringDigitUtils {

    /**
     * Formats a double value with comma separators and optional decimal places.
     * <p>
     * If the value has no decimal (e.g., 1000.00), decimals are not shown even if {@code hasDecimal} is true.
     * If the value has decimals and {@code hasDecimal} is true, it will display up to 2 decimal places.
     * If {@code hasDecimal} is false, only the integer part with commas is shown.
     *
     * @param value      The double value to format.
     * @param hasDecimal Whether to show decimals if they exist.
     * @return Formatted string representation of the value.
     */
    public static String formatDouble(double value, boolean hasDecimal) {
        if (value == (long) value) {
            return String.format("%,d", (long) value);
        }
        return hasDecimal ? String.format("%,.2f", value) : String.format("%,d", (long) value);
    }

    /**
     * Formats a BigDecimal value with comma separators and optional decimal places.
     * <p>
     * If the value has no decimal (e.g., 1000.00), decimals are not shown even if {@code hasDecimal} is true.
     * If the value has decimals and {@code hasDecimal} is true, it will display up to 2 decimal places.
     * If {@code hasDecimal} is false, only the integer part with commas is shown.
     *
     * @param value      The BigDecimal value to format.
     * @param hasDecimal Whether to show decimals if they exist.
     * @return Formatted string representation of the value.
     */
    public static String formatBigDecimal(BigDecimal value, boolean hasDecimal) {
        if (value == null) {
            ConsoleUtils.severe("BigDecimal value is null.");
            return "";
        }
        if (value.stripTrailingZeros().scale() <= 0) { // No decimal part
            return String.format("%,d", value.longValue());
        }
        return hasDecimal ? String.format("%,.2f", value.doubleValue())
                : String.format("%,d", value.longValue());
    }

    /**
     * Formats an integer value with comma separators.
     *
     * @param value The integer value to format.
     * @return Formatted string representation with commas.
     */
    public static String formatInteger(int value) {
        return String.format("%,d", value);
    }

    public static String formatLong(long value) {
        return String.format("%,d", value);
    }

    /**
     * Formats the given double value into shortened units (K, M, B, T) with up to two decimal places.
     * For example, 1150000 becomes "1.15M".
     *
     * @param value The double value to format.
     * @return A string representation of the value with unit suffix.
     */
    public static String formatInUnits(double value) {
        final double absValue = Math.abs(value);
        final String suffix;
        double scaledValue;

        if (absValue >= 1_000_000_000_000.0) {
            scaledValue = value / 1_000_000_000_000.0;
            suffix = "T";
        } else if (absValue >= 1_000_000_000.0) {
            scaledValue = value / 1_000_000_000.0;
            suffix = "B";
        } else if (absValue >= 1_000_000.0) {
            scaledValue = value / 1_000_000.0;
            suffix = "M";
        } else if (absValue >= 1_000.0) {
            scaledValue = value / 1_000.0;
            suffix = "K";
        } else {
            return formatDouble(value, true);
        }

        if (scaledValue == (long) scaledValue) {
            return String.format("%d%s", (long) scaledValue, suffix);
        } else {
            return String.format("%.2f%s", scaledValue, suffix);
        }
    }

    /**
     * Converts an integer to its corresponding Roman numeral representation.
     *
     * @param num the integer to convert
     * @return a string containing the Roman numeral equivalent of the input number
     */
    public static String toRoman(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                roman.append(symbols[i]);
                num -= values[i];
            }
        }

        return roman.toString();
    }
}

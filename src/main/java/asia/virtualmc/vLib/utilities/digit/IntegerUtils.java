package asia.virtualmc.vLib.utilities.digit;

import java.util.Set;

public class IntegerUtils {

    /**
     * Rounds the given double value to the nearest integer.
     *
     * @param value The double value to round.
     * @return The rounded integer value.
     */
    public static int roundToInt(double value) {
        return (int) Math.round(value);
    }

    /**
     * Parses the given string into an integer, stripping out non‑digit characters (except a leading minus).
     * Examples:
     *   "1000"    → 1000
     *   "1,234"   → 1234
     *   "-56px"   → -56
     *
     * @param value the string to convert
     * @return the parsed integer
     * @throws NumberFormatException if no valid digits are found
     */
    public static int toInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new NumberFormatException("Cannot parse integer from empty or null string");
        }
        String str = value.trim();
        boolean negative = str.startsWith("-");

        String digits = str.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            throw new NumberFormatException("No digits found in input: " + value);
        }
        if (negative) {
            digits = "-" + digits;
        }
        return Integer.parseInt(digits);
    }
}
package asia.virtualmc.vLib.utilities.text;

import java.util.regex.Pattern;

public class SplitUtils {

    /**
     * Splits the given string by the specified symbol (literal) and returns the segment at the given index.
     * If the index is out of bounds (>= parts.length), returns the last segment.
     * If the index is negative, returns the last segment as well.
     *
     * @param string the input string to split (if null, treated as empty)
     * @param symbol the delimiter to split by (literal, not regex; if null, returns the whole input)
     * @param index  the requested zero-based segment index; negative or too-large indices yield the last segment
     * @return the segment at the “clamped” index, never null
     */
    public static String split(String string, String symbol, int index) {
        if (string == null) {
            string = "";
        }

        if (symbol == null) {
            return string;
        }

        String[] parts = string.split(Pattern.quote(symbol), -1);
        int safeIndex = index < 0
                ? parts.length - 1
                : Math.min(index, parts.length - 1);
        return parts[safeIndex];
    }

    /**
     * Splits the given string by the specified symbol and returns the resulting array.
     * <p>
     * If the input string is {@code null}, it is treated as an empty string. If the symbol is {@code null},
     * the method returns a single-element array containing the original string.
     *
     * @param string the string to split (null-safe)
     * @param symbol the delimiter to use for splitting (literal, not regex; null returns single-element array)
     * @return a non-null array of string segments
     */
    public static String[] split(String string, String symbol) {
        if (string == null) string = "";
        if (symbol == null) return new String[] { string };
        return string.split(Pattern.quote(symbol), -1);
    }

    /**
     * Splits the given string by a symbol and removes the part at the specified index.
     * <p>
     * Rules for index adjustment:
     * <ul>
     *   <li>{@code -1} → removes the last element.</li>
     *   <li>{@code <= -2} → removes the first element.</li>
     *   <li>{@code >= parts.length} → removes the last element.</li>
     * </ul>
     * If the string or symbol is null/empty, or if there are no parts, the original string is returned.
     *
     * @param string the input string to be split
     * @param symbol the delimiter symbol used for splitting
     * @param index the position of the part to remove, with rules for special values
     * @return the string with the specified part removed, or the original string if invalid
     */
    public static String splitAndRemove(String string, String symbol, int index) {
        if (string == null || string.isEmpty() || symbol == null || symbol.isEmpty()) {
            return string;
        }

        String[] parts = string.split(symbol);

        if (parts.length == 0) {
            return string;
        }

        if (index == -1) {
            index = parts.length - 1;
        } else if (index <= -2) {
            index = 0; // remove first
        } else if (index >= parts.length) {
            index = parts.length - 1;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i != index) {
                if (!result.isEmpty()) {
                    result.append(symbol);
                }
                result.append(parts[i]);
            }
        }

        return result.toString();
    }
}

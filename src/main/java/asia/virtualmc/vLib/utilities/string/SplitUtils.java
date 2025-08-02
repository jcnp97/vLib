package asia.virtualmc.vLib.utilities.string;

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

}

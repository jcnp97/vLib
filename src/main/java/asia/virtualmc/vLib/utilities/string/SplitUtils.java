package asia.virtualmc.vLib.utilities.string;

import java.util.regex.Pattern;

public class SplitUtils {

    /**
     * Splits the given string by the specified symbol (literal) and returns the segment at the given index.
     * If the index is out of bounds (>= parts.length), returns the last segment.
     * If the index is negative, returns the last segment as well.
     *
     * @param str    the input string to split (if null, treated as empty)
     * @param symbol the delimiter to split by (literal, not regex; if null, returns the whole input)
     * @param index  the requested zero-based segment index; negative or too-large indices yield the last segment
     * @return the segment at the “clamped” index, never null
     */
    public static String split(String str, String symbol, int index) {
        if (str == null) {
            str = "";
        }
        if (symbol == null) {
            return str;
        }
        String[] parts = str.split(Pattern.quote(symbol), -1);
        int safeIndex = index < 0
                ? parts.length - 1
                : Math.min(index, parts.length - 1);
        return parts[safeIndex];
    }
}

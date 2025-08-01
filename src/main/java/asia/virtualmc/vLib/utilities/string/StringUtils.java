package asia.virtualmc.vLib.utilities.string;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {

    /**
     * Formats a string by replacing underscores with spaces, converting all characters to lowercase,
     * and capitalizing the first letter of each word.
     *
     * @param string The input string to format (e.g., "HELLO_WORLD" becomes "Hello World").
     * @return A human-readable version of the string with proper capitalization, or the original string if null or empty.
     */
    public static String format(String string) {
        if (string == null || string.isEmpty()) return string;

        String formatted = string.replace("_", " ").toLowerCase();
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : formatted.toCharArray()) {
            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
            if (c == ' ') {
                capitalizeNext = true;
            }
        }

        return result.toString();
    }

    /**
     * Converts a string to key format by:
     * - Removing all non-letter characters (only a–z and A–Z allowed),
     * - Replacing whitespace with underscores,
     * - Converting to lowercase.
     * Example: "Player's Data 123!" -> "players_data"
     *
     * @param string The input string.
     * @return A lowercase, underscore-separated string with only a-z characters.
     */
    public static String toKey(String string) {
        if (string == null || string.isEmpty()) return "";

        // Remove everything except letters and spaces
        String cleaned = string.replaceAll("[^a-zA-Z\\s]", "").trim();

        // Replace all whitespace with underscore, then convert to lowercase
        return cleaned.replaceAll("\\s+", "_").toLowerCase();
    }

    /**
     * Splits lines of lore into smaller lines with a maximum character count per line,
     * ensuring words are not split in the middle.
     *
     * @param stringList      the original list of strings to be split
     * @param charCount the maximum number of characters allowed per line
     * @return a new list of lore strings, formatted to fit within the specified character count
     */
    public static List<String> divide(List<String> stringList, int charCount) {
        List<String> formattedLore = new ArrayList<>();

        for (String line : stringList) {
            String[] words = line.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                if (currentLine.length() + word.length() + 1 > charCount) {
                    formattedLore.add(currentLine.toString().trim());
                    currentLine.setLength(0);
                }

                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }

            if (!currentLine.isEmpty()) {
                formattedLore.add(currentLine.toString());
            }
        }

        return formattedLore;
    }
}

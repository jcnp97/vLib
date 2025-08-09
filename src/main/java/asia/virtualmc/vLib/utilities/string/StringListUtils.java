package asia.virtualmc.vLib.utilities.string;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringListUtils {

    /**
     * Replaces occurrences of each key in the provided strings with the corresponding value.
     * Returns a new list with all replacements applied.
     *
     * @param input         the original list of strings to process
     * @param replacements  a map where each key is the substring to replace, and each value is the replacement string
     * @return a new List<String> where every occurrence of each key in each string has been replaced by its corresponding value
     */
    public static List<String> replace(List<String> input, Map<String, String> replacements) {
        return input.stream()
                .map(str -> {
                    String result = str;
                    for (var entry : replacements.entrySet()) {
                        result = result.replace(entry.getKey(), entry.getValue());
                    }
                    return result;
                })
                .collect(Collectors.toList());
    }

    /**
     * Replaces all occurrences of a specific substring within each string in the list.
     * Returns a new list with the replacements applied.
     *
     * @param input     the original list of strings to process
     * @param oldString the substring to be replaced
     * @param newString the string to replace with
     * @return a new List<String> where every occurrence of oldString in each string has been replaced by newString
     */
    public static List<String> replace(List<String> input, String oldString, String newString) {
        return input.stream()
                .map(str -> str.replace(oldString, newString))
                .collect(Collectors.toList());
    }


}

package asia.virtualmc.vLib.utilities.java;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class HashSetUtils {

    /**
     * Serializes a Set of Integer values into a single comma-separated String.
     *
     * @param set the Set of Integer to serialize; may be null or empty
     * @return a comma-separated String representation of the set; an empty string if the set is null or empty
     */
    public static String serializeInt(Set<Integer> set) {
        if (set == null || set.isEmpty()) {
            return "";
        }

        return set.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    /**
     * Deserializes a comma-separated String into a Set of Integer values.
     *
     * @param data the comma-separated String to parse; may be null or empty
     * @return a Set of Integer parsed from the input; an empty Set if the input is null, empty, or only contains whitespace
     * @throws NumberFormatException if any token in the string is not a valid integer
     */
    public static Set<Integer> deserializeInt(String data) {
        Set<Integer> result = new HashSet<>();
        if (data == null || data.trim().isEmpty()) {
            return result;
        }
        String[] tokens = data.split(",");
        for (String token : tokens) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) {
                result.add(Integer.parseInt(trimmed));
            }
        }

        return result;
    }

    /**
     * Serializes a Set of String values into a single comma-separated String.
     *
     * @param set the Set of String to serialize; may be null or empty
     * @return a comma-separated String representation of the set; an empty string if the set is null or empty
     */
    public static String serializeString(Set<String> set) {
        if (set == null || set.isEmpty()) {
            return "";
        }

        return String.join(",", set);
    }

    /**
     * Deserializes a comma-separated String into a Set of String values.
     *
     * @param data the comma-separated String to parse; may be null or empty
     * @return a Set of String parsed from the input; an empty Set if the input is null or empty
     */
    public static Set<String> deserializeString(String data) {
        Set<String> result = new HashSet<>();
        if (data == null || data.isEmpty()) {
            return result;
        }
        String[] tokens = data.split(",");
        result.addAll(Arrays.asList(tokens));

        return result;
    }

    /**
     * Parses a string in the format "start-end" and returns a Set<Integer>
     * containing all integers from start to end (inclusive).
     * If the input is invalid, an empty set is returned.
     *
     * @param value the string range (e.g. "1-100")
     * @return a set of integers representing the range
     */
    public static Set<Integer> getRange(String value) {
        Set<Integer> result = new HashSet<>();
        if (value == null || !value.contains("-")) {
            return result;
        }

        String[] parts = value.split("-");
        if (parts.length != 2) {
            return result;
        }

        try {
            int start = Integer.parseInt(parts[0].trim());
            int end = Integer.parseInt(parts[1].trim());

            if (start > end) {
                return result;
            }

            for (int i = start; i <= end; i++) {
                result.add(i);
            }
        } catch (NumberFormatException ignored) {

        }

        return result;
    }
}

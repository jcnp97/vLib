package asia.virtualmc.vLib.utilities.java;

import java.util.ArrayList;
import java.util.List;

public class ArrayUtils {

    /**
     * Converts a comma-separated string to an array of integers.
     *
     * @param input the input string (e.g., "1, 2, 3, 4, 5")
     * @return an array of integers parsed from the input string
     */
    public static int[] toIntArray(String input) {
        if (input == null || input.isBlank()) {
            return new int[0];
        }

        String[] tokens = input.split(",");
        List<Integer> tempList = new ArrayList<>();

        for (String token : tokens) {
            try {
                tempList.add(Integer.parseInt(token.trim()));
            } catch (NumberFormatException e) {
                // Skip invalid entries
            }
        }

        int[] result = new int[tempList.size()];
        for (int i = 0; i < tempList.size(); i++) {
            result[i] = tempList.get(i);
        }

        return result;
    }
}

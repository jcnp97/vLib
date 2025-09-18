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

    /**
     * Performs element-wise summation of two double arrays.
     * <p>
     * Both input arrays must have the same length. The result is a new array
     * where each index contains the sum of the corresponding elements from
     * {@code arr1} and {@code arr2}.
     * </p>
     *
     * <p>
     * If {@code positiveOnly} is {@code true}, any negative result is replaced
     * with {@code 0.0}. If {@code false}, results can be negative.
     * </p>
     *
     * @param arr1         the first array (must have the same length as arr2)
     * @param arr2         the second array (must have the same length as arr1)
     * @param positiveOnly if true, negative results are clamped to zero
     * @return a new double array containing the element-wise sums
     * @throws IllegalArgumentException if the arrays are not the same length
     */
    public static double[] sum(double[] arr1, double[] arr2, boolean positiveOnly) {
        if (arr1.length != arr2.length) {
            throw new IllegalArgumentException("Arrays must have the same length.");
        }

        double[] result = new double[arr1.length];
        for (int i = 0; i < arr1.length; i++) {
            double sum = arr1[i] + arr2[i];
            result[i] = positiveOnly && sum < 0 ? 0 : sum;
        }
        return result;
    }
}

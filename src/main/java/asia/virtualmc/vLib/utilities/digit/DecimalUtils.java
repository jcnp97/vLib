package asia.virtualmc.vLib.utilities.digit;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalUtils {

    /**
     * Converts a BigDecimal to a double value by rounding it to two decimal places first.
     * If the input is null, it will return 0.0 to avoid NullPointerException.
     *
     * @param value the BigDecimal to convert
     * @return the rounded double value with two decimal precision
     */
    public static double toDouble(BigDecimal value) {
        if (value == null) return 0.0;
        return value.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Converts a double value to a BigDecimal by first rounding it to two decimal places.
     *
     * @param value the double to convert
     * @return the BigDecimal with two decimal places
     */
    public static BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
    }
}

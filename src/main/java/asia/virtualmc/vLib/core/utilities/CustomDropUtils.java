package asia.virtualmc.vLib.core.utilities;

import asia.virtualmc.vLib.utilities.digit.MathUtils;
import asia.virtualmc.vLib.utilities.digit.RandomUtils;

import java.util.concurrent.ThreadLocalRandom;

public class CustomDropUtils {

    public static int get(double[] weights) {
        double totalWeight = MathUtils.sum(weights);
        if (totalWeight <= 0) return 0;

        double rand = RandomUtils.getDouble() * totalWeight;
        double cumulativeWeight = 0;

        for (int i = 0; i < weights.length; i++) {
            cumulativeWeight += weights[i];
            if (rand < cumulativeWeight) {
                return i + 1;
            }
        }

        throw new IllegalStateException("Unexpected state in DropUtils.get()");
    }

    public static int getAmount(double chance, int min, int count) {
        int result = 0;
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        for (int i = 0; i < count; i++) {
            if (rand.nextDouble(100.0) < chance) {
                result++;
            }
        }

        return (result == 0) ? min : result;
    }
}

package asia.virtualmc.vLib.core.guis;

public class GUIItemUtils {

    public static int[] getBarItem(double value, int count) {
        int[] models = new int[count];
        int totalChunks = (int) Math.floor(value / ((double) (100 / (count * 4))));

        for (int i = 0; i < count && totalChunks > 0; i++) {
            models[i] = Math.min(totalChunks, 4);
            totalChunks -= models[i];
        }
        return models;
    }
}

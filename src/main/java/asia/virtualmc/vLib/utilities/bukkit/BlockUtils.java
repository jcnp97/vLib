package asia.virtualmc.vLib.utilities.bukkit;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockUtils {

    /**
     * Sets blocks in a square area centered at the given location.
     *
     * @param location the center location
     * @param radius   1 = single block, 2 = 3x3 square, 3 = 5x5 square, etc.
     * @param material the material to place
     * @param override if true, replace non-air blocks as well
     */
    public static void setBlocks(Location location, int radius, Material material, boolean override) {
        if (location == null || material == null || radius < 1) return;

        int centerX = location.getBlockX();
        int centerY = location.getBlockY();
        int centerZ = location.getBlockZ();

        // Loop over square around center
        for (int x = centerX - (radius - 1); x <= centerX + (radius - 1); x++) {
            for (int z = centerZ - (radius - 1); z <= centerZ + (radius - 1); z++) {
                Block block = location.getWorld().getBlockAt(x, centerY, z);

                if (override || block.getType() == Material.AIR) {
                    block.setType(material, false);
                }
            }
        }
    }
}

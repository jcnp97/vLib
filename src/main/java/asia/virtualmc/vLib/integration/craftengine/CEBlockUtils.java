package asia.virtualmc.vLib.integration.craftengine;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.core.block.CustomBlock;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class CEBlockUtils {

    public static boolean replaceBlock(Block block, String id) {
        if (block == null || id == null) return false;
        Location blockLocation = block.getLocation();

        if (CraftEngineBlocks.isCustomBlock(block)) {
            if (CraftEngineBlocks.remove(block)) {
                placeBlock(id, blockLocation);
                return true;
            }
        } else {
            block.setType(Material.AIR);
            placeBlock(id, blockLocation);
            return true;
        }

        return false;
    }

    public static void placeBlock(String id, Location blockLocation) {
        CustomBlock customBlock = CraftEngineBlocks.byId(Key.of(id));
        if (customBlock == null) return;
        try {
            CraftEngineBlocks.place(blockLocation, customBlock.defaultState(), false);
        } catch (Exception e) {
            ConsoleUtils.severe("An error occurred when trying to place a custom block on " + blockLocation + ": " + e);
        }
    }
}

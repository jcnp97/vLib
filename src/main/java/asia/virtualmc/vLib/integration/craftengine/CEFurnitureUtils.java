package asia.virtualmc.vLib.integration.craftengine;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;

public class CEFurnitureUtils {

    public static boolean removeFurniture(BukkitFurniture furniture) {
        if (furniture == null) return false;
        CraftEngineFurniture.remove(furniture, false, false);
        return true;
    }

    public static boolean replaceFurniture(BukkitFurniture furniture, String id) {
        if (furniture == null || id == null) return false;
        Location furnLocation = furniture.location();
        try {
            CraftEngineFurniture.remove(furniture, false, false);
            CraftEngineFurniture.place(furnLocation, Key.of(id));
            return true;
        } catch (Exception e) {
            ConsoleUtils.severe("An error occurred when trying to place a custom block on " + furnLocation + ": " + e);
        }

        return false;
    }
}

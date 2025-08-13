package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.Material;

public class MaterialUtils {

    /**
     * Retrieves a {@link Material} from its name, ignoring case.
     * If the name is invalid, logs an error and returns {@link Material#PAPER} as the default.
     *
     * @param materialName the name of the material to retrieve
     * @return the matching {@link Material}, or {@link Material#PAPER} if invalid
     */
    public static Material getMaterial(String materialName) {
        try {
            return Material.valueOf(materialName.toUpperCase());
        } catch (IllegalStateException e) {
            ConsoleUtils.severe("Invalid material '" + materialName + "'. Returning default material..");
        }
        return Material.PAPER;
    }

    /**
     * Returns the name of the given {@link Material} as a string.
     * If the material is {@code null}, returns "AIR".
     *
     * @param material the {@link Material} to get the name of
     * @return the material name, or "AIR" if null
     */
    public static String getMaterial(Material material) {
        if (material == null) return "AIR";
        return material.name().toUpperCase();
    }
}

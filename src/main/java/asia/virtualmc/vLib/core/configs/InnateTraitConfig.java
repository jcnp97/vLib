package asia.virtualmc.vLib.core.configs;

import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.items.MaterialUtils;
import asia.virtualmc.vLib.utilities.java.HashSetUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class InnateTraitConfig {
    public record InnateTrait(
            Material material,
            String name,
            List<String> lore,
            String itemModel,
            Map<String, Double> effects,
            int slot,
            int maxLevel) {}

    public static Map<String, InnateTrait> getTraits(Plugin plugin, String filePath) {
        Map<String, InnateTrait> innateTraits = new HashMap<>();
        YamlFileService.YamlFile file = YamlFileService.get(plugin, filePath);

        // Information Section
        Section infoSec = file.getSection("information");
        Material infoMaterial = MaterialUtils.getMaterial(infoSec.getString("material"));
        String infoName = infoSec.getString("name");
        List<String> infoLore = infoSec.getStringList("lore");

        // Add information
        innateTraits.put("information", new InnateTrait(infoMaterial, infoName, infoLore, "", null,
                0, 0));

        // Talent Section
        Section traitSec = file.getSection("traitList");
        Set<String> keys = traitSec.getRoutesAsStrings(false);

        for (String key : keys) {
            Section section = traitSec.getSection(key);

            Material material = MaterialUtils.getMaterial(section.getString("material"));
            String name = section.getString("name");
            List<String> lore = section.getStringList("lore");
            int slot = section.getInt("slot");
            int maxLevel = section.getInt("max-level");
            Map<String, Double> effects = file.stringKeyDoubleMap(section.getSection("effects"), false);
            String itemModel = section.getString("item-model");

            innateTraits.put(key, new InnateTrait(material, name, lore, itemModel, effects, slot, maxLevel));
        }

        return innateTraits;
    }

    public static Map<Integer, Integer> getPoints(Plugin plugin, String filePath) {
        Map<Integer, Integer> points = new HashMap<>();
        YamlFileService.YamlFile file = YamlFileService.get(plugin, filePath);

        // Settings Section
        Section settingsSec = file.getSection("settings");
        List<String> pointsList = settingsSec.getStringList("points-gain");
        if (pointsList.isEmpty()) return points;

        for (String point : pointsList) {
            String[] parts = point.split(";");
            Set<Integer> levels = HashSetUtils.getRange(parts[0]);
            if (levels.isEmpty()) continue;

            int pointGain = IntegerUtils.toInt(parts[1]);
            for (int level : levels) {
                points.put(level, pointGain);
            }
        }

        return points;
    }
}

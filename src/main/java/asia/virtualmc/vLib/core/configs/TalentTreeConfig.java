package asia.virtualmc.vLib.core.configs;

import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.items.MaterialUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class TalentTreeConfig {
    public record Talent(
            Material material,
            String displayName,
            List<String> lore,
            String itemModel,
            int slot,
            int reqLevel,
            Map<String, Integer> required,
            String effect,
            double value,
            Set<String> paths,
            int cost,
            int maxLevel) {}

    public static Map<String, Talent> get(Plugin plugin, String filePath) {
        Map<String, Talent> talentTrees = new HashMap<>();
        YamlFileService.YamlFile file = YamlFileService.get(plugin, filePath);

        // Talent Section
        Section talentSec = file.getSection("talentList");
        Set<String> keys = talentSec.getRoutesAsStrings(false);

        for (String key : keys) {
            Section section = talentSec.getSection(key);

            Material material = MaterialUtils.getMaterial(section.getString("material"));
            String name = section.getString("name");
            List<String> lore = section.getStringList("lore");
            int slot = section.getInt("slot");
            int cost = section.getInt("cost");
            int maxLevel = section.getInt("max-level");
            int reqLevel = section.getInt("required_level");
            Map<String, Integer> required = file.stringKeyIntMap(section.getSection("required_talent"), false);
            String effect = section.getString("effect");
            double value = section.getDouble("value");
            String itemModel = section.getString("item-model");
            Set<String> paths = file.getStringSet(section, "paths");

            talentTrees.put(key, new Talent(material, name, lore, itemModel, slot,
                    reqLevel, required, effect, value, paths, cost, maxLevel));
        }

        return talentTrees;
    }
}

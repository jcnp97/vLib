package asia.virtualmc.vLib.core.configs;

import asia.virtualmc.vLib.services.file.YamlFileService;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RanksConfig {

    public record Rank(double points, String rankName) {}
    public static class RankData {
        public Map<String, Double> bonuses;
        public Map<Integer, Rank> ranks;

        public RankData(Map<String, Double> bonuses, Map<Integer, Rank> ranks) {
            this.bonuses = bonuses;
            this.ranks = ranks;
        }
    }

    public static RankData load(@NotNull Plugin plugin, @NotNull String fileName) {
        YamlFileService.YamlFile file = YamlFileService.get(plugin, fileName);
        String prefix = "[" + plugin.getName() + "]";

        try {
            Section bonusSection = file.getSection("xp_bonuses");
            Section ranksSection = file.getSection("ranksList");

            if (bonusSection == null || ranksSection == null) {
                ConsoleUtils.severe(prefix, "Error when trying to read " + file + ". Please check your configuration.");
                return null;
            }

            Map<String, Double> bonuses = file.stringKeyDoubleMap(bonusSection, false);
            Map<Integer, Rank> ranks = getRanks(prefix, ranksSection);
            return new RankData(bonuses, ranks);
        } catch (Exception e) {
            ConsoleUtils.severe(prefix, "An error occurred when trying to read " + fileName);
        }

        return null;
    }

    private static Map<Integer, Rank> getRanks(String prefix, Section section) {
        Map<Integer, Rank> ranks = new HashMap<>();
        Set<String> keys = section.getRoutesAsStrings(false);
        if (keys.isEmpty()) {
            ConsoleUtils.severe(prefix, "Section `ranksList` is empty! Skipping ranks creation..");
            return ranks;
        }

        double prevPoints = 0;

        for (String key : keys) {
            double points = section.getDouble(key + ".points");
            String rankName = section.getString(key + ".rank_name");
            if (prevPoints > points) {
                ConsoleUtils.severe(prefix, "Invalid configuration! Previous required points of " + rankName + " is higher than the next!");
                return ranks;
            }

            ranks.put(IntegerUtils.toInt(key), new Rank(points, rankName));
            prevPoints = points;
        }

        return ranks;
    }
}

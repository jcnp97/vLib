//package asia.virtualmc.vLib.core.player_data;
//
//import dev.dejvokep.boostedyaml.YamlDocument;
//import dev.dejvokep.boostedyaml.block.implementation.Section;
//import org.bukkit.plugin.Plugin;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.Map;
//import java.util.Set;
//
//public class RankUtils {
//
//    public static class Rank {
//        public String name;
//        public double points;
//
//        public Rank(String name, double points) {
//            this.name = name;
//            this.points = points;
//        }
//    }
//
//    public static void load(@NotNull Plugin plugin, @NotNull String fileName,
//                            Map<Integer, Rank> rankPoints, Map<String, Double> rankValues) {
//
//        // Clear Cache first
//        String name = plugin.getName();
//        rankPoints.clear();
//        rankValues.clear();
//
//        YamlDocument yaml = YAMLUtils.getYaml(plugin, fileName);
//
//        if (yaml == null) {
//            ConsoleUtils.severe(name, "Couldn't find " + fileName + ". Skipping rank creation..");
//            return;
//        }
//
//        rankValues.putAll(YAMLUtils.getDoubleMap(yaml, "xp_bonuses"));
//        rankValues.putAll(YAMLUtils.getDoubleMap(yaml, "points_calculation"));
//
//        Section section = YAMLUtils.getSection(yaml, "ranksList");
//        if (section == null) {
//            ConsoleUtils.severe(name, "Couldn't find " + fileName + ". Skipping rank creation..");
//            return;
//        }
//
//        Set<String > keys = section.getRoutesAsStrings(false);
//        for (String key : keys) {
//            int points = section.getInt(key + ".points");
//            String rankName = section.getString(key + ".rank_name");
//            rankPoints.put(DigitUtils.toInt(key), new Rank(rankName, points));
//        }
//    }
//}

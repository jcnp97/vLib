package asia.virtualmc.vLib.core.guis.skills.ranks;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface RankGUIHandler {
    // Player Data
    int getRankId(UUID uuid);
    double getCurrentPts(UUID uuid);

    // Rank Data
    double getRankPoints(int rankId);
    String getRankName(int rankId);
    String getRankTag(int rankId);
    Set<String> getStatistics();
    Map<String, Double> getBonuses();

    void incrementRankId(UUID uuid);
}

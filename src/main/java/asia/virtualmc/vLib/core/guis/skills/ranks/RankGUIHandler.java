package asia.virtualmc.vLib.core.guis.skills.ranks;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface RankGUIHandler {
    int getRankId(UUID uuid);
    double getRankPoints(UUID uuid);
    String getRankName(int rankId);
    String getNextRankName(int rankId);
    double getCurrentPts(UUID uuid);
    double getNextPts(int rankId);
    String getNextRankTag(int rankId);
    Set<String> getStatistics();
    Map<String, Double> getBonuses();

    void incrementRankId(UUID uuid);
    void upgradeEffects(Player player, String displayName, int newLevel);
}

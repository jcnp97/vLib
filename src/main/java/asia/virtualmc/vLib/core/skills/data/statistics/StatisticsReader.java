package asia.virtualmc.vLib.core.skills.data.statistics;

import java.util.Map;
import java.util.UUID;

public interface StatisticsReader {
    int get(UUID uuid, String statName);
    Map<String, Integer> getAll(UUID uuid);
}

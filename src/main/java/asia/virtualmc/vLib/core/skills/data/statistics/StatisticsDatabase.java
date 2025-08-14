package asia.virtualmc.vLib.core.skills.data.statistics;

import java.util.Map;
import java.util.UUID;

public interface StatisticsDatabase {
    Map<String, Integer> load(UUID uuid) throws Exception;
    void save(UUID uuid, Map<String, Integer> data) throws Exception;
    void saveAll(Map<UUID, Map<String, Integer>> snapshot) throws Exception;
}

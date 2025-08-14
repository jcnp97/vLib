package asia.virtualmc.vLib.core.skills.data.collection_log;

import java.util.Map;
import java.util.UUID;

public interface CollectionLogDatabase {
    Map<String, Integer> load(UUID uuid) throws Exception;
    void save(UUID uuid, Map<String, Integer> data) throws Exception;
    void saveAll(Map<UUID, Map<String, Integer>> snapshot) throws Exception;
}

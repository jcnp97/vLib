package asia.virtualmc.vLib.core.skills.data.collection_log;

import java.util.Map;
import java.util.UUID;

public interface CollectionLogReader {
    int get(UUID uuid, String itemName);
    Map<String, Integer> getAll(UUID uuid);
}

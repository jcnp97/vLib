package asia.virtualmc.vLib.core.skills.data.player_data;

import java.util.Map;
import java.util.UUID;

public interface DataReader {
    int get(UUID uuid, String key);
    Map<String, Integer> getAll(UUID uuid);
}
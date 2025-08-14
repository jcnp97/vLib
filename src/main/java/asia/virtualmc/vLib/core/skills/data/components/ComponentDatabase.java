package asia.virtualmc.vLib.core.skills.data.components;

import java.util.Map;
import java.util.UUID;

public interface ComponentDatabase {
    Components load(UUID uuid) throws Exception;
    void save(UUID uuid, Components data) throws Exception;
    void saveAll(Map<UUID, Components> snapshot) throws Exception;
}
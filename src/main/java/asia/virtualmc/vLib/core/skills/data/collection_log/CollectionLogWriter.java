package asia.virtualmc.vLib.core.skills.data.collection_log;

import java.util.UUID;

public interface CollectionLogWriter {
    void load(UUID uuid);
    void save(UUID uuid);
    void saveAll();
    void unload(UUID uuid);

    void add(UUID uuid, String itemName, int amount);
    void subtract(UUID uuid, String itemName, int amount);
    void set(UUID uuid, String itemName, int amount);
    void increment(UUID uuid, String itemName);
    void decrement(UUID uuid, String itemName);
}

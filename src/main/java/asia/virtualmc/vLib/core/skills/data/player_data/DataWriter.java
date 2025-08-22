package asia.virtualmc.vLib.core.skills.data.player_data;

import java.util.UUID;

public interface DataWriter {
    void load(UUID uuid);
    void save(UUID uuid);
    void saveAll();
    void unload(UUID uuid);

    void add(UUID uuid, String key, int amount);
    void subtract(UUID uuid, String key, int amount);
    void set(UUID uuid, String key, int amount);
    void increment(UUID uuid, String key);
    void decrement(UUID uuid, String key);
}

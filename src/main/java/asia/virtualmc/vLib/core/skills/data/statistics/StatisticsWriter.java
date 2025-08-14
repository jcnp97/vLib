package asia.virtualmc.vLib.core.skills.data.statistics;

import java.util.UUID;

public interface StatisticsWriter {
    void load(UUID uuid);
    void save(UUID uuid);
    void saveAll();
    void unload(UUID uuid);

    void add(UUID uuid, String statName, int amount);
    void subtract(UUID uuid, String statName, int amount);
    void set(UUID uuid, String statName, int amount);
    void increment(UUID uuid, String statName);
    void decrement(UUID uuid, String statName);
}

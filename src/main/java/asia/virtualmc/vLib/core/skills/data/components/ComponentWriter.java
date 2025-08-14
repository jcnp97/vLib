package asia.virtualmc.vLib.core.skills.data.components;

import java.util.UUID;

public interface ComponentWriter {
    void add(UUID uuid, ComponentType type, int amount);
    void addAll(UUID uuid, int amount);
    void subtractAll(UUID uuid, int amount);
    void add(UUID uuid, int[] amounts);
    void subtract(UUID uuid, int[] amounts);

    void load(UUID uuid);
    void save(UUID uuid);
    void saveAll();
    void unload(UUID uuid);
}
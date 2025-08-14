package asia.virtualmc.vLib.core.skills.data.components;

import java.util.UUID;

public interface ComponentReader {
    int get(UUID uuid, ComponentType type);
    int[] getAll(UUID uuid);
}

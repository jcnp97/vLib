package asia.virtualmc.vLib.core.skills.data.statistics;

import java.util.Set;

public interface Statistics {
    Set<String> keys();
    default boolean contains(String name) {
        return keys().contains(name);
    }
}

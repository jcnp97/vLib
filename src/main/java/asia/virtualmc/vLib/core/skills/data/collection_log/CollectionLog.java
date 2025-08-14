package asia.virtualmc.vLib.core.skills.data.collection_log;

import java.util.Set;

public interface CollectionLog {
    Set<String> keys();
    default boolean contains(String name) {
        return keys().contains(name);
    }
}

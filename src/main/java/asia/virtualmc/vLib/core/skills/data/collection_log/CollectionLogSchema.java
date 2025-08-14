package asia.virtualmc.vLib.core.skills.data.collection_log;

import java.util.Set;

public final class CollectionLogSchema implements CollectionLog {
    private final Set<String> keys;

    public CollectionLogSchema(Set<String> keys) {
        this.keys = Set.copyOf(keys);
    }

    @Override public Set<String> keys() { return keys; }
}

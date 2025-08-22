package asia.virtualmc.vLib.core.skills.data.player_data;

import java.util.Set;

public final class GenericSchema implements DataSchema {
    private final Set<String> keys;
    private final String tableName;

    public GenericSchema(Set<String> keys, String tableName) {
        this.keys = Set.copyOf(keys);
        this.tableName = tableName;
    }

    @Override public Set<String> keys() { return keys; }
    @Override public String tableName() { return tableName; }
}

package asia.virtualmc.vLib.core.skills.data.player_data;

import java.util.Set;

public interface DataSchema {
    Set<String> keys();
    String tableName();

    default boolean contains(String name) {
        return keys().contains(name);
    }
}

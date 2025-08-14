package asia.virtualmc.vLib.core.skills.data.statistics;

import java.util.Set;

public final class StatisticsSchema implements Statistics {
    private final Set<String> keys;

    public StatisticsSchema(Set<String> keys) {
        this.keys = Set.copyOf(keys);
    }

    @Override public Set<String> keys() { return keys; }
}

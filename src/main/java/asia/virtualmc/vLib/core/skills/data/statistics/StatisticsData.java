package asia.virtualmc.vLib.core.skills.data.statistics;

import asia.virtualmc.vLib.utilities.paper.AsyncUtils;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsData implements StatisticsReader, StatisticsWriter {
    private final Plugin plugin;
    private final StatisticsDatabase database;
    private final Statistics statistics;
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, Integer>> cache = new ConcurrentHashMap<>();

    public StatisticsData(Plugin plugin, StatisticsDatabase database, Statistics statistics) {
        this.plugin = plugin;
        this.database = database;
        this.statistics = statistics;
    }

    @Override
    public void load(UUID uuid) {
        try {
            Map<String, Integer> data = database.load(uuid);
            cache.put(uuid, new ConcurrentHashMap<>(data));
        } catch (Exception ignored) {}
    }

    @Override
    public void save(UUID uuid) {
        Map<String, Integer> map = cache.get(uuid);
        if (map == null) return;
        try {
            database.save(uuid, new HashMap<>(map));
        } catch (Exception ignored) {}
    }

    @Override
    public void saveAll() {
        Map<UUID, Map<String, Integer>> snapshot = new HashMap<>();
        for (var e : cache.entrySet()) {
            snapshot.put(e.getKey(), new HashMap<>(e.getValue()));
        }
        try {
            database.saveAll(snapshot);
        } catch (Exception ignored) {}
    }

    @Override
    public void unload(UUID uuid) {
        try { save(uuid); } finally { cache.remove(uuid); }
    }

    @Override
    public int get(UUID uuid, String statName) {
        if (!statistics.contains(statName)) return 0;
        Map<String, Integer> m = cache.get(uuid);
        return (m == null) ? 0 : m.getOrDefault(statName, 0);
    }

    @Override
    public Map<String, Integer> getAll(UUID uuid) {
        Map<String, Integer> m = cache.get(uuid);
        return (m == null) ? Collections.emptyMap() : new HashMap<>(m);
    }

    @Override
    public void add(UUID uuid, String statName, int amount) {
        if (!statistics.contains(statName)) return;
        Map<String, Integer> m = cache.get(uuid);
        if (m == null) return;
        m.merge(statName, amount, Integer::sum);
        AsyncUtils.runAsync(plugin, () -> {
            save(uuid);
        });
    }

    @Override
    public void subtract(UUID uuid, String statName, int amount) {
        add(uuid, statName, -amount);
    }

    @Override
    public void increment(UUID uuid, String statName) { add(uuid, statName, 1); }

    @Override
    public void decrement(UUID uuid, String statName) { add(uuid, statName, -1); }

    @Override
    public void set(UUID uuid, String statName, int amount) {
        if (!statistics.contains(statName)) return;
        Map<String, Integer> m = cache.get(uuid);
        if (m == null) return;
        m.put(statName, amount);
        AsyncUtils.runAsync(plugin, () -> {
            save(uuid);
        });
    }
}

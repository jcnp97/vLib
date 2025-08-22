package asia.virtualmc.vLib.core.skills.data.player_data;

import asia.virtualmc.vLib.utilities.paper.AsyncUtils;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GenericDataService implements DataReader, DataWriter {
    private final Plugin plugin;
    private final GenericDatabase database;
    private final DataSchema schema;
    private final ConcurrentHashMap<UUID, ConcurrentHashMap<String, Integer>> cache = new ConcurrentHashMap<>();

    public GenericDataService(Plugin plugin, GenericDatabase database, DataSchema schema) {
        this.plugin = plugin;
        this.database = database;
        this.schema = schema;
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
    public int get(UUID uuid, String key) {
        if (!schema.contains(key)) return 0;
        Map<String, Integer> m = cache.get(uuid);
        return (m == null) ? 0 : m.getOrDefault(key, 0);
    }

    @Override
    public Map<String, Integer> getAll(UUID uuid) {
        Map<String, Integer> m = cache.get(uuid);
        return (m == null) ? Collections.emptyMap() : new HashMap<>(m);
    }

    @Override
    public void add(UUID uuid, String key, int amount) {
        if (!schema.contains(key)) return;
        Map<String, Integer> m = cache.get(uuid);
        if (m == null) return;
        m.merge(key, amount, Integer::sum);
        AsyncUtils.runAsync(plugin, () -> {
            save(uuid);
        });
    }

    @Override
    public void subtract(UUID uuid, String key, int amount) {
        add(uuid, key, -amount);
    }

    @Override
    public void increment(UUID uuid, String key) { add(uuid, key, 1); }

    @Override
    public void decrement(UUID uuid, String key) { add(uuid, key, -1); }

    @Override
    public void set(UUID uuid, String key, int amount) {
        if (!schema.contains(key)) return;
        Map<String, Integer> m = cache.get(uuid);
        if (m == null) return;
        m.put(key, amount);
        AsyncUtils.runAsync(plugin, () -> {
            save(uuid);
        });
    }
}

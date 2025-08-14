package asia.virtualmc.vLib.core.skills.data.components;

import asia.virtualmc.vLib.utilities.paper.AsyncUtils;
import org.bukkit.plugin.Plugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ComponentData implements ComponentReader, ComponentWriter {
    private final Plugin plugin;
    private final ComponentDatabase database;
    private final ConcurrentHashMap<UUID, Components> cache = new ConcurrentHashMap<>();

    public ComponentData(Plugin plugin, ComponentDatabase database) {
        this.plugin = plugin;
        this.database = database;
    }

    @Override
    public void load(UUID uuid) {
        try {
            Components loaded = database.load(uuid);
            cache.put(uuid, loaded);
        } catch (Exception e) {
            // Todo: Add log
        }
    }

    @Override
    public void save(UUID uuid) {
        Components c = cache.get(uuid);
        if (c == null) return;
        try {
            database.save(uuid, c);
        } catch (Exception ignored) {}
    }

    @Override
    public void saveAll() {
        try {
            database.saveAll(new ConcurrentHashMap<>(cache));
        } catch (Exception ignored) {}
    }

    @Override
    public void unload(UUID uuid) {
        try {
            save(uuid);
        } finally {
            cache.remove(uuid);
        }
    }

    @Override
    public int get(UUID uuid, ComponentType type) {
        Components c = cache.get(uuid);
        return (c == null) ? 0 : c.get(type);
    }

    @Override
    public int[] getAll(UUID uuid) {
        Components c = cache.get(uuid);
        return (c == null) ? new int[7] : c.toArray();
    }

    @Override
    public void add(UUID uuid, ComponentType type, int amount) {
        Components components = cache.get(uuid);
        if (components == null) return;
        components.add(type, amount);
        AsyncUtils.runAsync(plugin, () -> {
            save(uuid);
        });
    }

    @Override
    public void addAll(UUID uuid, int amount) {
        Components components = cache.computeIfAbsent(uuid, k -> new Components());
        components.addAll(amount);
        AsyncUtils.runAsync(plugin, () -> {
            save(uuid);
        });
    }

    @Override
    public void subtractAll(UUID uuid, int amount) {
        Components components = cache.computeIfAbsent(uuid, k -> new Components());
        components.addAll(-amount);
        AsyncUtils.runAsync(plugin, () -> {
            save(uuid);
        });
    }

    @Override
    public void add(UUID uuid, int[] amounts) {
        Components components = cache.computeIfAbsent(uuid, k -> new Components());
        components.addArray(amounts);
        AsyncUtils.runAsync(plugin, () -> {
            save(uuid);
        });
    }

    @Override
    public void subtract(UUID uuid, int[] amounts) {
        int[] values = amounts.clone();
        for (int i = 0; i < values.length; i++) values[i] = -values[i];
        add(uuid, values);
    }
}

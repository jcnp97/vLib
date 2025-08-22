package asia.virtualmc.vLib.core.skills.data.player_data;

import asia.virtualmc.vLib.storage.mysql.misc.StringKeyDatabase;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GenericDBAdapter implements GenericDatabase {
    private final Plugin plugin;
    private final List<String> keys;
    private final String tableName;

    public GenericDBAdapter(Plugin plugin, DataSchema schema) {
        this.plugin = plugin;
        this.keys = List.copyOf(schema.keys());
        this.tableName = schema.tableName();
        StringKeyDatabase.createTable(plugin, tableName);
    }

    @Override
    public Map<String, Integer> load(UUID uuid) throws Exception {
        var map = StringKeyDatabase.loadPlayerData(plugin, tableName, uuid, keys);
        Map<String, Integer> result = new HashMap<>();
        for (String k : keys) result.put(k, map.getOrDefault(k, 0));
        return result;
    }

    @Override
    public void save(UUID uuid, Map<String, Integer> data) throws Exception {
        StringKeyDatabase.savePlayerData(plugin, tableName, uuid, data);
    }

    @Override
    public void saveAll(Map<UUID, Map<String, Integer>> snapshot) throws Exception {
        StringKeyDatabase.saveAllData(plugin, tableName, snapshot);
    }
}


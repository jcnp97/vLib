package asia.virtualmc.vLib.core.skills.data.statistics;

import asia.virtualmc.vLib.storage.mysql.misc.StringKeyDatabase;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatsDBAdapter implements StatisticsDatabase {
    private static final String tableName = "statistics";

    private final Plugin plugin;
    private final List<String> keys;

    public StatsDBAdapter(Plugin plugin,
                          Statistics statistics) {
        this.plugin = plugin;
        this.keys = List.copyOf(statistics.keys());
        StringKeyDatabase.createTable(plugin, tableName);
    }

    @Override
    public Map<String, Integer> load(UUID uuid) throws Exception {
        ConcurrentHashMap<String, Integer> map =
                StringKeyDatabase.loadPlayerData(plugin, tableName, uuid, keys);

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

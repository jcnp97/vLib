package asia.virtualmc.vLib.core.skills.data.components;

import asia.virtualmc.vLib.storage.mysql.misc.StringKeyDatabase;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentDBAdapter implements ComponentDatabase {
    private static final String tableName = "components";

    private static final List<String> KEYS = Arrays.asList(
            "common_components","uncommon_components","rare_components",
            "unique_components","epic_components","mythical_components","exotic_components"
    );

    private final Plugin plugin;

    public ComponentDBAdapter(Plugin plugin) {
        this.plugin = plugin;
        StringKeyDatabase.createTable(plugin, tableName);
    }

    @Override
    public Components load(UUID playerId) throws Exception {
        ConcurrentHashMap<String, Integer> map =
                StringKeyDatabase.loadPlayerData(plugin, tableName, playerId, KEYS);

        // map -> Components
        Components c = new Components();
        setFromMap(c, map);
        return c;
    }

    @Override
    public void save(UUID playerId, Components data) throws Exception {
        Map<String,Integer> map = toMap(data);
        StringKeyDatabase.savePlayerData(plugin, tableName, playerId, map);
    }

    @Override
    public void saveAll(Map<UUID, Components> snapshot) throws Exception {
        Map<UUID, Map<String,Integer>> bulk = new HashMap<>();
        for (var e : snapshot.entrySet()) bulk.put(e.getKey(), toMap(e.getValue()));
        StringKeyDatabase.saveAllData(plugin, tableName, bulk);
    }

    private static Map<String,Integer> toMap(Components c) {
        int[] arr = c.toArray();
        Map<String,Integer> m = new HashMap<>(7);
        for (int i=0;i<7;i++) m.put(KEYS.get(i), arr[i]);
        return m;
    }

    private static void setFromMap(Components c, Map<String,Integer> m) {
        for (int i=0;i<7;i++) {
            int v = m.getOrDefault(KEYS.get(i), 0);
            c.set(ComponentType.values()[i], v);
        }
    }
}

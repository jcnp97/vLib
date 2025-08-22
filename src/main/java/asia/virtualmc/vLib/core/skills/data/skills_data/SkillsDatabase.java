package asia.virtualmc.vLib.core.skills.data.skills_data;

import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;

/**
 * Abstraction for persisting and loading skill player stats.
 * Implementations can wrap MySQL, SQLite, or anything else.
 */
public interface SkillsDatabase {

    /**
     * Create backing table(s) for this service if needed.
     *
     * @param plugin    plugin context (for logging / schedulers if needed)
     * @param tableName database table name to use (per-skill or per-plugin)
     */
    void createTable(Plugin plugin, String tableName);

    /**
     * Load a single player's stats.
     *
     * @param plugin plugin context
     * @param table  table name
     * @param uuid   player uuid
     * @return loaded stats (never null; implementor should return sane defaults)
     */
    SkillsSnapshot load(Plugin plugin, String table, UUID uuid);

    /**
     * Persist a single player's stats.
     *
     * @param plugin plugin context
     * @param table  table name
     * @param uuid   player uuid
     * @param data   snapshot to persist
     */
    void save(Plugin plugin, String table, UUID uuid, SkillsSnapshot data);

    /**
     * Batch persist. Implementations may upsert in bulk for efficiency.
     *
     * @param plugin plugin context
     * @param table  table name
     * @param all    map of uuid -> snapshot
     */
    void saveAll(Plugin plugin, String table, Map<UUID, SkillsSnapshot> all);

    /**
     * Immutable data carrier for stats to keep vLib decoupled from DB model types.
     */
    record SkillsSnapshot(
            String name,
            double exp,
            double bxp,
            double xpm,
            int level,
            int luck,
            int traitPoints,
            int talentPoints,
            int wisdomTrait,
            int charismaTrait,
            int karmaTrait,
            int dexterityTrait
    ) {}
}

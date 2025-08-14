package asia.virtualmc.vLib.core.skills.data.player_data;

import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Wraps your existing static SkillsDatabase API so vLib uses it via {@link SkillsDatabase}.
 * Keeps your current SQL layer intact.
 */
public final class SkillsDatabaseAdapter implements SkillsDatabase {

    @Override
    public void createTable(Plugin plugin, String tableName) {
        asia.virtualmc.vLib.storage.mysql.skills.SkillsDatabase.createTable(plugin);
    }

    @Override
    public SkillsSnapshot load(Plugin plugin, String table, UUID uuid) {
        asia.virtualmc.vLib.storage.mysql.skills.SkillsDatabase.PlayerStats s = asia.virtualmc.vLib.storage.mysql.skills.SkillsDatabase.loadPlayerData(plugin, uuid);
        return new SkillsSnapshot(
                s.name, s.exp, s.bxp, s.xpm, s.level, s.luck, s.traitPoints, s.talentPoints,
                s.wisdomTrait, s.charismaTrait, s.karmaTrait, s.dexterityTrait
        );
    }

    @Override
    public void save(Plugin plugin, String table, UUID uuid, SkillsSnapshot d) {
        asia.virtualmc.vLib.storage.mysql.skills.SkillsDatabase.savePlayerData(
                plugin, uuid, d.name(), d.exp(), d.bxp(), d.xpm(), d.level(), d.luck(),
                d.traitPoints(), d.talentPoints(), d.wisdomTrait(), d.charismaTrait(),
                d.karmaTrait(), d.dexterityTrait()
        );
    }

    @Override
    public void saveAll(Plugin plugin, String table, Map<UUID, SkillsSnapshot> all) {
        Map<UUID, asia.virtualmc.vLib.storage.mysql.skills.SkillsDatabase.PlayerStats> raw = new HashMap<>();
        for (Map.Entry<UUID, SkillsSnapshot> e : all.entrySet()) {
            SkillsSnapshot d = e.getValue();
            raw.put(e.getKey(), new asia.virtualmc.vLib.storage.mysql.skills.SkillsDatabase.PlayerStats(
                    d.name(), d.exp(), d.bxp(), d.xpm(), d.level(), d.luck(),
                    d.traitPoints(), d.talentPoints(), d.wisdomTrait(), d.charismaTrait(),
                    d.karmaTrait(), d.dexterityTrait()
            ));
        }
        asia.virtualmc.vLib.storage.mysql.skills.SkillsDatabase.saveAllData(plugin, raw);
    }
}

package asia.virtualmc.vLib.core.skills.data.skills_data;

import asia.virtualmc.vLib.core.configs.InnateTraitConfig;
import asia.virtualmc.vLib.core.skills.utilities.SkillsDataUtils;
import asia.virtualmc.vLib.events.skills.SkillLevelUpEvent;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SkillsData implements SkillsWriter, SkillsReader {
    private final Plugin plugin;
    private final SkillsDatabase database;
    private final EnumsLib.Skills skill;
    private final int MAX_LEVEL;

    private final Map<Integer, Double> expTable;
    private final Map<String, InnateTraitConfig.InnateTrait> traits;
    private final Map<Integer, Integer> traitPoints;

    // Runtime cache
    private final ConcurrentHashMap<UUID, PlayerData> cache = new ConcurrentHashMap<>();
    private record PlayerData(
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
    ) {
        PlayerData withExp(double v) { return new PlayerData(name, v, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        PlayerData withBxp(double v) { return new PlayerData(name, exp, v, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        PlayerData withXpm(double v) { return new PlayerData(name, exp, bxp, v, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        PlayerData withLevel(int v) { return new PlayerData(name, exp, bxp, xpm, v, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        PlayerData withLuck(int v) { return new PlayerData(name, exp, bxp, xpm, level, v, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        PlayerData withTraitPoints(int v) { return new PlayerData(name, exp, bxp, xpm, level, luck, v, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        PlayerData withTalentPoints(int v) { return new PlayerData(name, exp, bxp, xpm, level, luck, traitPoints, v, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        PlayerData withWisdom(int v) { return new PlayerData(name, exp, bxp, xpm, level, luck, traitPoints, talentPoints, v, charismaTrait, karmaTrait, dexterityTrait); }
        PlayerData withCharisma(int v) { return new PlayerData(name, exp, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, v, karmaTrait, dexterityTrait); }
        PlayerData withKarma(int v) { return new PlayerData(name, exp, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, v, dexterityTrait); }
        PlayerData withDexterity(int v) { return new PlayerData(name, exp, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, v); }
        PlayerData withName(String v) { return new PlayerData(v, exp, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
    }

    /**
     * Construct a reusable PlayerData dataervice for one "skill".
     *
     * @param plugin          owning plugin
     * @param database      persistence adapter
     * @param expTable        level -> next exp requirement map
     * @param traits       traitName -> trait definition map
     */
    public SkillsData(
            @NotNull Plugin plugin,
            @NotNull SkillsDatabase database,
            @NotNull Map<Integer, Double> expTable,
            @NotNull Map<String, InnateTraitConfig.InnateTrait> traits,
            @NotNull Map<Integer, Integer> traitPoints,
            @NotNull EnumsLib.Skills skill,
            int maxLevel
    ) {
        this.plugin = plugin;
        this.database = database;
        this.expTable = expTable;
        this.traits = traits;
        this.traitPoints = traitPoints;
        this.database.createTable(plugin, plugin.getName().toLowerCase() + "_playerData");
        this.skill = skill;
        this.MAX_LEVEL = maxLevel;
    }

    // ---------- Lifecycle ----------

    /**
     * Loads a player's data from storage into memory cache.
     *
     * @param uuid player UUID
     */
    public void load(@NotNull UUID uuid) {
        try {
            SkillsDatabase.PlayerDataDB data = database.load(plugin, plugin.getName().toLowerCase() + "_playerData", uuid);
            cache.put(uuid, toMemoryData(data));
        } catch (Exception e) {
            ConsoleUtils.severe(prefix(), "Failed to load player data for " + uuid + " : " + e.getMessage());
        }
    }

    /**
     * Saves a single player's cached data to storage.
     *
     * @param uuid player UUID
     */
    public void save(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data == null) return;
        try {
            database.save(plugin, plugin.getName().toLowerCase() + "_playerData", uuid, toRepo(data));
        } catch (Exception e) {
            ConsoleUtils.severe(prefix(), "Failed to store player data for " + data.name + ": " + e.getMessage());
        }
    }

    /**
     * Saves all cached players to storage (best-effort).
     */
    public void saveAll() {
        try {
            Map<UUID, SkillsDatabase.PlayerDataDB> out = new java.util.HashMap<>();
            for (Map.Entry<UUID, PlayerData> e : cache.entrySet()) {
                out.put(e.getKey(), toRepo(e.getValue()));
            }
            database.saveAll(plugin, plugin.getName().toLowerCase() + "_playerData", out);
        } catch (Exception e) {
            ConsoleUtils.severe(prefix(), "Failed to store all player data: " + e.getMessage());
        }
    }

    /**
     * Flushes and removes a player's data from cache.
     *
     * @param uuid player UUID
     */
    public void unload(@NotNull UUID uuid) {
        String name = getPlayerName(uuid);
        try {
            save(uuid);
        } catch (Exception ignored) {
        } finally {
            cache.remove(uuid);
        }
        if (name == null) {
            ConsoleUtils.info(prefix(), "Unloaded player data for " + uuid);
        }
    }

    // ---------- Update methods ----------

    public void updateEXP(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, double value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;

        cache.put(uuid, data.withExp(SkillsDataUtils.getEXP(type, data.exp, value)));
        if (type == EnumsLib.UpdateType.ADD) checkLevelUp(uuid);
//        if (type == EnumsLib.UpdateType.ADD) {
//            //double bonus = consumeBonusXp(uuid, value);
//            double newExp = SkillsDataUtils.getEXP(type, data.exp, value + bonus);
//            cache.put(uuid, data.withExp(newExp));
//
//        } else {
//
//        }
    }

    public void updateLevel(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, int value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withLevel(SkillsDataUtils.getLevel(type, data.level, value)));
    }

    public void updateName(@NotNull UUID uuid, String name) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withName(name));
    }

    public void updateXPM(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, double value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withXpm(SkillsDataUtils.getXPM(type, data.xpm, value)));
    }

    public void updateBXP(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, double value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withBxp(SkillsDataUtils.getBXP(type, data.bxp, value)));
    }

    public void updateTraitPoints(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, int value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withTraitPoints(SkillsDataUtils.getTraitPoints(type, data.traitPoints, value)));
    }

    public void updateTalentPoints(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, int value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withTalentPoints(SkillsDataUtils.getTalentPoints(type, data.talentPoints, value)));
    }

    public void updateLuck(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, int value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withLuck(SkillsDataUtils.getLuck(type, data.luck, value)));
    }

    public void updateWisdom(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, int value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withWisdom(SkillsDataUtils.getTraitLevel(type, data.wisdomTrait, value)));
    }

    public void updateKarma(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, int value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withKarma(SkillsDataUtils.getTraitLevel(type, data.karmaTrait, value)));
    }

    public void updateDexterity(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, int value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withDexterity(SkillsDataUtils.getTraitLevel(type, data.dexterityTrait, value)));
    }

    public void updateCharisma(@NotNull UUID uuid, @NotNull EnumsLib.UpdateType type, int value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null) return;
        cache.put(uuid, data.withCharisma(SkillsDataUtils.getTraitLevel(type, data.charismaTrait, value)));
    }

    public void addAllTraits(@NotNull UUID uuid, int[] value) {
        PlayerData data = ensureLoaded(uuid);
        if (data == null || value.length < 4) return;

        int w = Math.min(data.wisdomTrait + value[0], traits.get("wisdom_trait").maxLevel());
        int c = Math.min(data.charismaTrait + value[1], traits.get("charisma_trait").maxLevel());
        int k = Math.min(data.karmaTrait + value[2], traits.get("karma_trait").maxLevel());
        int d = Math.min(data.dexterityTrait + value[3], traits.get("dexterity_trait").maxLevel());

        cache.put(uuid, new PlayerData(data.name, data.exp, data.bxp, data.xpm, data.level, data.luck, data.traitPoints, data.talentPoints, w, c, k, d));
    }

    // ---------- Getters ----------

    /**
     * Returns a live snapshot from cache; auto-loads on first access.
     */
    public SkillsDatabase.PlayerDataDB getPlayerData(@NotNull UUID uuid) {
        PlayerData data = ensureLoaded(uuid);
        return data == null ? null : toRepo(data);
    }

    public String getPlayerName(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        return data != null ? data.name : null;
    }

    public double getEXP(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        return data != null ? data.exp : 0.0;
    }

    public int getLevel(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        return data != null ? data.level : 1;
    }

    public double getBXP(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        return data != null ? data.bxp : 0.0;
    }

    public int getLuck(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        return data != null ? data.luck : 0;
    }

    public double getXPM(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        return data != null ? data.xpm : 1.0;
    }

    public int getTraitPoints(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        return data != null ? data.traitPoints : 1;
    }

    public int getTalentPoints(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        return data != null ? data.talentPoints : 0;
    }

    public int[] getAllTraits(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data == null) return new int[] {0,0,0,0};
        return new int[] { data.wisdomTrait, data.charismaTrait, data.karmaTrait, data.dexterityTrait };
    }

    public int getWisdom(@NotNull UUID uuid)     { PlayerData data = cache.get(uuid); return data != null ? data.wisdomTrait : 0; }
    public int getKarma(@NotNull UUID uuid)      { PlayerData data = cache.get(uuid); return data != null ? data.karmaTrait : 0; }
    public int getCharisma(@NotNull UUID uuid)   { PlayerData data = cache.get(uuid); return data != null ? data.charismaTrait : 0; }
    public int getDexterity(@NotNull UUID uuid)  { PlayerData data = cache.get(uuid); return data != null ? data.dexterityTrait : 0; }

    // ---------- Misc (EXP & Traits) ----------

    /**
     * Returns the required EXP for the next level or a very large number at cap.
     *
     * @param level current level
     * @return exp required to reach next level
     */
    public double getNextEXP(int level) {
        double exp = expTable.getOrDefault(level + 1, 0.0);
        if (exp > 0) {
            return exp;
        }

        return 9999999999.00;
    }

    public Map<String, InnateTraitConfig.InnateTrait> getTraits() {
        return traits;
    }

    // ---------- Internals ----------

    private PlayerData ensureLoaded(UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data != null) return data;
        load(uuid);
        return cache.get(uuid);
    }

    private double consumeBonusXp(UUID uuid, double value) {
        PlayerData data = cache.get(uuid);
        if (data == null || data.bxp <= 0.0) return 0.0;

        double bonus = Math.min(data.bxp, value);
        double newBxp = (bonus >= value) ? (data.bxp - value) : 0.0;
        cache.put(uuid, data.withBxp(newBxp));
        return bonus;
    }

    private void checkLevelUp(@NotNull UUID uuid) {
        PlayerData data = cache.get(uuid);
        if (data == null) return;

        boolean levelUp = false;
        int prev = data.level;

        int finalLevel = data.level;
        int finalTraitPoints = data.traitPoints;

        while (data.exp >= getNextEXP(data.level)) {
            int nextLevel = data.level + 1;

            // only give trait points if below 120
            int nextTrait = data.traitPoints;
            if (nextLevel <= MAX_LEVEL) {
                nextTrait += traitPoints.getOrDefault(nextLevel, 0);
            }

            data = data.withLevel(nextLevel).withTraitPoints(nextTrait);

            finalLevel = nextLevel;
            finalTraitPoints = nextTrait;
            levelUp = true;
        }

        cache.put(uuid, data);
        if (levelUp) {
            int newLevel = finalLevel;
            int newTraitPoints = finalTraitPoints;

            plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> {
                Bukkit.getPluginManager().callEvent(
                        new SkillLevelUpEvent(uuid, skill, prev, newLevel, newTraitPoints)
                );
            });
        }
    }

    private SkillsDatabase.PlayerDataDB toRepo(PlayerData data) {
        return new SkillsDatabase.PlayerDataDB(
                data.name, data.exp, data.bxp, data.xpm, data.level, data.luck,
                data.traitPoints, data.talentPoints, data.wisdomTrait, data.charismaTrait, data.karmaTrait, data.dexterityTrait
        );
    }

    private PlayerData toMemoryData(SkillsDatabase.PlayerDataDB data) {
        return new PlayerData(
                data.name(), data.exp(), data.bxp(), data.xpm(), data.level(), data.luck(),
                data.traitPoints(), data.talentPoints(), data.wisdomTrait(), data.charismaTrait(),
                data.karmaTrait(), data.dexterityTrait()
        );
    }

    private String prefix() {
        return "[" + plugin.getName() + "]";
    }
}

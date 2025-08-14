package asia.virtualmc.vLib.core.skills.data.player_data;

import asia.virtualmc.vLib.core.player_data.InnateTraitUtils;
import asia.virtualmc.vLib.core.skills.utilities.SkillsDataUtils;
import asia.virtualmc.vLib.core.skills.utilities.SkillsUtils;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.text.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SkillsData implements SkillsWriter, SkillsReader {

    private static final int MIN_LEVEL = 1;
    private static final int MAX_LEVEL = 120;
    private static final int MAX_TRAIT_LEVEL = 50;

    private final Plugin plugin;
    private final SkillsDatabase database;
    private final String skillDisplayName;

    private final Map<Integer, Integer> expTable;
    private final Map<String, InnateTraitUtils.InnateTrait> traitInfo;

    // Runtime cache
    private final ConcurrentHashMap<UUID, Snapshot> cache = new ConcurrentHashMap<>();
    private record Snapshot(
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
        Snapshot withExp(double v) { return new Snapshot(name, v, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        Snapshot withBxp(double v) { return new Snapshot(name, exp, v, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        Snapshot withXpm(double v) { return new Snapshot(name, exp, bxp, v, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        Snapshot withLevel(int v) { return new Snapshot(name, exp, bxp, xpm, v, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        Snapshot withLuck(int v) { return new Snapshot(name, exp, bxp, xpm, level, v, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        Snapshot withTraitPoints(int v) { return new Snapshot(name, exp, bxp, xpm, level, luck, v, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        Snapshot withTalentPoints(int v) { return new Snapshot(name, exp, bxp, xpm, level, luck, traitPoints, v, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
        Snapshot withWisdom(int v) { return new Snapshot(name, exp, bxp, xpm, level, luck, traitPoints, talentPoints, v, charismaTrait, karmaTrait, dexterityTrait); }
        Snapshot withCharisma(int v) { return new Snapshot(name, exp, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, v, karmaTrait, dexterityTrait); }
        Snapshot withKarma(int v) { return new Snapshot(name, exp, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, v, dexterityTrait); }
        Snapshot withDexterity(int v) { return new Snapshot(name, exp, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, v); }
        Snapshot withName(String v) { return new Snapshot(v, exp, bxp, xpm, level, luck, traitPoints, talentPoints, wisdomTrait, charismaTrait, karmaTrait, dexterityTrait); }
    }

    /**
     * Construct a reusable PlayerData service for one "skill".
     *
     * @param plugin          owning plugin
     * @param database      persistence adapter
     * @param expTable        level -> next exp requirement map
     * @param traitInfo       traitName -> trait definition map
     */
    public SkillsData(
            @NotNull Plugin plugin,
            @NotNull SkillsDatabase database,
            @NotNull Map<Integer, Integer> expTable,
            @NotNull Map<String, InnateTraitUtils.InnateTrait> traitInfo
    ) {
        this.plugin = plugin;
        this.database = database;
        this.expTable = expTable;
        this.traitInfo = traitInfo;
        this.database.createTable(plugin, plugin.getName().toLowerCase() + "_playerData");
        this.skillDisplayName = StringUtils.deleteCharAt(plugin.getName(), 0);
    }

    // ---------- Lifecycle ----------

    /**
     * Loads a player's data from storage into memory cache.
     *
     * @param uuid player UUID
     */
    public void load(@NotNull UUID uuid) {
        try {
            SkillsDatabase.SkillsSnapshot d = database.load(plugin, plugin.getName().toLowerCase() + "_playerData", uuid);
            cache.put(uuid, toSnap(d));
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
        Snapshot s = cache.get(uuid);
        if (s == null) return;
        try {
            database.save(plugin, plugin.getName().toLowerCase() + "_playerData", uuid, toRepo(s));
        } catch (Exception e) {
            ConsoleUtils.severe(prefix(), "Failed to store player data for " + s.name + ": " + e.getMessage());
        }
    }

    /**
     * Saves all cached players to storage (best-effort).
     */
    public void saveAll() {
        try {
            Map<UUID, SkillsDatabase.SkillsSnapshot> out = new java.util.HashMap<>();
            for (Map.Entry<UUID, Snapshot> e : cache.entrySet()) {
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

    /**
     * Updates a player's EXP and handles level-up + action bar UX.
     *
     * @param player player
     * @param type   operation type (ADD, SUBTRACT, SET)
     * @param value  amount
     */
    public void updateEXP(@NotNull Player player, @NotNull EnumsLib.UpdateType type, double value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;

        if (type == EnumsLib.UpdateType.ADD) {
            double bonus = consumeBonusXp(id, value);
            double newExp = SkillsDataUtils.getEXP(type, s.exp, value + bonus);
            cache.put(id, s.withExp(newExp));
            SkillsUtils.buildEXPActionBar(player, skillDisplayName, value, bonus);
            checkAndApplyLevelUp(player);
        } else {
            cache.put(id, s.withExp(SkillsDataUtils.getEXP(type, s.exp, value)));
        }
    }

    /**
     * Adjusts level directly (no level-up loop or rewards).
     */
    public void updateLevel(@NotNull Player player, @NotNull EnumsLib.UpdateType type, int value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withLevel(SkillsDataUtils.getLevel(type, s.level, value)));
    }

    /**
     * Updates stored player name (used for DB & logs).
     */
    public void updateName(@NotNull Player player, String name) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withName(name));
    }

    /**
     * Updates XPM (exp multiplier).
     */
    public void updateXPM(@NotNull Player player, @NotNull EnumsLib.UpdateType type, double value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withXpm(SkillsDataUtils.getXPM(type, s.xpm, value)));
    }

    /**
     * Updates bonus exp (BXP).
     */
    public void updateBXP(@NotNull Player player, @NotNull EnumsLib.UpdateType type, double value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withBxp(SkillsDataUtils.getBXP(type, s.bxp, value)));
    }

    public void updateTraitPoints(@NotNull Player player, @NotNull EnumsLib.UpdateType type, int value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withTraitPoints(SkillsDataUtils.getTraitPoints(type, s.traitPoints, value)));
    }

    public void updateTalentPoints(@NotNull Player player, @NotNull EnumsLib.UpdateType type, int value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withTalentPoints(SkillsDataUtils.getTalentPoints(type, s.talentPoints, value)));
    }

    public void updateLuck(@NotNull Player player, @NotNull EnumsLib.UpdateType type, int value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withLuck(SkillsDataUtils.getLuck(type, s.luck, value)));
    }

    public void updateWisdom(@NotNull Player player, @NotNull EnumsLib.UpdateType type, int value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withWisdom(SkillsDataUtils.getTraitLevel(type, s.wisdomTrait, value)));
    }

    public void updateKarma(@NotNull Player player, @NotNull EnumsLib.UpdateType type, int value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withKarma(SkillsDataUtils.getTraitLevel(type, s.karmaTrait, value)));
    }

    public void updateDexterity(@NotNull Player player, @NotNull EnumsLib.UpdateType type, int value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withDexterity(SkillsDataUtils.getTraitLevel(type, s.dexterityTrait, value)));
    }

    public void updateCharisma(@NotNull Player player, @NotNull EnumsLib.UpdateType type, int value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null) return;
        cache.put(id, s.withCharisma(SkillsDataUtils.getTraitLevel(type, s.charismaTrait, value)));
    }

    /**
     * Adds to all trait levels with clamping.
     *
     * @param player player
     * @param value  int[4] -> {wisdom, charisma, karma, dexterity}
     */
    public void addAllTraits(@NotNull Player player, int[] value) {
        UUID id = player.getUniqueId();
        Snapshot s = ensureLoaded(id);
        if (s == null || value.length < 4) return;

        int w = Math.min(s.wisdomTrait + value[0], MAX_TRAIT_LEVEL);
        int c = Math.min(s.charismaTrait + value[1], MAX_TRAIT_LEVEL);
        int k = Math.min(s.karmaTrait + value[2], MAX_TRAIT_LEVEL);
        int d = Math.min(s.dexterityTrait + value[3], MAX_TRAIT_LEVEL);

        cache.put(id, new Snapshot(s.name, s.exp, s.bxp, s.xpm, s.level, s.luck, s.traitPoints, s.talentPoints, w, c, k, d));
    }

    // ---------- Getters ----------

    /**
     * Returns a live snapshot from cache; auto-loads on first access.
     */
    public SkillsDatabase.SkillsSnapshot getPlayerData(@NotNull UUID uuid) {
        Snapshot s = ensureLoaded(uuid);
        return s == null ? null : toRepo(s);
    }

    public String getPlayerName(@NotNull UUID uuid) {
        Snapshot s = cache.get(uuid);
        return s != null ? s.name : null;
    }

    public double getEXP(@NotNull UUID uuid) {
        Snapshot s = cache.get(uuid);
        return s != null ? s.exp : 0.0;
    }

    public int getLevel(@NotNull UUID uuid) {
        Snapshot s = cache.get(uuid);
        return s != null ? s.level : MIN_LEVEL;
    }

    public double getBXP(@NotNull UUID uuid) {
        Snapshot s = cache.get(uuid);
        return s != null ? s.bxp : 0.0;
    }

    public int getLuck(@NotNull UUID uuid) {
        Snapshot s = cache.get(uuid);
        return s != null ? s.luck : 0;
    }

    public double getXPM(@NotNull UUID uuid) {
        Snapshot s = cache.get(uuid);
        return s != null ? s.xpm : 1.0;
    }

    public int getTraitPoints(@NotNull UUID uuid) {
        Snapshot s = cache.get(uuid);
        return s != null ? s.traitPoints : 1;
    }

    public int getTalentPoints(@NotNull UUID uuid) {
        Snapshot s = cache.get(uuid);
        return s != null ? s.talentPoints : 0;
    }

    public int[] getAllTraits(@NotNull UUID uuid) {
        Snapshot s = cache.get(uuid);
        if (s == null) return new int[] {0,0,0,0};
        return new int[] { s.wisdomTrait, s.charismaTrait, s.karmaTrait, s.dexterityTrait };
    }

    public int getWisdom(@NotNull UUID uuid)     { Snapshot s = cache.get(uuid); return s != null ? s.wisdomTrait : 0; }
    public int getKarma(@NotNull UUID uuid)      { Snapshot s = cache.get(uuid); return s != null ? s.karmaTrait : 0; }
    public int getCharisma(@NotNull UUID uuid)   { Snapshot s = cache.get(uuid); return s != null ? s.charismaTrait : 0; }
    public int getDexterity(@NotNull UUID uuid)  { Snapshot s = cache.get(uuid); return s != null ? s.dexterityTrait : 0; }

    // ---------- Misc (EXP & Traits) ----------

    /**
     * Returns the required EXP for the next level or a very large number at cap.
     *
     * @param level current level
     * @return exp required to reach next level
     */
    public int getNextEXP(int level) {
        if (level < MAX_LEVEL) {
            Integer n = expTable.get(level);
            return n != null ? n : Integer.MAX_VALUE / 4;
        }
        return 1_000_000_000;
    }

    public InnateTraitUtils.InnateTrait getTrait(String traitName) {
        return traitInfo.get(traitName);
    }

    public double getTraitEffect(String traitName, String effectName) {
        InnateTraitUtils.InnateTrait t = traitInfo.get(traitName);
        if (t == null) {
            ConsoleUtils.severe(prefix(), "Trait not found: " + traitName);
            return 0.0;
        }
        Double v = t.effects.get(effectName);
        if (v == null) {
            ConsoleUtils.severe(prefix(), "Effect not found: " + effectName);
            return 0.0;
        }
        return v;
    }

    // ---------- Internals ----------

    private Snapshot ensureLoaded(UUID uuid) {
        Snapshot s = cache.get(uuid);
        if (s != null) return s;
        load(uuid);
        return cache.get(uuid);
    }

    private double consumeBonusXp(UUID uuid, double value) {
        Snapshot s = cache.get(uuid);
        if (s == null || s.bxp <= 0.0) return 0.0;

        double bonus = Math.min(s.bxp, value);
        double newBxp = (bonus >= value) ? (s.bxp - value) : 0.0;
        cache.put(uuid, s.withBxp(newBxp));
        return bonus;
    }

    private void checkAndApplyLevelUp(@NotNull Player player) {
        UUID id = player.getUniqueId();
        Snapshot s = cache.get(id);
        if (s == null || s.level >= MAX_LEVEL) return;

        boolean levelUp = false;
        int prev = s.level;

        while (s.exp >= getNextEXP(s.level) && s.level < MAX_LEVEL) {
            int nextLevel = s.level + 1;
            int nextTrait = s.traitPoints + 1 + (nextLevel > 100 ? 2 : 0);
            s = s.withLevel(nextLevel).withTraitPoints(nextTrait);
            levelUp = true;
        }
        cache.put(id, s);

        if (levelUp) {
            // Still reuse your UX helper, just pass the injected display name
            SkillsUtils.levelup(player, skillDisplayName, prev, s.level, s.traitPoints);
        }
    }

    private SkillsDatabase.SkillsSnapshot toRepo(Snapshot s) {
        return new SkillsDatabase.SkillsSnapshot(
                s.name, s.exp, s.bxp, s.xpm, s.level, s.luck,
                s.traitPoints, s.talentPoints, s.wisdomTrait, s.charismaTrait, s.karmaTrait, s.dexterityTrait
        );
    }

    private Snapshot toSnap(SkillsDatabase.SkillsSnapshot s) {
        return new Snapshot(
                s.name(), s.exp(), s.bxp(), s.xpm(), s.level(), s.luck(),
                s.traitPoints(), s.talentPoints(), s.wisdomTrait(), s.charismaTrait(), s.karmaTrait(), s.dexterityTrait()
        );
    }

    private String prefix() {
        return "[" + plugin.getName() + "]";
    }
}

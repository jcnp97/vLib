package asia.virtualmc.vLib.storage.mysql.skills;

import asia.virtualmc.vLib.storage.mysql.misc.PlayerIDUtils;
import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

public class PlayerDataUtils {

    public static class PlayerStats {
        public String name;
        public double exp;
        public double bxp;
        public double xpm;
        public int level;
        public int luck;
        public int traitPoints;
        public int talentPoints;
        public int wisdomTrait;
        public int charismaTrait;
        public int karmaTrait;
        public int dexterityTrait;

        public PlayerStats(String name, double exp, double bxp, double xpm, int level, int luck,
                           int traitPoints, int talentPoints, int wisdomTrait, int charismaTrait,
                           int karmaTrait, int dexterityTrait) {
            this.name = name;
            this.exp = exp;
            this.bxp = bxp;
            this.xpm = xpm;
            this.level = level;
            this.luck = luck;
            this.traitPoints = traitPoints;
            this.talentPoints = talentPoints;
            this.wisdomTrait = wisdomTrait;
            this.charismaTrait = charismaTrait;
            this.karmaTrait = karmaTrait;
            this.dexterityTrait = dexterityTrait;
        }
    }

    /**
     * Creates the player data table for the given plugin if it does not already exist.
     * <p>
     * The table name is dynamically determined using the plugin name (e.g., `pluginName_playerData`)
     * and includes columns for experience, levels, traits, and timestamps.
     * </p>
     *
     * @param pluginName the name of the plugin creating the table
     */
    public static void createTable(@NotNull String pluginName) {
        String sql = "CREATE TABLE IF NOT EXISTS " + pluginName + "_playerData (" +
                "playerID INT NOT NULL PRIMARY KEY, " +
                "playerName VARCHAR(16) NOT NULL, " +
                "playerEXP DECIMAL(13,2) DEFAULT 0.00, " +
                "playerBXP DECIMAL(13,2) DEFAULT 0.00, " +
                "playerXPM DECIMAL(4,2) DEFAULT 1.00, " +
                "playerLevel TINYINT DEFAULT 1, " +
                "playerLuck TINYINT DEFAULT 0, " +
                "traitPoints INT DEFAULT 1, " +
                "talentPoints INT DEFAULT 0, " +
                "wisdomTrait INT DEFAULT 0, " +
                "charismaTrait INT DEFAULT 0, " +
                "karmaTrait INT DEFAULT 0, " +
                "dexterityTrait INT DEFAULT 0, " +
                "lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.execute();
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to create player data table: " + e.getMessage());
        }
    }

    /**
     * Saves the current player data to the database using the provided stats.
     * <p>
     * If no row exists for the playerID, it will invoke {@link #createNewPlayerData(UUID, String, String)}.
     * </p>
     *
     * @param uuid           the UUID of the player
     * @param name           the player's username
     * @param exp            player experience
     * @param bxp            bonus experience
     * @param xpm            experience multiplier
     * @param level          player level
     * @param luck           luck value
     * @param traitPoints    unspent trait points
     * @param talentPoints   unspent talent points
     * @param wisdom         wisdom trait level
     * @param charisma       charisma trait level
     * @param karma          karma trait level
     * @param dexterity      dexterity trait level
     * @param pluginName     the plugin that owns the data
     */
    public static void savePlayerData(
            @NotNull UUID uuid,
            @NotNull String name,
            double exp,
            double bxp,
            double xpm,
            int level,
            int luck,
            int traitPoints,
            int talentPoints,
            int wisdom,
            int charisma,
            int karma,
            int dexterity,
            String pluginName
    ) {
        Integer playerID = PlayerIDUtils.get(uuid);
        String updateQuery = "UPDATE " + pluginName + "_playerData SET " +
                "playerName = ?, playerEXP = ?, playerBXP = ?, " +
                "playerXPM = ?, playerLevel = ?, playerLuck = ?, " +
                "traitPoints = ?, talentPoints = ?, wisdomTrait = ?, " +
                "charismaTrait = ?, karmaTrait = ?, dexterityTrait = ?, " +
                "lastUpdated = CURRENT_TIMESTAMP " +
                "WHERE playerID = ?";

        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {

            ps.setString(1, name);
            ps.setDouble(2, exp);
            ps.setDouble(3, bxp);
            ps.setDouble(4, xpm);
            ps.setInt(5, level);
            ps.setInt(6, luck);
            ps.setInt(7, traitPoints);
            ps.setInt(8, talentPoints);
            ps.setInt(9, wisdom);
            ps.setInt(10, charisma);
            ps.setInt(11, karma);
            ps.setInt(12, dexterity);
            ps.setInt(13, playerID);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                // If no rows were updated, the player's data row doesn't exist yet.
                createNewPlayerData(uuid, name, pluginName);
            }

        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to save " + name + " data on database: " + e.getMessage());
        }
    }

    /**
     * Saves all player stats from the given map to the database in batch.
     * <p>
     * Uses {@code PreparedStatement#addBatch()} and commits every 100 records
     * for performance. Fails silently for missing entries.
     * </p>
     *
     * @param playerDataMap map of player UUIDs to their associated stats
     * @param pluginName    the plugin that owns the data
     */
    public static void saveAllData(@NotNull Map<UUID, PlayerStats> playerDataMap,
                               String pluginName
    ) {
        if (playerDataMap.isEmpty()) {
            return;
        }

        String updateQuery = "UPDATE " + pluginName + "_playerData SET " +
                "playerName = ?, playerEXP = ?, playerBXP = ?, " +
                "playerXPM = ?, playerLevel = ?, playerLuck = ?, " +
                "traitPoints = ?, talentPoints = ?, wisdomTrait = ?, " +
                "charismaTrait = ?, karmaTrait = ?, dexterityTrait = ?, " +
                "lastUpdated = CURRENT_TIMESTAMP " +
                "WHERE playerID = ?";

        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {

            conn.setAutoCommit(false);
            int batchSize = 0;

            for (Map.Entry<UUID, PlayerStats> entry : playerDataMap.entrySet()) {
                UUID uuid = entry.getKey();
                PlayerStats stats = entry.getValue();
                Integer playerID = PlayerIDUtils.get(uuid);

                ps.setString(1, stats.name);
                ps.setDouble(2, stats.exp);
                ps.setDouble(3, stats.bxp);
                ps.setDouble(4, stats.xpm);
                ps.setInt(5, stats.level);
                ps.setInt(6, stats.luck);
                ps.setInt(7, stats.traitPoints);
                ps.setInt(8, stats.talentPoints);
                ps.setInt(9, stats.wisdomTrait);
                ps.setInt(10, stats.charismaTrait);
                ps.setInt(11, stats.karmaTrait);
                ps.setInt(12, stats.dexterityTrait);
                ps.setInt(13, playerID);

                ps.addBatch();
                batchSize++;

                if (batchSize % 100 == 0) {
                    ps.executeBatch();
                    conn.commit();
                }
            }

            if (batchSize % 100 != 0) {
                ps.executeBatch();
                conn.commit();
            }

        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to save all player data: " + e.getMessage());
        }
    }

    /**
     * Creates a new row in the database for the specified player with default stats.
     * <p>
     * Only used when the player is seen for the first time or no existing data is found.
     * </p>
     *
     * @param uuid        the UUID of the player
     * @param name        the player's username
     * @param pluginName  the plugin that owns the data
     */
    public static void createNewPlayerData(@NotNull UUID uuid, String name, String pluginName) {
        Integer playerID = PlayerIDUtils.get(uuid);

        String insertQuery =
                "INSERT INTO " + pluginName + "_playerData" +
                        " (playerID, playerName, playerEXP, playerBXP, playerXPM, " +
                        "playerLevel, playerLuck, traitPoints, talentPoints, wisdomTrait, " +
                        "charismaTrait, karmaTrait, dexterityTrait) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = MySQLConnection.get(pluginName)) {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setInt(1, playerID);
                ps.setString(2, name);
                ps.setDouble(3, 0.0);
                ps.setDouble(4, 0.0);
                ps.setDouble(5, 1.0);
                ps.setInt(6, 1);
                ps.setInt(7, 0);
                ps.setInt(8, 1);
                ps.setInt(9, 0);
                ps.setInt(10, 0);
                ps.setInt(11, 0);
                ps.setInt(12, 0);
                ps.setInt(13, 0);
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to create data for " + name + ": " + e.getMessage());
        }
    }

    /**
     * Loads a player's data from the database and returns it as a {@link PlayerStats} object.
     * <p>
     * If no data is found, it will create a new entry with default values and attempt to load again.
     * </p>
     *
     * @param uuid        the UUID of the player
     * @param pluginName  the plugin that owns the data
     * @return the player's stats from the database, or default values if not found
     */
    @NotNull
    public static PlayerStats loadPlayerData(@NotNull UUID uuid, String pluginName) {
        Integer playerID = PlayerIDUtils.get(uuid);

        String selectQuery = "SELECT * FROM " + pluginName + "_playerData WHERE playerID = ?";
        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            ps.setInt(1, playerID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PlayerStats(
                            rs.getString("playerName"),
                            rs.getDouble("playerEXP"),
                            rs.getDouble("playerBXP"),
                            rs.getDouble("playerXPM"),
                            rs.getInt("playerLevel"),
                            rs.getInt("playerLuck"),
                            rs.getInt("traitPoints"),
                            rs.getInt("talentPoints"),
                            rs.getInt("wisdomTrait"),
                            rs.getInt("charismaTrait"),
                            rs.getInt("karmaTrait"),
                            rs.getInt("dexterityTrait")
                    );
                }
            }

            // If no record is found, create one and try again.
            createNewPlayerData(uuid, "Unknown", pluginName);

            // Try loading again.
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new PlayerStats(
                            rs.getString("playerName"),
                            rs.getDouble("playerEXP"),
                            rs.getDouble("playerBXP"),
                            rs.getDouble("playerXPM"),
                            rs.getInt("playerLevel"),
                            rs.getInt("playerLuck"),
                            rs.getInt("traitPoints"),
                            rs.getInt("talentPoints"),
                            rs.getInt("wisdomTrait"),
                            rs.getInt("charismaTrait"),
                            rs.getInt("karmaTrait"),
                            rs.getInt("dexterityTrait")
                    );
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to load data for player " + uuid + ": " + e.getMessage());
        }

        return new PlayerStats("Unknown", 0.0, 0.0, 1.0, 1, 0, 1, 0, 0, 0, 0, 0);
    }
}
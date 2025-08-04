package asia.virtualmc.vLib.storage.mysql.misc;

import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IntegerKeyUtils {

    /**
     * Creates the necessary tables for storing integer-keyed data types and player data mappings.
     * <p>
     * Creates a definition table (e.g., {@code pluginName}) containing unique {@code data_name} entries,
     * and a composite player data table (e.g., {@code pluginName_data}) referencing both player IDs and data IDs.
     * </p>
     *
     * @param dataList    list of all possible data names to register in the definition table
     * @param pluginName  the plugin prefix used for naming the tables
     */
    public static void createTable(@NotNull List<String> dataList, @NotNull String pluginName) {
        try (Connection conn = MySQLConnection.get(pluginName)) {

            // Create data definition table
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + pluginName + " (" +
                            "data_id INT NOT NULL AUTO_INCREMENT," +
                            "data_name VARCHAR(255) NOT NULL," +
                            "PRIMARY KEY (data_id)," +
                            "UNIQUE KEY (data_name)" +
                            ")"
            );

            // Create player data table with composite foreign keys (now referencing vlib_players)
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + pluginName + "_data (" +
                            "player_id INT NOT NULL," +
                            "data_id INT NOT NULL," +
                            "amount INT DEFAULT 0," +
                            "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                            "PRIMARY KEY (player_id, data_id)," +
                            "FOREIGN KEY (player_id) REFERENCES vlib_players(playerID) ON DELETE CASCADE," +
                            "FOREIGN KEY (data_id) REFERENCES " + pluginName + "(data_id) ON DELETE CASCADE," +
                            "INDEX idx_player_id (player_id)" +
                            ")"
            );

            // Insert new stat types if they don't exist
            String insertQuery = "INSERT IGNORE INTO " + pluginName + " (data_name) VALUES (?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                for (String data : dataList) {
                    insertStmt.setString(1, data);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to create " + pluginName + " tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Saves the provided map of data values for a specific player.
     * <p>
     * Uses {@code INSERT ... ON DUPLICATE KEY UPDATE} to insert or update values per data ID.
     * </p>
     *
     * @param uuid         the UUID of the player whose data is being saved
     * @param pluginName   the plugin prefix used for table names
     * @param playerData   a map where the key is the data ID and the value is the amount
     */
    public static void savePlayerData(@NotNull UUID uuid, @NotNull String pluginName,
                                      @NotNull Map<Integer, Integer> playerData) {
        if (playerData.isEmpty()) {
            ConsoleUtils.warning("[" + pluginName + "]", "Attempted to update data for player " +
                    uuid + " but the provided data map is empty.");
            return;
        }

        String sql = "INSERT INTO " + pluginName + "_data (player_id, data_id, amount) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(pluginName)) {
            conn.setAutoCommit(false);
            int playerId = PlayerIDUtils.get(uuid);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<Integer, Integer> entry : playerData.entrySet()) {
                    ps.setInt(1, playerId);
                    ps.setInt(2, entry.getKey());
                    ps.setInt(3, entry.getValue());
                    ps.addBatch();
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to update data on " + pluginName + " for player " +
                    uuid + ": " + e.getMessage());
        }
    }

    /**
     * Saves all player data provided in a nested map structure.
     * <p>
     * Iterates through all players and their respective data entries, updating each row one-by-one.
     * Uses {@code INSERT ... ON DUPLICATE KEY UPDATE}.
     * </p>
     *
     * @param pluginName     the plugin prefix used for table names
     * @param allPlayerData  a map of player UUIDs to their data maps (dataID -> amount)
     */
    public static void saveAllData(@NotNull String pluginName,
                                   @NotNull Map<UUID, Map<Integer, Integer>> allPlayerData) {
        if (allPlayerData.isEmpty()) return;

        String sql = "INSERT INTO " + pluginName + "_data (player_id, data_id, amount) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map.Entry<UUID, Map<Integer, Integer>> playerEntry : allPlayerData.entrySet()) {
                int playerId = PlayerIDUtils.get(playerEntry.getKey());
                for (Map.Entry<Integer, Integer> dataEntry : playerEntry.getValue().entrySet()) {
                    ps.setInt(1, playerId);
                    ps.setInt(2, dataEntry.getKey());
                    ps.setInt(3, dataEntry.getValue());
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]",
                    "Failed to save all data for " + pluginName + ": " + e.getMessage());
        }
    }

    /**
     * Initializes default data entries for a new player.
     * <p>
     * Inserts zeroed values for every defined data ID from the data definition table,
     * skipping existing combinations via {@code WHERE NOT EXISTS}.
     * </p>
     *
     * @param uuid         the UUID of the player to initialize
     * @param pluginName   the plugin prefix used for table names
     */
    public static void createNewPlayerData(@NotNull UUID uuid, @NotNull String pluginName) {
        try (Connection conn = MySQLConnection.get(pluginName)) {
            conn.setAutoCommit(false);
            int playerId = PlayerIDUtils.get(uuid);

            String insertQuery =
                    "INSERT INTO " + pluginName + "_data (player_id, data_id, amount) " +
                            "SELECT ?, data_id, 0 FROM " + pluginName + " t " +
                            "WHERE NOT EXISTS (SELECT 1 FROM " + pluginName + "_data " +
                            "WHERE player_id = ? AND data_id = t.data_id)";

            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setInt(1, playerId);
                ps.setInt(2, playerId);
                ps.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to create new player data on " + pluginName +
                    " for " + uuid + ": " + e.getMessage());
        }
    }

    /**
     * Loads all integer-keyed data values for a specific player into memory.
     * <p>
     * If no data is found, {@link #createNewPlayerData(UUID, String)} is called to initialize the player.
     * Returns a thread-safe {@link ConcurrentHashMap} of dataID-to-amount.
     * </p>
     *
     * @param uuid         the UUID of the player
     * @param pluginName   the plugin prefix used for table names
     * @return a concurrent map of data ID to amount for the player
     */
    public static ConcurrentHashMap<Integer, Integer> loadPlayerData(@NotNull UUID uuid,
                                                                     @NotNull String pluginName) {

        ConcurrentHashMap<Integer, Integer> playerDataMap = new ConcurrentHashMap<>();

        try (Connection conn = MySQLConnection.get(pluginName)) {
            // Retrieve playerID using the external vlib_players table
            int playerId = PlayerIDUtils.get(uuid);

            // Check if player data exists
            String countQuery = "SELECT COUNT(*) FROM " + pluginName + "_data WHERE player_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(countQuery)) {
                ps.setInt(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) == 0) {
                        // Create new player data if it doesn't exist
                        createNewPlayerData(uuid, pluginName);
                    }
                }
            }

            // Load the data
            String loadQuery = "SELECT d.data_id, d.amount FROM " + pluginName + "_data d " +
                    "WHERE d.player_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(loadQuery)) {
                ps.setInt(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int dataId = rs.getInt("data_id");
                        int amount = rs.getInt("amount");
                        playerDataMap.put(dataId, amount);
                    }
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to load data from " + pluginName + " for player " +
                    uuid + ": " + e.getMessage());
        }

        return playerDataMap;
    }
}

package asia.virtualmc.vLib.storage.mysql.misc;

import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.storage.mysql.vlib_data.PlayerIDData;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class IntKeyDatabase {

    /**
     * Creates the required MySQL tables for storing plugin data definitions and player-specific values.
     * <p>
     * This includes:
     * - A main table to register stat types (`pluginName`)
     * - A data table mapping players to stats (`pluginName_data`)
     *
     * @param plugin   The plugin instance using this utility.
     * @param dataList A list of stat identifiers (data_name) to insert into the table.
     */
    public static void createTable(@NotNull Plugin plugin, @NotNull List<String> dataList) {
        String pluginName = plugin.getName();
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
     * Saves or updates the given stat data for a specific player in the MySQL database.
     * <p>
     * Will insert new rows or update existing ones using `ON DUPLICATE KEY UPDATE`.
     *
     * @param plugin     The plugin instance saving the data.
     * @param uuid       The UUID of the player.
     * @param playerData A map of data_id to amount values for the player.
     */
    public static void savePlayerData(@NotNull Plugin plugin, @NotNull UUID uuid,
                                      @NotNull Map<Integer, Integer> playerData) {
        String pluginName = plugin.getName();
        if (playerData.isEmpty()) {
            ConsoleUtils.warning("[" + pluginName + "]", "Attempted to update data for player " +
                    uuid + " but the provided data map is empty.");
            return;
        }

        String sql = "INSERT INTO " + pluginName + "_data (player_id, data_id, amount) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(pluginName)) {
            conn.setAutoCommit(false);
            int playerId = PlayerIDData.get(uuid);

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
     * Saves or updates stat data for all players in the provided dataset.
     * <p>
     * Uses a single prepared statement for performance, but executes each update individually.
     *
     * @param plugin        The plugin instance saving the data.
     * @param allPlayerData A map of UUIDs to their respective stat data maps.
     */
    public static void saveAllData(@NotNull Plugin plugin,
                                   @NotNull Map<UUID, Map<Integer, Integer>> allPlayerData) {
        if (allPlayerData.isEmpty()) return;

        String pluginName = plugin.getName();
        String sql = "INSERT INTO " + pluginName + "_data (player_id, data_id, amount) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map.Entry<UUID, Map<Integer, Integer>> playerEntry : allPlayerData.entrySet()) {
                int playerId = PlayerIDData.get(playerEntry.getKey());
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
     * Creates default stat rows (all zero) for a player if they don't already exist in the data table.
     * <p>
     * This ensures the player has entries for every existing data_id in the plugin’s definition table.
     *
     * @param uuid       The player's UUID.
     * @param pluginName The name of the plugin.
     * @apiNote Only for internal library use.
     */
    @Internal
    private static void createNewPlayerData(@NotNull UUID uuid, @NotNull String pluginName) {
        try (Connection conn = MySQLConnection.get(pluginName)) {
            conn.setAutoCommit(false);
            int playerId = PlayerIDData.get(uuid);

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
     * Loads all stat data for a player from the database.
     * <p>
     * If no data exists, a new row for each data_id will be initialized with 0.
     *
     * @param plugin The plugin requesting the data.
     * @param uuid   The UUID of the player.
     * @return A {@link ConcurrentHashMap} mapping data_id to amount for the player.
     */
    public static ConcurrentHashMap<Integer, Integer> loadPlayerData(@NotNull Plugin plugin,
                                                                     @NotNull UUID uuid) {
        ConcurrentHashMap<Integer, Integer> playerDataMap = new ConcurrentHashMap<>();
        String pluginName = plugin.getName();

        try (Connection conn = MySQLConnection.get(pluginName)) {
            // Retrieve playerID using the external vlib_players table
            int playerId = PlayerIDData.get(uuid);

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

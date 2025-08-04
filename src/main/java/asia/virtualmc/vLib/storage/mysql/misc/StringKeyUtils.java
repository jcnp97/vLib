package asia.virtualmc.vLib.storage.mysql.misc;

import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StringKeyUtils {

    /**
     * Creates the table used to store string-keyed player data for the specified plugin.
     * <p>
     * The table format includes a compound primary key of (player_id, data_name),
     * and supports automatic timestamp updates on modification.
     * </p>
     *
     * @param pluginName the name of the plugin used as a prefix for table naming
     */
    public static void createTable(@NotNull String pluginName) {
        try (Connection conn = MySQLConnection.get(pluginName)) {
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + pluginName + "_data (" +
                            "player_id INT NOT NULL," +
                            "data_name VARCHAR(255) NOT NULL," +
                            "amount INT DEFAULT 0," +
                            "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                            "PRIMARY KEY (player_id, data_name)," +
                            "FOREIGN KEY (player_id) REFERENCES vlib_players(playerID) ON DELETE CASCADE," +
                            "INDEX idx_player_id (player_id)" +
                            ")"
            );

        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to create " + pluginName + " tables: " + e.getMessage());
        }
    }

    /**
     * Saves or updates the string-keyed data for a specific player.
     * <p>
     * Uses {@code INSERT ... ON DUPLICATE KEY UPDATE} to write or update each key-value pair.
     * Will log a warning if the provided data map is empty.
     * </p>
     *
     * @param uuid        the UUID of the player
     * @param pluginName  the plugin prefix used for table names
     * @param playerData  a map of string keys to integer values representing the player's data
     */
    public static void savePlayerData(@NotNull UUID uuid, @NotNull String pluginName,
                                      @NotNull Map<String, Integer> playerData) {
        if (playerData.isEmpty()) {
            ConsoleUtils.warning("[" + pluginName + "]", "Attempted to update data for player " +
                    uuid + " but the provided data map is empty.");
            return;
        }

        String sql = "INSERT INTO " + pluginName + "_data (player_id, data_name, amount) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(pluginName)) {
            conn.setAutoCommit(false);
            int playerId = PlayerIDUtils.get(uuid);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<String, Integer> entry : playerData.entrySet()) {
                    ps.setInt(1, playerId);
                    ps.setString(2, entry.getKey());
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
     * Saves or updates all string-keyed data for all players.
     * <p>
     * Iterates through all entries and performs individual updates per player and key.
     * Uses {@code INSERT ... ON DUPLICATE KEY UPDATE}.
     * </p>
     *
     * @param pluginName      the plugin prefix used for table names
     * @param allPlayerData   a map of UUIDs to their respective data maps (key -> amount)
     */
    public static void saveAllData(@NotNull String pluginName,
                                   @NotNull Map<UUID, Map<String, Integer>> allPlayerData) {
        if (allPlayerData.isEmpty()) return;

        String sql = "INSERT INTO " + pluginName + "_data (player_id, data_name, amount) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map.Entry<UUID, Map<String, Integer>> playerEntry : allPlayerData.entrySet()) {
                int playerId = PlayerIDUtils.get(playerEntry.getKey());
                for (Map.Entry<String, Integer> dataEntry : playerEntry.getValue().entrySet()) {
                    ps.setInt(1, playerId);
                    ps.setString(2, dataEntry.getKey());
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
     * Loads all string-keyed data entries for a given player.
     * <p>
     * If no data is found in the database, the result is pre-filled with the given set of expected keys,
     * each initialized to 0. This ensures a complete and predictable return structure.
     * </p>
     *
     * @param uuid        the UUID of the player
     * @param pluginName  the plugin prefix used for table names
     * @param dataNames   the set of expected string keys (used to prefill default values if no data is found)
     * @return a concurrent map of string keys to integer values representing the player's data
     */
    public static ConcurrentHashMap<String, Integer> loadPlayerData(@NotNull UUID uuid,
                                                                    @NotNull String pluginName,
                                                                    @NotNull Set<String> dataNames) {

        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        String sql = "SELECT data_name, amount FROM " + pluginName + "_data WHERE player_id = ?";

        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int playerId = PlayerIDUtils.get(uuid);
            ps.setInt(1, playerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString("data_name"), rs.getInt("amount"));
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]",
                    "Failed to load data for player " + uuid + " from " + pluginName + ": " + e.getMessage());
        }

        // If no data was found, pre-fill with expected keys at 0.
        if (result.isEmpty()) {
            for (String key : dataNames) {
                result.put(key, 0);
            }
        }

        return result;
    }
}
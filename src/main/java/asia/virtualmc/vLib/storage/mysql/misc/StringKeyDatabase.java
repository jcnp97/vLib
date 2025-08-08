package asia.virtualmc.vLib.storage.mysql.misc;

import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.storage.mysql.vlib_data.PlayerIDData;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StringKeyDatabase {

    /**
     * Creates the MySQL table for storing string-keyed player data.
     * <p>
     * The table name follows the format: {@code pluginName_data} and stores:
     * player ID, data name (as string), amount, and timestamp.
     *
     * @param plugin The plugin instance requesting table creation.
     */
    public static void createTable(@NotNull Plugin plugin) {
        String pluginName = plugin.getName();
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
     * Saves or updates string-keyed stat data for a specific player.
     * <p>
     * Existing values are updated via {@code ON DUPLICATE KEY UPDATE}.
     *
     * @param plugin     The plugin instance saving the data.
     * @param uuid       UUID of the player.
     * @param playerData A map of data_name to amount values for the player.
     */
    public static void savePlayerData(@NotNull Plugin plugin,
                                      @NotNull UUID uuid,
                                      @NotNull Map<String, Integer> playerData) {

        String pluginName = plugin.getName();
        if (playerData.isEmpty()) {
            ConsoleUtils.warning("[" + pluginName + "]", "Attempted to update data for player " +
                    uuid + " but the provided data map is empty.");
            return;
        }

        String sql = "INSERT INTO " + pluginName + "_data (player_id, data_name, amount) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(pluginName)) {
            conn.setAutoCommit(false);
            int playerId = PlayerIDData.get(uuid);

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
     * Saves or updates string-keyed stat data for multiple players.
     * <p>
     * Uses a single prepared statement for batch-style inserts or updates.
     *
     * @param plugin        The plugin instance saving the data.
     * @param allPlayerData A map of UUIDs to their respective stat data maps.
     */
    public static void saveAllData(@NotNull Plugin plugin,
                                   @NotNull Map<UUID, Map<String, Integer>> allPlayerData) {
        if (allPlayerData.isEmpty()) return;

        String pluginName = plugin.getName();
        String sql = "INSERT INTO " + pluginName + "_data (player_id, data_name, amount) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Map.Entry<UUID, Map<String, Integer>> playerEntry : allPlayerData.entrySet()) {
                int playerId = PlayerIDData.get(playerEntry.getKey());
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
     * Loads all string-keyed data for a given player from the database.
     * <p>
     * If the player has no existing data, the result is initialized with the expected keys set to {@code 0}.
     *
     * @param plugin    The plugin requesting the data.
     * @param uuid      The UUID of the player.
     * @param dataNames The set of expected data keys to include with default 0 if none exist.
     * @return A {@link ConcurrentHashMap} mapping data_name to amount values.
     */
    public static ConcurrentHashMap<String, Integer> loadPlayerData(@NotNull Plugin plugin,
                                                                    @NotNull UUID uuid,
                                                                    @NotNull Set<String> dataNames) {
        String pluginName = plugin.getName();
        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        String sql = "SELECT data_name, amount FROM " + pluginName + "_data WHERE player_id = ?";

        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int playerId = PlayerIDData.get(uuid);
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
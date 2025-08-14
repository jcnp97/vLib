package asia.virtualmc.vLib.storage.mysql.misc;

import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.storage.mysql.utilities.MySQLUtils;
import asia.virtualmc.vLib.storage.mysql.vlib_data.PlayerIDData;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.text.StringSetUtils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StringKeyDatabase {

    /**
     * Creates the per-plugin table `<pluginName>_<tableName>` with (player_id, data_name) as a composite PK.
     *
     * @param plugin    the owning plugin (its name prefixes the table)
     * @param tableName the logical table suffix (without plugin prefix)
     */
    public static void createTable(@NotNull Plugin plugin, String tableName) {
        String pluginName = plugin.getName();
        String fullTableName = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName);

        try (Connection conn = MySQLConnection.get(plugin)) {
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS "  + fullTableName + " (" +
                            "player_id INT NOT NULL," +
                            "data_name VARCHAR(255) NOT NULL," +
                            "amount INT UNSIGNED DEFAULT 0," +
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
     * Update and inserts (INSERT ... ON DUPLICATE KEY UPDATE) a player's string-keyed integer values in a single transaction.
     * Rolls back if any batch entry fails.
     *
     * @param plugin     the owning plugin (used for connection and table prefix)
     * @param tableName  the logical table suffix (without plugin prefix)
     * @param uuid       the player's UUID resolved to an internal player_id
     * @param playerData map of data_name -> amount to persist (must not be empty)
     */
    public static void savePlayerData(@NotNull Plugin plugin,
                                      @NotNull String tableName, @NotNull UUID uuid,
                                      @NotNull Map<String, Integer> playerData) {

        String pluginName = plugin.getName();
        String fullTableName = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName);
        if (playerData.isEmpty()) {
            ConsoleUtils.warning("[" + pluginName + "]", "Attempted to update data for player " +
                    uuid + " but the provided data map is empty.");
            return;
        }

        String sql = "INSERT INTO " + fullTableName + " (player_id, data_name, amount) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";
        try (Connection conn = MySQLConnection.get(plugin)) {
            boolean old = conn.getAutoCommit();
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
            } finally {
                conn.setAutoCommit(old);
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Failed to update data on " + pluginName + " for player " +
                    uuid + ": " + e.getMessage());
        }
    }

    /**
     * Upserts string-keyed integer values for multiple players.
     * No-op if the input map is empty.
     *
     * @param plugin        the owning plugin (used for connection and table prefix)
     * @param tableName     the logical table suffix (without plugin prefix)
     * @param allPlayerData map of player UUID -> (data_name -> amount) to persist
     */
    public static void saveAllData(@NotNull Plugin plugin,
                                   @NotNull String tableName,
                                   @NotNull Map<UUID, Map<String, Integer>> allPlayerData) {
        if (allPlayerData.isEmpty()) return;

        String pluginName = plugin.getName();
        String fullTableName = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName);
        String sql = "INSERT INTO " + fullTableName + " (player_id, data_name, amount) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(plugin)) {
            boolean old = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (var playerEntry : allPlayerData.entrySet()) {
                    int playerId = PlayerIDData.get(playerEntry.getKey());
                    for (var dataEntry : playerEntry.getValue().entrySet()) {
                        ps.setInt(1, playerId);
                        ps.setString(2, dataEntry.getKey());
                        ps.setInt(3, dataEntry.getValue());
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(old);
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]",
                    "Failed to save all data for " + pluginName + ": " + e.getMessage());
        }
    }

    /**
     * Loads all stored (data_name -> amount) for a player.
     * If nothing is found, returns a map pre-filled with the provided dataNames at 0.
     *
     * @param plugin    the owning plugin (used for connection and table prefix)
     * @param tableName the logical table suffix (without plugin prefix)
     * @param uuid      the player's UUID resolved to an internal player_id
     * @param dataNames expected keys to pre-fill with 0 when no rows exist
     * @return a thread-safe map of data_name -> amount
     */
    public static ConcurrentHashMap<String, Integer> loadPlayerData(@NotNull Plugin plugin,
                                                                    @NotNull String tableName,
                                                                    @NotNull UUID uuid,
                                                                    @NotNull List<String> dataNames) {
        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();

        String pluginName = plugin.getName();
        String fullTableName = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName);
        Set<String> sortedData = StringSetUtils.sortedSet(dataNames);

        String sql = "SELECT data_name, amount FROM " + fullTableName + " WHERE player_id = ?";
        try (Connection conn = MySQLConnection.get(plugin);
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
            for (String key : sortedData) {
                result.putIfAbsent(key, 0);
            }
        }

        return result;
    }
}
package asia.virtualmc.vLib.storage.mysql.misc;

import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.storage.mysql.utilities.MySQLUtils;
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
     * Creates two tables for this plugin: a definition table (<plugin>_<tableName>) and a player data table (<plugin>_<tableName>_data).
     * Ensures all entries from dataList exist in the definition table.
     *
     * @param plugin    the owning plugin (its name prefixes the tables)
     * @param tableName logical table name (without plugin prefix)
     * @param dataList  list of data names to register in the definition table
     */
    public static void createTable(@NotNull Plugin plugin,
                                   @NotNull String tableName,
                                   @NotNull List<String> dataList) {
        String pluginName = plugin.getName();
        String defTable = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName);
        String dataTable = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName + "_data");

        try (Connection conn = MySQLConnection.get(pluginName)) {

            // Create data definition table
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + defTable + " (" +
                            "data_id INT NOT NULL AUTO_INCREMENT," +
                            "data_name VARCHAR(255) NOT NULL," +
                            "PRIMARY KEY (data_id)," +
                            "UNIQUE KEY (data_name)" +
                            ")"
            );

            // Create player data table with composite foreign keys (now referencing vlib_players)
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + dataTable + " (" +
                            "player_id INT NOT NULL," +
                            "data_id INT NOT NULL," +
                            "amount INT DEFAULT 0," +
                            "last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                            "PRIMARY KEY (player_id, data_id)," +
                            "FOREIGN KEY (player_id) REFERENCES vlib_players(playerID) ON DELETE CASCADE," +
                            "FOREIGN KEY (data_id) REFERENCES " + defTable + "(data_id) ON DELETE CASCADE," +
                            "INDEX idx_player_id (player_id)" +
                            ")"
            );

            // Insert new stat types if they don't exist
            String insertQuery = "INSERT IGNORE INTO " + defTable + " (data_name) VALUES (?)";

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
     * Upserts (INSERT ... ON DUPLICATE KEY UPDATE) a single player's values in a single transaction.
     *
     * @param plugin     the owning plugin (for connection/table prefix)
     * @param tableName  logical table name (without plugin prefix)
     * @param uuid       player UUID (resolved to internal player_id)
     * @param playerData map of data_id -> amount to persist (must not be empty)
     */
    public static void savePlayerData(@NotNull Plugin plugin, @NotNull String tableName,
                                      @NotNull UUID uuid, @NotNull Map<Integer, Integer> playerData) {
        String pluginName = plugin.getName();
        String dataTable = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName + "_data");

        if (playerData.isEmpty()) {
            ConsoleUtils.warning("[" + pluginName + "]", "Attempted to update data for player " +
                    uuid + " but the provided data map is empty.");
            return;
        }

        String sql = "INSERT INTO " + dataTable + " (player_id, data_id, amount) VALUES (?, ?, ?) " +
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
     * Batch upserts values for multiple players in one transaction.
     *
     * @param plugin        the owning plugin (for connection/table prefix)
     * @param tableName     logical table name (without plugin prefix)
     * @param allPlayerData map of player UUID -> (data_id -> amount) to persist
     */
    public static void saveAllData(@NotNull Plugin plugin, @NotNull String tableName,
                                   @NotNull Map<UUID, Map<Integer, Integer>> allPlayerData) {
        if (allPlayerData.isEmpty()) return;

        String pluginName = plugin.getName();
        String dataTable = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName + "_data");
        String sql = "INSERT INTO " + dataTable + " (player_id, data_id, amount) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount)";

        try (Connection conn = MySQLConnection.get(pluginName);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            boolean old = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try {
                for (Map.Entry<UUID, Map<Integer, Integer>> playerEntry : allPlayerData.entrySet()) {
                    int playerId = PlayerIDData.get(playerEntry.getKey());
                    for (Map.Entry<Integer, Integer> dataEntry : playerEntry.getValue().entrySet()) {
                        ps.setInt(1, playerId);
                        ps.setInt(2, dataEntry.getKey());
                        ps.setInt(3, dataEntry.getValue());
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(old);
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]",
                    "Failed to save all data for " + pluginName + ": " + e.getMessage());
        }
    }

    /**
     * Ensures missing rows for the player are created (amount=0), then loads all data_id -> amount for that player.
     *
     * @param plugin    the owning plugin (for connection/table prefix)
     * @param tableName logical table name (without plugin prefix)
     * @param uuid      player UUID (resolved to internal player_id)
     * @return a thread-safe map of data_id -> amount (0 for newly created rows)
     */
    public static ConcurrentHashMap<Integer, Integer> loadPlayerData(@NotNull Plugin plugin,
                                                                     @NotNull String tableName,
                                                                     @NotNull UUID uuid) {
        ConcurrentHashMap<Integer, Integer> playerDataMap = new ConcurrentHashMap<>();
        String pluginName = plugin.getName();
        String defTable = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName);
        String dataTable = MySQLUtils.toSafeIdentifer(pluginName + "_" + tableName + "_data");

        try (Connection conn = MySQLConnection.get(pluginName)) {
            int playerId = PlayerIDData.get(uuid);
            updateRows(conn, defTable, dataTable, playerId);

            // Load the data after sync
            String loadQuery = "SELECT d.data_id, dd.amount " +
                    "FROM " + defTable + " d " +
                    "LEFT JOIN " + dataTable + " dd ON dd.data_id = d.data_id AND dd.player_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(loadQuery)) {
                ps.setInt(1, playerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int dataId = rs.getInt("data_id");
                        int amount = rs.getInt("amount");
                        if (!rs.wasNull()) {
                            playerDataMap.put(dataId, amount);
                        } else {
                            // left join can return null amount on brand-new rows; treat as 0
                            playerDataMap.put(dataId, 0);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "]",
                    "Failed to load data from " + pluginName + " for player " + uuid + ": " + e.getMessage());
        }

        return playerDataMap;
    }

    /**
     * WARNING (@Internal): Library-use only; not for public API consumers.
     * Inserts missing (player_id, data_id) rows with amount=0 based on the definition table.
     *
     * @param conn            open SQL connection (transaction is handled inside)
     * @param definitionTable fully qualified definition table name
     * @param dataTable       fully qualified player data table name
     * @param playerId        internal numeric player id
     */
    @Internal
    private static void updateRows(@NotNull Connection conn,
                                   @NotNull String definitionTable,
                                   @NotNull String dataTable,
                                   int playerId) throws SQLException {

        String insertMissing = "INSERT INTO " + dataTable + " (player_id, data_id, amount) " +
                "SELECT ?, d.data_id, 0 " +
                "FROM " + definitionTable + " d " +
                "LEFT JOIN " + dataTable + " dd " +
                "  ON dd.player_id = ? AND dd.data_id = d.data_id " +
                "WHERE dd.player_id IS NULL";

        boolean old = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try (PreparedStatement ps = conn.prepareStatement(insertMissing)) {
            ps.setInt(1, playerId);
            ps.setInt(2, playerId);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(old);
        }
    }
}

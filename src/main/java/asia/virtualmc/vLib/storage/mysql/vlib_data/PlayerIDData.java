package asia.virtualmc.vLib.storage.mysql.vlib_data;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerIDData {
    private static final ConcurrentHashMap<UUID, Integer> playerIDMap = new ConcurrentHashMap<>();

    public PlayerIDData() {
        create();
    }

    /**
     * Creates the `vlib_players` table in the MySQL database if it does not already exist.
     * The table stores player IDs linked to UUIDs.
     */
    public void create() {
        String sql = "CREATE TABLE IF NOT EXISTS vlib_players (" +
                "playerID INT NOT NULL AUTO_INCREMENT, " +
                "uuid CHAR(36) NOT NULL, " +
                "PRIMARY KEY (playerID), " +
                "UNIQUE KEY (uuid)" +
                ")";
        try {
            Connection conn = MySQLConnection.get(Main.getInstance());
            if (conn != null) {
                PreparedStatement statement = conn.prepareStatement(sql);
                statement.executeUpdate();
                ConsoleUtils.info("Table 'vlib_players' checked/created successfully.");
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Error creating vlib_players table: " + e.getMessage());
        }
    }

    /**
     * Retrieves the internal player ID for the given UUID from memory or the database.
     * If the UUID is not yet in the database, it will be inserted automatically.
     *
     * @param uuid The UUID of the player.
     * @return The associated internal player ID.
     * @throws IllegalStateException if the player ID could not be retrieved or inserted.
     */
    @NotNull
    public static Integer get(UUID uuid) {
        Integer id = playerIDMap.get(uuid);
        if (id != null) {
            return id;
        }

        String insertQuery = "INSERT INTO vlib_players (uuid) VALUES (?) " +
                "ON DUPLICATE KEY UPDATE playerID = LAST_INSERT_ID(playerID)";
        String selectQuery = "SELECT playerID FROM vlib_players WHERE uuid = ?";

        try (Connection conn = MySQLConnection.get(Main.getInstance())) {
            PreparedStatement insertStmt = conn.prepareStatement(
                    insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            insertStmt.setString(1, uuid.toString());
            insertStmt.executeUpdate();

            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int playerID = rs.getInt(1);
                    playerIDMap.put(uuid, playerID);
                    return playerID;
                }
            }

            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, uuid.toString());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int playerID = rs.getInt("playerID");
                        playerIDMap.put(uuid, playerID);
                        return playerID;
                    }
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("Error fetching/inserting playerID: " + e.getMessage());
        }

        throw new IllegalStateException("Failed to retrieve or insert playerID for UUID: " + uuid);
    }

    /**
     * Replaces the UUID associated with the given internal player ID in the database.
     *
     * @param playerID The internal player ID.
     * @param newUUID  The new UUID to associate.
     * @return true if the update was successful, false otherwise.
     */
    public static boolean replace(int playerID, UUID newUUID) {
        String updateQuery = "UPDATE vlib_players SET uuid = ? WHERE playerID = ?";
        try (Connection conn = MySQLConnection.get(Main.getInstance())) {
            PreparedStatement statement = conn.prepareStatement(updateQuery);
            statement.setString(1, newUUID.toString());
            statement.setInt(2, playerID);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            ConsoleUtils.severe("Error updating uuid for playerID " + playerID + ": " + e.getMessage());
        }

        return false;
    }
}

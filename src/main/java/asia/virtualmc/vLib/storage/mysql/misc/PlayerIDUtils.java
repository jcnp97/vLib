package asia.virtualmc.vLib.storage.mysql.misc;

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

public class PlayerIDUtils {
    private static final ConcurrentHashMap<UUID, Integer> playerIDMap = new ConcurrentHashMap<>();

    /**
     * Creates the `vlib_players` table in the database if it does not already exist.
     * <p>
     * The table stores a unique integer `playerID` and the corresponding player's UUID.
     * Ensures `uuid` is unique and auto-generates the `playerID`.
     * </p>
     */
    public static void create() {
        String sql = "CREATE TABLE IF NOT EXISTS vlib_players (" +
                "playerID INT NOT NULL AUTO_INCREMENT, " +
                "uuid CHAR(36) NOT NULL, " +
                "PRIMARY KEY (playerID), " +
                "UNIQUE KEY (uuid)" +
                ")";

        try (Connection connection = MySQLConnection.get(Main.getPluginName());
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            ConsoleUtils.info("Table 'vlib_players' checked/created successfully.");
        } catch (SQLException e) {
            ConsoleUtils.severe("Error creating table: " + e.getMessage());
        }
    }

    /**
     * Retrieves the internal player ID for the given UUID.
     * <p>
     * If the UUID is already cached, it is returned directly. Otherwise,
     * inserts the UUID into the database if not present, retrieves the associated playerID,
     * caches it, and returns it.
     * </p>
     *
     * @param uuid the UUID of the player
     * @return the internal player ID associated with the UUID
     * @throws IllegalStateException if the playerID could not be retrieved or inserted
     */
    @NotNull
    public static Integer get(UUID uuid) {
        if (playerIDMap.containsKey(uuid)) {
            return playerIDMap.get(uuid);
        }

        String insertQuery = "INSERT INTO vlib_players (uuid) VALUES (?) " +
                "ON DUPLICATE KEY UPDATE playerID = LAST_INSERT_ID(playerID)";
        String selectQuery = "SELECT playerID FROM vlib_players WHERE uuid = ?";

        try (Connection connection = MySQLConnection.get(Main.getPluginName());
             PreparedStatement insertStmt = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

            insertStmt.setString(1, uuid.toString());
            insertStmt.executeUpdate();

            try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int playerID = rs.getInt(1);
                    playerIDMap.put(uuid, playerID);
                    return playerID;
                }
            }

            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
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
     * Updates the UUID associated with the given internal player ID.
     * <p>
     * This can be used to change the UUID stored for a player in case of migration or correction.
     * </p>
     *
     * @param playerID the internal player ID to update
     * @param newUUID the new UUID to associate with the playerID
     * @return true if the update was successful, false otherwise
     */
    public static boolean replace(int playerID, UUID newUUID) {
        String updateQuery = "UPDATE vlib_players SET uuid = ? WHERE playerID = ?";
        try (Connection connection = MySQLConnection.get(Main.getPluginName());
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {
            statement.setString(1, newUUID.toString());
            statement.setInt(2, playerID);
            int affectedRows = statement.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            ConsoleUtils.severe("Error updating uuid for playerID " + playerID + ": " + e.getMessage());
            return false;
        }
    }
}

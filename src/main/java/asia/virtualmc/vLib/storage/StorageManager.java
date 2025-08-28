package asia.virtualmc.vLib.storage;

import asia.virtualmc.vLib.Registry;
import asia.virtualmc.vLib.storage.mysql.vlib_data.PlayerIDData;
import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.storage.sqlite.SQLiteConnection;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;

import java.util.Map;

public class StorageManager {
    private final MySQLConnection mySQLConnection;
    private final SQLiteConnection sqLiteConnection;
    private final PlayerIDData playerIDData;

    public StorageManager() {
        this.mySQLConnection = new MySQLConnection();
        this.sqLiteConnection = new SQLiteConnection();
        this.playerIDData = new PlayerIDData();
        enable();
    }

    public void enable() {
        Map<String, Boolean> modules = Registry.getModules();
        if (Boolean.TRUE.equals(modules.get("sqlite"))) {
            if (sqLiteConnection.load()) {
                ConsoleUtils.info("Successfully loaded SQLite module!");
            }
        }

        if (Boolean.TRUE.equals(modules.get("mysql"))) {
            if (mySQLConnection.load()) {
                ConsoleUtils.info("Successfully loaded MySQL module!");
                playerIDData.create();
            }
        }
    }

    public void task() {
        sqLiteConnection.checkpointAll();
    }

    public void disable() {
        sqLiteConnection.closeAll();
        mySQLConnection.closeAll();
    }
}

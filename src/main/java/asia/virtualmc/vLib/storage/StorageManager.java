package asia.virtualmc.vLib.storage;

import asia.virtualmc.vLib.Registry;
import asia.virtualmc.vLib.storage.mysql.vlib_data.PlayerIDData;
import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.storage.sqlite.SQLiteConnection;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;

import java.util.Map;

public class StorageManager {

    public StorageManager() {
        enable();
    }

    public void enable() {
        Map<String, Boolean> modules = Registry.getModules();
        if (Boolean.TRUE.equals(modules.get("sqlite"))) {
            if (SQLiteConnection.load()) {
                ConsoleUtils.info("Successfully loaded SQLite module!");
            }
        }

        if (Boolean.TRUE.equals(modules.get("mysql"))) {
            if (MySQLConnection.load()) {
                ConsoleUtils.info("Successfully loaded MySQL module!");
                PlayerIDData.create();
            }
        }
    }

    public void disable() {
        SQLiteConnection.checkpointAll();
        SQLiteConnection.closeAll();
        MySQLConnection.closeAll();
    }
}

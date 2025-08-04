package asia.virtualmc.vLib.storage;

import asia.virtualmc.vLib.Registry;
import asia.virtualmc.vLib.storage.mysql.misc.PlayerIDUtils;
import asia.virtualmc.vLib.storage.mysql.utilities.MySQLConnection;
import asia.virtualmc.vLib.storage.sqlite.SQLiteUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;

import java.util.Map;

public class StorageManager {

    public StorageManager() {
        enable();
    }

    public void enable() {
        Map<String, Boolean> modules = Registry.getModules();
        if (Boolean.TRUE.equals(modules.get("sqlite"))) {
            if (SQLiteUtils.load()) {
                ConsoleUtils.info("Successfully loaded SQLite!");
            }
        }

        if (Boolean.TRUE.equals(modules.get("mysql"))) {
            if (MySQLConnection.load()) {
                ConsoleUtils.info("Successfully loaded MySQL Database!");
                PlayerIDUtils.create();
            }
        }
    }

    public void disable() {
        SQLiteUtils.closeAll();
        MySQLConnection.closeAll();
    }
}

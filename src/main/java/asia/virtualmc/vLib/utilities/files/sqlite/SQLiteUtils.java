package asia.virtualmc.vLib.utilities.files.sqlite;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteUtils {
    private static final String SQLITE_VERSION = "3.49.1.0";
    private static final String SQLITE_URL = "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/" +
            SQLITE_VERSION + "/sqlite-jdbc-" + SQLITE_VERSION + ".jar";

    // Pool map: fileName -> HikariDataSource
    private static final Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();

    /**
     * Initialize the SQLite driver by downloading the dependency at runtime
     * @param plugin the plugin instance
     * @return true if initialization was successful
     */
    public static boolean load(Plugin plugin) {
        try {
            File libFolder = new File(plugin.getDataFolder(), "libs");
            if (!libFolder.exists() && !libFolder.mkdirs()) {
                ConsoleUtils.severe("Failed to create lib directory for SQLite JDBC driver");
                return false;
            }

            File sqliteJar = new File(libFolder, "sqlite-jdbc-" + SQLITE_VERSION + ".jar");

            // Download SQLite JDBC if it doesn't exist
            if (!sqliteJar.exists()) {
                ConsoleUtils.info("Downloading SQLite JDBC driver...");
                try {
                    URL url = new URL(SQLITE_URL);
                    Files.copy(url.openStream(), sqliteJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    ConsoleUtils.info("SQLite JDBC driver downloaded successfully.");
                } catch (IOException e) {
                    ConsoleUtils.severe("Failed to download SQLite JDBC driver: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }

            // Load the driver into DriverManager
            URLClassLoader sqliteClassLoader = new URLClassLoader(
                    new URL[]{sqliteJar.toURI().toURL()},
                    SQLiteUtils.class.getClassLoader()
            );
            Class<?> driverClass = Class.forName("org.sqlite.JDBC", true, sqliteClassLoader);
            Driver driverInstance = (Driver) driverClass.getDeclaredConstructor().newInstance();
            DriverManager.registerDriver(new DriverShimUtils(driverInstance));
            return true;
        } catch (Exception e) {
            ConsoleUtils.severe("Failed to initialize SQLite: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Opens (and pools) a SQLite database file under the plugin's data folder.
     * Uses WAL journal mode and sets a 5s busy timeout.
     *
     * @param plugin       the plugin instance
     * @param relativePath path relative to plugin's data folder (e.g. "data/db")
     * @param fileName     database file name (e.g. "players.db")
     * @return a pooled Connection, or null if creation/connection failed
     */
    public static Connection connect(Plugin plugin, String relativePath, String fileName) {
        File dataDir = plugin.getDataFolder();
        File dir = new File(dataDir, relativePath);
        if (!dir.exists() && !dir.mkdirs()) {
            ConsoleUtils.severe("Failed to create directories for SQLite at " + dir.getAbsolutePath());
            return null;
        }

        File dbFile = new File(dir, fileName);
        try {
            if (!dbFile.exists() && !dbFile.createNewFile()) {
                ConsoleUtils.severe("Failed to create SQLite file at " + dbFile.getAbsolutePath());
                return null;
            }

            String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath() + "?journal_mode=WAL&busy_timeout=5000";
            HikariDataSource ds = dataSources.computeIfAbsent(fileName, key -> {
                HikariConfig config = new HikariConfig();
                config.setDriverClassName("org.sqlite.JDBC");
                config.setJdbcUrl(jdbcUrl);
                config.setMaximumPoolSize(10);
                config.setPoolName("SQLitePool-" + fileName);
                // SQLite PRAGMA via data source properties
                config.addDataSourceProperty("journal_mode", "WAL");
                config.addDataSourceProperty("busy_timeout", "5000");
                config.setAutoCommit(false);
                return new HikariDataSource(config);
            });
            return ds.getConnection();
        } catch (IOException e) {
            ConsoleUtils.severe("Could not connect to SQLite (" + fileName + "): " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Force a WAL checkpoint on a specific database pool.
     */
    public static void checkpoint(String fileName) {
        HikariDataSource ds = dataSources.get(fileName);
        if (ds == null) return;
        try (Connection connection = ds.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA wal_checkpoint(FULL);");
        } catch (SQLException e) {
            ConsoleUtils.severe("Failed to checkpoint SQLite WAL for " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Force WAL checkpoint on all active database pools.
     */
    public static void checkpointAll() {
        for (Map.Entry<String, HikariDataSource> entry : dataSources.entrySet()) {
            try (Connection conn = entry.getValue().getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA wal_checkpoint(FULL);");
            } catch (SQLException e) {
                ConsoleUtils.severe("Failed to checkpoint SQLite WAL for " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Close all HikariCP pools and clear references.
     */
    public static void closeAll() {
        for (HikariDataSource ds : dataSources.values()) {
            try {
                ds.close();
            } catch (Exception e) {
                ConsoleUtils.severe("Failed to close HikariCP pool: " + e.getMessage());
            }
        }
        dataSources.clear();
    }
}

package asia.virtualmc.vLib.storage.sqlite;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.storage.sqlite.utilities.DriverShimUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLiteConnection {
    private static final String SQLITE_VERSION = "3.49.1.0";
    private static final String SQLITE_URL = "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/" +
            SQLITE_VERSION + "/sqlite-jdbc-" + SQLITE_VERSION + ".jar";
    private static final Map<String, HikariDataSource> dataSourceMap = new ConcurrentHashMap<>();

    public SQLiteConnection() {
        load();
    }

    /**
     * Downloads and loads the SQLite JDBC driver if not already present.
     * Registers the driver using a shim to work with DriverManager.
     *
     * @return true if the driver was loaded successfully, false otherwise.
     */
    public boolean load() {
        try {
            File libFolder = new File(Main.getInstance().getDataFolder(), "libs");
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
                    try (InputStream in = url.openStream()) {
                        Files.copy(in, sqliteJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    ConsoleUtils.info("SQLite JDBC driver downloaded successfully.");
                } catch (IOException e) {
                    ConsoleUtils.severe("Failed to download SQLite JDBC driver: " + e.getMessage());
                    e.printStackTrace();
                    return false;
                }
            }

            // Load the driver into DriverManager
            try {
                URLClassLoader sqliteClassLoader = new URLClassLoader(
                        new URL[]{sqliteJar.toURI().toURL()},
                        SQLiteConnection.class.getClassLoader()
                );
                Class<?> driverClass = Class.forName("org.sqlite.JDBC", true, sqliteClassLoader);
                Driver driverInstance = (Driver) driverClass.getDeclaredConstructor().newInstance();
                DriverManager.registerDriver(new DriverShimUtils(driverInstance));
                return true;
            } catch (Exception e) {
                ConsoleUtils.severe("Unable to load sqlite class loader: " + e.getMessage());
                return false;
            }

        } catch (Exception e) {
            ConsoleUtils.severe("Failed to initialize SQLite: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a new HikariDataSource for the specified SQLite database file.
     * Adds the data source to an internal pool map using the given key.
     *
     * @param key     Unique identifier for the connection pool.
     * @param dbFile  SQLite database file to connect to.
     * @return A configured HikariDataSource or null if creation failed.
     */
    private static HikariDataSource create(@NotNull String key, @NotNull File dbFile) {
        try {
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }

            String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath() + "?journal_mode=WAL&busy_timeout=5000";
            HikariConfig config = new HikariConfig();
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl(jdbcUrl);
            config.setMaximumPoolSize(10);
            config.setPoolName("SQLitePool-" + key);
            config.setAutoCommit(false);

            try {
                HikariDataSource source = new HikariDataSource(config);
                dataSourceMap.put(key, source);
                return source;

            } catch (Exception e) {
                ConsoleUtils.severe("Failed to create a HikariDataSource for " + key + ": " + e.getMessage());
                return null;
            }

        } catch (IOException e) {
            ConsoleUtils.severe("Could not connect to SQLite for " + key + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a SQLite {@link Connection} from the connection pool associated with the given plugin and database file.
     * <p>
     * If no pool exists or the existing pool is closed, a new one will be created automatically.
     * This method throws an {@link IllegalStateException} if the connection pool cannot be initialized or accessed.
     *
     * @param plugin  The plugin requesting the connection.
     * @param dbFile  The SQLite database file.
     * @return An active {@link Connection} to the SQLite database.
     * @throws IllegalStateException if the connection pool cannot be created or a connection cannot be retrieved.
     */
    public static Connection get(@NotNull Plugin plugin, @NotNull File dbFile) {
        String key = plugin.getName() + ":" + dbFile.getAbsolutePath();
        HikariDataSource source = dataSourceMap.get(key);

        if (source == null || source.isClosed()) {
            source = create(key, dbFile);
            if (source == null || source.isClosed()) {
                throw new IllegalStateException("Failed to create a SQLite connection pool for: " + key);
            }
        }

        try {
            return source.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to obtain SQLite connection for: " + key, e);
        }
    }

    /**
     * Executes a WAL (Write-Ahead Logging) checkpoint on all managed SQLite connections.
     * This helps reduce WAL file size and commits changes to the main database file.
     */
    public void checkpointAll() {
        for (Map.Entry<String, HikariDataSource> entry : dataSourceMap.entrySet()) {
            try (Connection conn = entry.getValue().getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA wal_checkpoint(FULL);");
            } catch (SQLException e) {
                ConsoleUtils.severe("Failed to checkpoint SQLite WAL for " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Closes all active SQLite HikariCP connection pools and clears the internal map.
     */
    public void closeAll() {
        checkpointAll();

        for (Map.Entry<String, HikariDataSource> entry : dataSourceMap.entrySet()) {
            try {
                entry.getValue().close();
            } catch (Exception e) {
                ConsoleUtils.severe("Failed to close HikariCP pool for " + entry.getKey() + ": " + e.getMessage());
            }
        }
        dataSourceMap.clear();
    }
}
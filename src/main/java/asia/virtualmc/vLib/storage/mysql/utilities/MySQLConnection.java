package asia.virtualmc.vLib.storage.mysql.utilities;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MySQLConnection {
    private static final Map<String, HikariDataSource> dataSourceMap = new ConcurrentHashMap<>();

    private static DatabaseConfig databaseConfig;
    private record DatabaseConfig(String host, int port, String dbName, String user, String password) {}

    /**
     * Loads MySQL configuration from the plugin's {@code config.yml}.
     * <p>
     * Parses the {@code mysql} section and stores connection parameters in memory.
     * </p>
     *
     * @return true if configuration was successfully loaded; false otherwise
     */
    public static boolean load() {
        YamlDocument yaml = YAMLUtils.getYaml(Main.getInstance(), "config.yml");
        if (yaml == null) {
            ConsoleUtils.severe("Unable to find or read config.yml, it might be missing!");
            return false;
        }

        Section section = yaml.getSection("mysql");
        if (section == null) {
            ConsoleUtils.severe("Couldn't find mysql section from config.yml! Failed to connect to MySQL.");
            return false;
        }

        try {
            String host = section.getString("host", "localhost");
            int port = section.getInt("port", 3306);
            String dbName = section.getString("database", "minecraft");
            String user = section.getString("username", "root");
            String pass = section.getString("password", "");
            databaseConfig = new DatabaseConfig(host, port, dbName, user, pass);

        } catch (Exception e) {
            ConsoleUtils.severe("Error during database config read: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Retrieves a {@link Connection} for the specified plugin.
     * <p>
     * If the connection pool does not exist yet for the plugin, a new {@link HikariDataSource}
     * will be created and cached. Subsequent calls reuse the connection pool.
     * </p>
     *
     * @param pluginName the plugin requesting the MySQL connection
     * @return a valid SQL connection, or null if connection fails
     * @throws SQLException if the connection cannot be established
     */
    public static Connection get(@NotNull String pluginName) throws SQLException {
        if (dataSourceMap.containsKey(pluginName)) {
            return dataSourceMap.get(pluginName).getConnection();
        }

        try {
            HikariDataSource source = create(pluginName);
            if (source == null) {
                throw new SQLException("Failed to initialize HikariDataSource for plugin: " + pluginName);
            }

            Connection conn = source.getConnection();
            if (conn != null && !conn.isClosed()) {
                ConsoleUtils.info("[" + pluginName + "]", "Successfully connected to the MySQL database.");
            }
            return conn;

        } catch (SQLException e) {
            ConsoleUtils.severe("Failed to retrieve connection: HikariDataSource is null or closed for " + pluginName);
        }

        return null;
    }

    /**
     * Creates and caches a new {@link HikariDataSource} for the given plugin.
     * <p>
     * Reads from the previously loaded database configuration and applies
     * standard HikariCP settings.
     * </p>
     *
     * @param pluginName the plugin initializing the MySQL pool
     * @return the created data source, or null if configuration is invalid
     */
    private static HikariDataSource create(@NotNull String pluginName) {
        if (databaseConfig == null) {
            ConsoleUtils.severe("MySQL config not loaded. Aborting MySQL connection setup for " + pluginName);
            return null;
        }

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:mysql://" + databaseConfig.host + ":" + databaseConfig.port +
                    "/" + databaseConfig.dbName);
            config.setUsername(databaseConfig.user);
            config.setPassword(databaseConfig.password);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(10000);
            config.setMaxLifetime(1800000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            HikariDataSource dataSource = new HikariDataSource(config);
            dataSourceMap.put(pluginName, dataSource);
            return dataSource;

        } catch (Exception e) {
            ConsoleUtils.severe("[" + pluginName + "]", "Error during database setup: " + e.getMessage());
            ConsoleUtils.severe("[" + pluginName + "]", "Make sure you have configured the MySQL section on config.yml correctly!");
        }

        return null;
    }

    /**
     * Closes and removes the connection pool associated with a specific plugin.
     * <p>
     * Useful when a plugin is disabled or unloaded dynamically.
     * </p>
     *
     * @param pluginName the name of the plugin to disconnect
     */
    public static void close(@NotNull String pluginName) {
        HikariDataSource source = dataSourceMap.remove(pluginName);
        if (source != null && !source.isClosed()) {
            source.close();
        }
    }

    /**
     * Closes all existing plugin-specific MySQL connection pools.
     * <p>
     * Should only be used internally by the core library during shutdown.
     * </p>
     */
    @Internal
    public static void closeAll() {
        for (Map.Entry<String, HikariDataSource> entry : dataSourceMap.entrySet()) {
            HikariDataSource source = entry.getValue();
            if (source != null && !source.isClosed()) {
                source.close();
            }
        }
        dataSourceMap.clear();
    }
}
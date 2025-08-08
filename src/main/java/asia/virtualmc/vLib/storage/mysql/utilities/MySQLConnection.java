package asia.virtualmc.vLib.storage.mysql.utilities;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.files.YAMLUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
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
     * Loads the MySQL configuration from config.yml.
     * Populates the internal {@code databaseConfig} record with parsed values.
     *
     * @return true if the configuration was successfully loaded; false otherwise.
     * @apiNote This method is intended for internal library use only.
     */
    @Internal
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
     * Creates a new HikariCP connection pool for the specified plugin.
     * The connection uses the settings defined in the loaded MySQL configuration.
     *
     * @param pluginName The plugin's name used as a key for the connection pool.
     * @return A new {@link HikariDataSource} instance, or null if creation fails.
     * @apiNote This method is intended for internal library use only.
     */
    @Internal
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
     * Retrieves a MySQL connection for the specified plugin name.
     * If the pool is missing or closed, it attempts to recreate it.
     *
     * @param pluginName The name of the plugin requesting the connection.
     * @return A valid {@link Connection}, or null if connection fails.
     * @apiNote This method is intended for internal library use only.
     */
    @Internal
    public static Connection get(String pluginName) {
        HikariDataSource source = dataSourceMap.get(pluginName);

        if (source == null || source.isClosed()) {
            source = create(pluginName);
            if (source == null || source.isClosed()) {
                throw new IllegalStateException("Failed to create a MySQL connection pool for plugin: " + pluginName);
            }
        }

        try {
            return source.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException("Unable to obtain MySQL connection for plugin: " + pluginName, e);
        }
    }

    /**
     * Retrieves a MySQL connection for the given plugin.
     * Delegates to {@link #get(String)} using the plugin's name.
     *
     * @param plugin The plugin requesting the connection.
     * @return A valid {@link Connection}, or null if connection fails.
     */
    public static Connection get(@NotNull Plugin plugin) {
        return get(plugin.getName());
    }

    /**
     * Closes and removes the HikariCP connection pool associated with the given plugin name.
     *
     * @param pluginName The name of the plugin whose connection should be closed.
     * @apiNote This method is intended for internal library use only.
     */
    @Internal
    public static void close(String pluginName) {
        HikariDataSource source = dataSourceMap.remove(pluginName);
        if (source != null && !source.isClosed()) {
            source.close();
        }
    }

    /**
     * Closes and removes the HikariCP connection pool associated with the given plugin.
     * Delegates to {@link #close(String)}.
     *
     * @param plugin The plugin whose connection should be closed.
     */
    public static void close(@NotNull Plugin plugin) {
        close(plugin.getName());
    }

    /**
     * Closes all active MySQL connection pools and clears the internal connection map.
     *
     * @apiNote This method is intended for internal library use only.
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
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

    public static Connection get(@NotNull String pluginName) throws SQLException {
        HikariDataSource source = dataSourceMap.computeIfAbsent(pluginName, name -> {
            create(pluginName);
            return dataSourceMap.get(name);
        });

        if (source == null || source.isClosed()) {
            ConsoleUtils.severe("Failed to retrieve connection: HikariDataSource is null or closed for " + pluginName);
            throw new SQLException("HikariDataSource not initialized for " + pluginName);
        }

        return source.getConnection();
    }

    private static void create(@NotNull String pluginName) {
        if (databaseConfig == null) {
            ConsoleUtils.severe("MySQL config not loaded. Aborting MySQL connection setup for " + pluginName);
            return;
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

            try (Connection connection = dataSource.getConnection()) {
                if (connection != null && !connection.isClosed()) {
                    ConsoleUtils.info("[" + pluginName + "] Successfully connected to the MySQL database.");
                }
            }
        } catch (SQLException e) {
            ConsoleUtils.severe("[" + pluginName + "] Failed to connect to database: " + e.getMessage());
        } catch (Exception e) {
            ConsoleUtils.severe("[" + pluginName + "] Error during database setup: " + e.getMessage());
        }
    }

    public static void close(@NotNull String pluginName) {
        HikariDataSource source = dataSourceMap.remove(pluginName);
        if (source != null && !source.isClosed()) {
            source.close();
        }
    }

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
package asia.virtualmc.vLib.utilities.files.json;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JSONFileUtils {

    /**
     * Retrieves a JSON file from the plugin’s data folder at the given relative path.
     *
     * @param plugin   the plugin instance whose data folder is used as the root
     * @param path     the relative path to the JSON file (e.g. "logs/storage.json")
     * @param generate if true, attempt to copy from resources or create a new file when absent
     * @return the existing, copied, or newly created {@link File}, or {@code null} if absent and
     *         {@code generate} is false or an I/O error occurs
     */
    public static File get(Plugin plugin, String path, boolean generate) {
        Path filePath = plugin.getDataFolder().toPath().resolve(path);
        File file = filePath.toFile();

        if (file.exists()) {
            return file;
        }

        if (!generate) {
            return null;
        }

        try {
            if (plugin.getResource(path) != null) {
                plugin.saveResource(path, false);
                return file;
            }

            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
            return file;
        } catch (IOException e) {
            ConsoleUtils.severe("Could not retrieve or create JSON file '" + path + "': " + e.getMessage());
            return null;
        }
    }
}

package asia.virtualmc.vLib.utilities.files;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileUtils {

    /**
     * Clones a given file into a specified output directory and returns the cloned file.
     * <p>
     * This method works with any file type (e.g. .json, .png), except directories.
     * If the {@code fileToClone} is a directory, it will log an error using
     * {@code ConsoleUtils.severe(String)} and return {@code null}.
     *
     * @param fileToClone the file to be cloned
     * @param outputDir   the directory where the cloned file will be placed
     * @return the cloned {@link File}, or {@code null} if the operation failed
     */
    public static File clone(File fileToClone, File outputDir) {
        if (fileToClone.isDirectory()) {
            ConsoleUtils.severe("Cannot clone directory: " + fileToClone.getName());
            return null;
        }

        if (!fileToClone.exists()) {
            ConsoleUtils.severe("Source file does not exist: " + fileToClone.getAbsolutePath());
            return null;
        }

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File destination = new File(outputDir, fileToClone.getName());
        try {
            Files.copy(fileToClone.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException e) {
            ConsoleUtils.severe("Failed to clone file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Renames the given file while preserving its original file extension.
     * <p>
     * For example, renaming {@code test.json} with {@code newfile} results in {@code newfile.json}.
     * If the operation fails, an error will be logged via {@code ConsoleUtils.severe(String)}.
     *
     * @param file    the file to be renamed
     * @param newName the new base name (without extension)
     * @return the renamed {@link File}, or {@code null} if the operation failed
     */
    public static File rename(File file, String newName) {
        if (file == null || !file.exists() || file.isDirectory()) {
            ConsoleUtils.severe("Cannot rename: File is null, does not exist, or is a directory.");
            return null;
        }

        String fileName = file.getName();
        int lastDot = fileName.lastIndexOf(".");
        String extension = lastDot >= 0 ? fileName.substring(lastDot) : "";
        File renamedFile = new File(file.getParent(), newName + extension);

        if (file.renameTo(renamedFile)) {
            return renamedFile;
        } else {
            ConsoleUtils.severe("Failed to rename file: " + file.getAbsolutePath());
            return null;
        }
    }

    /**
     * Copies all resource files (excluding class files and META-INF entries) from the plugin jar
     * into the plugin's data folder. This is typically used to extract default YAML configs.
     * <p>
     * Skips extraction if the plugin folder already exists.
     * </p>
     *
     * @param plugin   the plugin instance
     * @param jarFile  the plugin's jar file
     */
    public static void copyAllResource(JavaPlugin plugin, File jarFile) {
        File pluginFolder = plugin.getDataFolder();
        if (pluginFolder.exists()) return;
        pluginFolder.mkdirs();

        try (JarFile jar = new JarFile(jarFile)) {
            for (JarEntry entry : Collections.list(jar.entries())) {
                String name = entry.getName();

                if (name.startsWith("META-INF") || name.equals("plugin.yml")) continue;
                if (entry.isDirectory()) continue;
                if (name.endsWith(".class")) continue;
                if (!name.endsWith(".yml") && !name.contains("/")) continue;

                File outFile = new File(pluginFolder, name);
                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdirs();
                }

                try (InputStream in = plugin.getResource(name);
                     OutputStream out = new FileOutputStream(outFile)) {
                    if (in == null) continue;
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                }
            }
        } catch (IOException e) {
            ConsoleUtils.severe(plugin.getName(), "Failed to extract default resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package asia.virtualmc.vLib.utilities.files;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

public class YAMLUtils {

    /**
     * Loads a YAML file from the plugin's data folder.
     * <p>
     * If a default resource is available in the plugin jar, it will be used to create the configuration.
     * Otherwise, it will attempt to load an existing file without defaults.
     * </p>
     *
     * @param plugin   the plugin instance
     * @param fileName the name of the YAML file to load
     * @return the loaded {@link YamlDocument}, or {@code null} if an error occurs
     */
    @Deprecated
    public static YamlDocument getYaml(@NotNull Plugin plugin, @NotNull String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        try {
            InputStream defaultFile = plugin.getResource(fileName);
            YamlDocument config;

            if (defaultFile != null) {
                config = YamlDocument.create(file, defaultFile);
            } else {
                config = YamlDocument.create(file);
            }

            return config;
        } catch (IOException e) {
            ConsoleUtils.severe("An error occurred when trying to read " + fileName);
            e.getCause();
        }

        return null;
    }

    /**
     * Retrieves a section from the specified YAML document at the given route.
     * Logs an error if the section is not found.
     *
     * @param yaml  the {@link YamlDocument} to retrieve the section from
     * @param route the path to the section in the YAML file
     * @return the {@link Section} if found, otherwise {@code null}
     */
    @Deprecated
    public static Section getSection(@NotNull YamlDocument yaml, @NotNull String route) {
        Section section = yaml.getSection(route);
        if (section == null) {
            ConsoleUtils.severe(route + " section not found from Yaml File " + yaml.getNameAsString());
            return null;
        }

        return section;
    }

    /**
     * Retrieves a {@code Map<String, String>} from a YAML section.
     * <p>
     * Keys and their string values are read from the given route.
     * If {@code debug} is true, the resulting map is logged using {@code ConsoleUtils.debugMap()}.
     * </p>
     *
     * @param yaml  the YAML document to read from
     * @param route the path to the target section
     * @param debug whether to print the resulting map for debugging
     * @return a map of key-value string pairs from the specified section, or an empty map if the section is missing
     */
    @Deprecated
    public static Map<String, String> getMap(@NotNull YamlDocument yaml, String route, boolean debug) {
        Map<String, String> map = new HashMap<>();

        Section section = yaml.getSection(route);
        if (section == null) {
            ConsoleUtils.severe("Unable to find route " + route + " from " + yaml.getNameAsString() + ". Returning an empty map..");
            return map;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        if (!keys.isEmpty()) {
            for (String key : keys) {
                map.put(key, section.getString(key));
            }
        }

        if (debug) {
            ConsoleUtils.debugMap(map);
        }

        return map;
    }

    /**
     * Retrieves a {@code Map<String, Integer>} from a YAML section.
     * <p>
     * Keys and their integer values are read from the given route.
     * If {@code debug} is true, the resulting map is logged using {@code ConsoleUtils.debugMap()}.
     * </p>
     *
     * @param yaml  the YAML document to read from
     * @param route the path to the target section
     * @param debug whether to print the resulting map for debugging
     * @return a map of key-integer pairs from the specified section, or an empty map if the section is missing
     */
    @Deprecated
    public static Map<String, Integer> getIntMap(@NotNull YamlDocument yaml, String route, boolean debug) {
        Map<String, Integer> map = new HashMap<>();

        Section section = yaml.getSection(route);
        if (section == null) {
            ConsoleUtils.severe("Unable to find route " + route + " from " + yaml.getNameAsString() + ". Returning an empty map..");
            return map;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        if (!keys.isEmpty()) {
            for (String key : keys) {
                map.put(key, section.getInt(key));
            }
        }

        if (debug) {
            ConsoleUtils.debugMap(map);
        }

        return map;
    }

    /**
     * Retrieves a {@code Map<String, Double>} from a YAML section.
     * <p>
     * Keys and their double values are read from the given route.
     * If {@code debug} is true, the resulting map is logged using {@code ConsoleUtils.debugMap()}.
     * </p>
     *
     * @param yaml  the YAML document to read from
     * @param route the path to the target section
     * @param debug whether to print the resulting map for debugging
     * @return a map of key-double pairs from the specified section, or an empty map if the section is missing
     */
    @Deprecated
    public static Map<String, Double> getDoubleMap(@NotNull YamlDocument yaml, String route, boolean debug) {
        Map<String, Double> map = new HashMap<>();

        Section section = yaml.getSection(route);
        if (section == null) {
            ConsoleUtils.severe("Unable to find route " + route + " from " + yaml.getNameAsString() + ". Returning an empty map..");
            return map;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        if (!keys.isEmpty()) {
            for (String key : keys) {
                map.put(key, section.getDouble(key));
            }
        }

        if (debug) {
            ConsoleUtils.debugMap(map);
        }

        return map;
    }

    /**
     * Retrieves a map of boolean values from a specified section in a YAML document.
     * <p>
     * Each key in the section will be mapped to its corresponding boolean value.
     * If the section does not exist, an empty map is returned and an error is logged.
     * Optionally prints the resulting map for debugging.
     *
     * @param yaml  the {@link YamlDocument} to read from
     * @param route the YAML path to the section containing boolean values
     * @param debug if true, prints the resulting map using {@link ConsoleUtils#debugMap(Map)}
     * @return a map of keys to boolean values from the specified YAML section, or an empty map if not found
     */
    @Deprecated
    public static Map<String, Boolean> getBooleanMap(@NotNull YamlDocument yaml, String route, boolean debug) {
        Map<String, Boolean> map = new HashMap<>();

        Section section = yaml.getSection(route);
        if (section == null) {
            ConsoleUtils.severe("Unable to find route " + route + " from " + yaml.getNameAsString() + ". Returning an empty map..");
            return map;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        if (!keys.isEmpty()) {
            for (String key : keys) {
                map.put(key, section.getBoolean(key));
            }
        }

        if (debug) {
            ConsoleUtils.debugMap(map);
        }

        return map;
    }

    /**
     * Retrieves a {@code Map<Integer, String>} from a YAML section.
     * <p>
     * Keys are expected to be parseable as integers; non-integer keys will be skipped with a warning.
     * If {@code debug} is true, the resulting map is logged using {@code ConsoleUtils.debugMap()}.
     * </p>
     *
     * @param yaml  the YAML document to read from
     * @param route the path to the target section
     * @param debug whether to print the resulting map for debugging
     * @return a map of integer-string pairs from the specified section, or an empty map if the section is missing
     */
    @Deprecated
    public static Map<Integer, String> getStringMap(@NotNull YamlDocument yaml, String route, boolean debug) {
        Map<Integer, String> map = new HashMap<>();

        Section section = yaml.getSection(route);
        if (section == null) {
            ConsoleUtils.severe("Unable to find route " + route + " from " + yaml.getNameAsString() + ". Returning an empty map..");
            return map;
        }

        Set<String> keys = section.getRoutesAsStrings(false);
        if (!keys.isEmpty()) {
            for (String key : keys) {
                try {
                    int intKey = Integer.parseInt(key);
                    map.put(intKey, section.getString(key));
                } catch (NumberFormatException ex) {
                    ConsoleUtils.severe("Skipping non-integer key " + key + " in section " + route);
                }
            }
        }

        if (debug) {
            ConsoleUtils.debugMap(map);
        }

        return map;
    }

    /**
     * Loads all {@code .yml} files from a given subdirectory of the plugin's data folder.
     * <p>
     * Each file is parsed into a {@link YamlDocument} and stored in a map keyed by filename.
     * </p>
     *
     * @param plugin  the plugin instance
     * @param dirPath the relative subdirectory to search within the plugin's data folder
     * @return a map of filename to loaded {@link YamlDocument}, or an empty map if no valid files are found
     */
    public static Map<String, YamlDocument> getFiles(@NotNull Plugin plugin,
                                                     @NotNull String dirPath,
                                                     Set<String> filesToIgnore,
                                                     boolean includeFileFormat) {
        Map<String, YamlDocument> documents = new HashMap<>();

        File directory = new File(plugin.getDataFolder(), dirPath);
        if (!directory.exists() || !directory.isDirectory()) {
            ConsoleUtils.severe("Directory not found: " + directory.getAbsolutePath());
            return documents;
        }

        File[] ymlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (ymlFiles == null || ymlFiles.length == 0) {
            ConsoleUtils.warning("No .yml files found in: " + directory.getAbsolutePath());
            return documents;
        }

        for (File file : ymlFiles) {
            String fileName = file.getName();
            String baseName = fileName.replaceFirst("(?i)\\.yml$", "");
            if (!filesToIgnore.isEmpty() && (filesToIgnore.contains(fileName.toLowerCase()) ||
                    filesToIgnore.contains(baseName.toLowerCase()))) {
                continue;
            }

            try {
                YamlDocument yaml = YamlDocument.create(file);
                String key = includeFileFormat
                        ? file.getName()
                        : file.getName().replaceFirst("\\.yml$", "");
                documents.put(key, yaml);
            } catch (IOException e) {
                ConsoleUtils.severe("Failed to load YAML file: " + file.getName());
                e.printStackTrace();
            }
        }

        return documents;
    }

    /**
     * Loads all {@code .yml} files from a given subdirectory of the plugin's data folder.
     * <p>
     * Each file is parsed into a {@link YamlDocument} and stored in a map keyed by filename.
     * </p>
     *
     * @param directory  the directory to search for .yml files
     * @return a map of filename to loaded {@link YamlDocument}, or an empty map if no valid files are found
     */
    public static Map<String, YamlDocument> getFiles(@NotNull File directory, Set<String> filesToIgnore, boolean includeFileFormat) {
        Map<String, YamlDocument> documents = new HashMap<>();
        if (!directory.exists() || !directory.isDirectory()) {
            ConsoleUtils.severe("Directory not found: " + directory.getAbsolutePath());
            return documents;
        }

        File[] ymlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (ymlFiles == null || ymlFiles.length == 0) {
            ConsoleUtils.warning("No .yml files found in: " + directory.getAbsolutePath());
            return documents;
        }

        for (File file : ymlFiles) {
            String fileName = file.getName();
            String baseName = fileName.replaceFirst("(?i)\\.yml$", "");
            if (!filesToIgnore.isEmpty() && (filesToIgnore.contains(fileName.toLowerCase()) ||
                    filesToIgnore.contains(baseName.toLowerCase()))) {
                continue;
            }

            try {
                YamlDocument yaml = YamlDocument.create(file);
                String key = includeFileFormat
                        ? file.getName()
                        : file.getName().replaceFirst("\\.yml$", "");
                documents.put(key, yaml);
            } catch (IOException e) {
                ConsoleUtils.severe("Failed to load YAML file: " + file.getName());
                e.printStackTrace();
            }
        }

        return documents;
    }

    public static List<YamlDocument> getFiles(@NotNull File directory) {
        List<YamlDocument> documents = new ArrayList<>();
        if (!directory.exists() || !directory.isDirectory()) {
            ConsoleUtils.severe("Directory not found: " + directory.getAbsolutePath());
            return documents;
        }

        File[] ymlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
        if (ymlFiles == null || ymlFiles.length == 0) {
            ConsoleUtils.warning("No .yml files found in: " + directory.getAbsolutePath());
            return documents;
        }

        for (File file : ymlFiles) {
            try {
                documents.add(YamlDocument.create(file));
            } catch (IOException e) {
                ConsoleUtils.severe("Failed to load YAML file: " + file.getName());
                e.printStackTrace();
            }
        }

        return documents;
    }

    /**
     * Reads a string value from a YAML file.
     *
     * @param fileDir   The directory of where to check the yaml file.
     * @param yamlName The name of the YAML file (e.g., "config.yml").
     * @param route    The YAML path/route (e.g., "settings.option").
     * @return The string value if found, otherwise {@code null}.
     */
    @Nullable
    public static String getString(@NotNull File fileDir, @NotNull String yamlName, @NotNull String route) {
        File file = new File(fileDir, yamlName);
        if (!file.exists()) {
            return null;
        }
        try {
            YamlDocument yaml = YamlDocument.create(file);
            return yaml.getString(route);
        } catch (IOException e) {
            ConsoleUtils.severe("Failed to read YAML file: " + yamlName + " from " + fileDir.getName() + ": " + e);
            return null;
        }
    }

    /**
     * Loads a YamlDocument if the specified file exists.
     *
     * @param directory The folder where the file should be located.
     * @param fileName  The YAML file name (e.g., "config.yml").
     * @return YamlDocument if the file exists and loads successfully, otherwise null.
     */
    @Nullable
    public static YamlDocument get(File directory, String fileName) {
        File yamlFile = new File(directory, fileName);
        if (!yamlFile.exists()) {
            return null;
        }
        try {
            return YamlDocument.create(yamlFile);
        } catch (IOException e) {
            ConsoleUtils.severe("Yaml file does not exists: " + e);
            return null;
        }
    }
}

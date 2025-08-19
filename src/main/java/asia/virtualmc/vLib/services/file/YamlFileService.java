package asia.virtualmc.vLib.services.file;

import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class YamlFileService {

    /**
     * Represents a loaded YAML file and provides convenience methods to extract mapped data.
     */
    public static class YamlFile {
        private final String prefix;
        private final YamlDocument yaml;

        /**
         * Creates a new YamlFile instance for the given plugin and file name.
         *
         * @param plugin   the plugin instance
         * @param fileName the name of the YAML file to load
         */
        public YamlFile(@NotNull Plugin plugin, @NotNull String fileName) {
            this.prefix = "[" + plugin.getName() + "]";
            this.yaml = loadYaml(plugin, fileName);
        }

        /**
         * Gets the loaded YAML document.
         *
         * @return the YAML document
         */
        public YamlDocument getYaml() {
            return yaml;
        }

        /**
         * Retrieves a section from the YAML file.
         *
         * @param route the section path
         * @return the section
         * @throws IllegalStateException if the section is not found
         */
        public Section getSection(String route) {
            Section section = yaml.getSection(route);
            if (section == null) {
                throw new IllegalStateException(prefix + " Section " + route + " not found from Yaml File " + yaml.getNameAsString());
            }
            return section;
        }

        /**
         * Creates a map from string keys to string values from the given section.
         *
         * @param section the section to read
         * @param debug   if true, prints the map for debugging
         * @return the string-to-string map
         */
        public Map<String, String> stringKeyStringMap(Section section, boolean debug) {
            Map<String, String> map = new HashMap<>();
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
         * Creates a map from string keys to integer values from the given section.
         *
         * @param section the section to read
         * @param debug   if true, prints the map for debugging
         * @return the string-to-integer map
         */
        public Map<String, Integer> stringKeyIntMap(Section section, boolean debug) {
            Map<String, Integer> map = new HashMap<>();
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
         * Creates a map from string keys to double values from the given section.
         *
         * @param section the section to read
         * @param debug   if true, prints the map for debugging
         * @return the string-to-double map
         */
        public Map<String, Double> stringKeyDoubleMap(Section section, boolean debug) {
            Map<String, Double> map = new HashMap<>();
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
         * Creates a map from integer keys to string values from the given section.
         *
         * @param section the section to read
         * @param debug   if true, prints the map for debugging
         * @return the integer-to-string map
         */
        public Map<Integer, String> intKeyStringMap(Section section, boolean debug) {
            Map<Integer, String> map = new HashMap<>();
            Set<String> keys = section.getRoutesAsStrings(false);
            if (!keys.isEmpty()) {
                for (String key : keys) {
                    int intKey = IntegerUtils.toInt(key);
                    map.put(intKey, section.getString(key));
                }
            }

            if (debug) {
                ConsoleUtils.debugMap(map);
            }

            return map;
        }

        /**
         * Creates a map from integer keys to integer values from the given section.
         *
         * @param section the section to read
         * @param debug   if true, prints the map for debugging
         * @return the integer-to-integer map
         */
        public Map<Integer, Integer> intKeyIntMap(Section section, boolean debug) {
            Map<Integer, Integer> map = new HashMap<>();
            Set<String> keys = section.getRoutesAsStrings(false);
            if (!keys.isEmpty()) {
                for (String key : keys) {
                    int intKey = IntegerUtils.toInt(key);
                    map.put(intKey, section.getInt(key));
                }
            }

            if (debug) {
                ConsoleUtils.debugMap(map);
            }

            return map;
        }

        /**
         * Creates a map from integer keys to double values from the given section.
         *
         * @param section the section to read
         * @param debug   if true, prints the map for debugging
         * @return the integer-to-double map
         */
        public Map<Integer, Double> intKeyDoubleMap(Section section, boolean debug) {
            Map<Integer, Double> map = new HashMap<>();
            Set<String> keys = section.getRoutesAsStrings(false);
            if (!keys.isEmpty()) {
                for (String key : keys) {
                    int intKey = IntegerUtils.toInt(key);
                    map.put(intKey, section.getDouble(key));
                }
            }

            if (debug) {
                ConsoleUtils.debugMap(map);
            }

            return map;
        }

        /**
         * Retrieves a set of strings from a list at the given route in the section.
         *
         * @param section the section to read
         * @param route   the route within the section
         * @return a set of strings
         */
        public Set<String> getStringSet(Section section, String route) {
            return new HashSet<>(section.getStringList(route));
        }
    }

    /**
     * Loads a YAML file for the given plugin.
     *
     * @param plugin   the plugin instance
     * @param fileName the file name
     * @return a YamlFile instance
     */
    public static YamlFile get(@NotNull Plugin plugin, @NotNull String fileName) {
        return new YamlFile(plugin, fileName);
    }

    /**
     * Loads a YAML document from the plugin's data folder.
     * <p>
     * ⚠ Internal use only — not intended for public API use.
     *
     * @param plugin   the plugin instance
     * @param fileName the file name
     * @return the loaded YamlDocument
     * @throws IllegalStateException if the file cannot be loaded
     */
    @Internal
    private static YamlDocument loadYaml(@NotNull Plugin plugin, @NotNull String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        file.getParentFile().mkdirs();

        try (InputStream defaultFile = plugin.getResource(fileName)) {
            return (defaultFile != null)
                    ? YamlDocument.create(file, defaultFile)
                    : YamlDocument.create(file);
        } catch (IOException e) {
            throw new IllegalStateException("[" + plugin.getName() + "] Failed to load YAML file: " + fileName, e);
        }
    }
}

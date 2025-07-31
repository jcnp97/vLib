package asia.virtualmc.vLib.utilities.files;

import asia.virtualmc.vLibrary.utilities.messages.ConsoleUtils;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class YAMLUtils {

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
            plugin.getLogger().severe("An error occurred when trying to read " + fileName);
            e.getCause();
        }

        return null;
    }

    public static Section getSection(@NotNull YamlDocument yaml, @NotNull String sectionPath) {
        return yaml.getSection(sectionPath);
    }

    public static Section getSection(@NotNull Plugin plugin, @NotNull String fileName, @NotNull String sectionPath) {
        File file = new File(plugin.getDataFolder(), fileName);

        try {
            InputStream defaultFile = plugin.getResource(fileName);
            YamlDocument config;

            if (defaultFile != null) {
                config = YamlDocument.create(file, defaultFile);
            } else {
                config = YamlDocument.create(file);
            }

            Section section = config.getSection(sectionPath);

            if (section == null) {
                plugin.getLogger().severe("Missing " + sectionPath + " section in " + fileName + "!");
                return null;
            }

            return section;
        } catch (IOException e) {
            plugin.getLogger().severe("An error occurred when trying to read " + fileName);
            e.getCause();
        }

        return null;
    }

    public static List<String> getList(Plugin plugin, String fileName, String mainKey, String subKey) {
        List<String> list = new ArrayList<>();

        YamlDocument yaml = getYaml(plugin, fileName);
        if (yaml == null) return null;

        Section section = getSection(yaml, mainKey);
        if (section == null) return null;

        Set<String> subKeys = section.getRoutesAsStrings(false);
        if (subKeys.isEmpty()) return null;

        for (String key : subKeys) {
            String path = mainKey + "." + key + "." + subKey;
            List<String> items = yaml.getStringList(path);
            if (items != null && !items.isEmpty()) {
                list.addAll(items);
            }
        }

        return list;
    }

    public static List<String> getList(Plugin plugin, String fileName, String sectionPath) {
        YamlDocument yaml = getYaml(plugin, fileName);
        if (yaml == null) {
            ConsoleUtils.severe("Unable to find " + fileName + "!");
            return null;
        }

        List<String> items = yaml.getStringList(sectionPath);
        if (items == null || items.isEmpty()) {
            ConsoleUtils.severe("Unable to find list at section path " + sectionPath + "!");
            return null;
        }

        return items;
    }

    public static int[] getArray(String string) {
        if (string == null) return null;

        return Arrays.stream(string.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static Map<String, Integer> getIntMap(Plugin plugin, String fileName, String path) {
        Map<String, Integer> map = new HashMap<>();

        YamlDocument yaml = getYaml(plugin, fileName);
        if (yaml == null) return null;

        Section section = getSection(yaml, path);
        if (section == null) return null;

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String key : keys) {
            map.put(key, section.getInt(key));
        }

        return map;
    }

    public static Map<String, Double> getDoubleMap(@NotNull YamlDocument yaml,
                                                   @NotNull String route) {
        Map<String, Double> map = new HashMap<>();

        Section section = getSection(yaml, route);
        if (section == null) return map;

        Set<String> keys = section.getRoutesAsStrings(false);
        for (String key : keys) {
            map.put(key, section.getDouble(key));
        }

        return map;
    }

    public static Map<Integer, String> getStringMap(@NotNull YamlDocument yaml,
                                                    @NotNull String route) {

        Map<Integer, String> map = new HashMap<>();
        Section section = getSection(yaml, route);
        if (section == null) return map;

        for (String key : section.getRoutesAsStrings(false)) {
            try {
                int intKey = Integer.parseInt(key);
                map.put(intKey, section.getString(key));
            } catch (NumberFormatException ex) {
                ConsoleUtils.severe("Skipping non-integer key " + key + " in section " + route);
            }
        }

        return map;
    }

    public static List<YamlDocument> getFiles(@NotNull Plugin plugin, @NotNull String dirPath) {
        List<YamlDocument> documents = new ArrayList<>();

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
            try {
                YamlDocument yaml = YamlDocument.create(file);
                documents.add(yaml);
            } catch (IOException e) {
                ConsoleUtils.severe("Failed to load YAML file: " + file.getName());
                e.printStackTrace();
            }
        }

        return documents;
    }

    public static void copyAllResources(JavaPlugin plugin, File jarFile) {
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

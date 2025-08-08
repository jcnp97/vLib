package asia.virtualmc.vLib.utilities.files.json;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONWriterUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Writes a String value under the given key into the specified JSON file.
     * If the file does not exist, it and any missing parent directories will be created.
     *
     * @param jsonFile the JSON file to write to
     * @param key      the key under which to store the value
     * @param value    the String value to write
     * @return true if json writing succeeds, false otherwise
     */
    public static boolean write(@NotNull File jsonFile, @NotNull String key, @NotNull String value) {
        try {
            File parent = jsonFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                ConsoleUtils.severe("Unable to create directories for " + jsonFile);
                return false;
            }

            JsonObject root = new JsonObject();
            if (jsonFile.exists()) {
                try (FileReader reader = new FileReader(jsonFile)) {
                    root = JsonParser.parseReader(reader).getAsJsonObject();
                } catch (Exception e) {
                    ConsoleUtils.severe("Unable to read a JSON file: " + e.getMessage());
                    return false;
                }
            }

            root.addProperty(key, value);
            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(root, writer);
            }
            return true;
        } catch (IOException e) {
            ConsoleUtils.severe("Unable to write key=" + key + ", value=" + value + " to " + jsonFile + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Writes an int value under the given key into the specified JSON file.
     * If the file does not exist, it and any missing parent directories will be created.
     *
     * @param jsonFile the JSON file to write to
     * @param key      the key under which to store the value
     * @param value    the int value to write
     * @return true if json writing succeeds, false otherwise
     */
    public static boolean write(@NotNull File jsonFile, @NotNull String key, int value) {
        try {
            File parent = jsonFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                ConsoleUtils.severe("Unable to create directories for " + jsonFile);
                return false;
            }

            JsonObject root = new JsonObject();
            if (jsonFile.exists()) {
                try (FileReader reader = new FileReader(jsonFile)) {
                    root = JsonParser.parseReader(reader).getAsJsonObject();
                } catch (Exception e) {
                    ConsoleUtils.severe("Unable to read a JSON file: " + e.getMessage());
                    return false;
                }
            }

            root.addProperty(key, value);
            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(root, writer);
            }
            return true;
        } catch (IOException e) {
            ConsoleUtils.severe("Unable to write key=" + key + ", value=" + value + " to " + jsonFile + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Writes a double value under the given key into the specified JSON file.
     * If the file or its parent directories do not exist, they will be created.
     *
     * @param jsonFile the JSON file to write to
     * @param key      the key under which to store the value
     * @param value    the double value to write
     * @return true if writing succeeds, false otherwise
     */
    public static boolean write(@NotNull File jsonFile, @NotNull String key, double value) {
        try {
            File parent = jsonFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                ConsoleUtils.severe("Unable to create directories for " + jsonFile);
                return false;
            }

            JsonObject root = new JsonObject();
            if (jsonFile.exists()) {
                try (FileReader reader = new FileReader(jsonFile)) {
                    root = JsonParser.parseReader(reader).getAsJsonObject();
                } catch (Exception e) {
                    ConsoleUtils.severe("Unable to read existing JSON file: " + e.getMessage());
                    return false;
                }
            }

            root.addProperty(key, value);
            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(root, writer);
            }
            return true;
        } catch (IOException e) {
            ConsoleUtils.severe("Unable to write key=" + key + ", value=" + value + " to " + jsonFile + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Writes a nested object under the given key into the specified JSON file.
     * The provided map will be converted into a JSON object.
     * If the file does not exist, it and any missing parent directories will be created.
     *
     * @param jsonFile the JSON file to write to
     * @param key      the key under which to store the object
     * @param values   the map of values to write as a nested JSON object
     * @return true if json writing succeeds, false otherwise
     */
    public static boolean write(@NotNull File jsonFile, @NotNull String key, @NotNull Map<String, Object> values) {
        try {
            File parent = jsonFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                ConsoleUtils.severe("Unable to create directories for " + jsonFile);
                return false;
            }

            JsonObject root = new JsonObject();
            if (jsonFile.exists()) {
                try (FileReader reader = new FileReader(jsonFile)) {
                    root = JsonParser.parseReader(reader).getAsJsonObject();
                } catch (Exception e) {
                    ConsoleUtils.severe("Unable to read a JSON file: " + e.getMessage());
                    return false;
                }
            }

            JsonElement element = gson.toJsonTree(values);
            root.add(key, element);
            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(root, writer);
            }
            return true;
        } catch (IOException e) {
            ConsoleUtils.severe("Unable to write new values on " + jsonFile + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Reads a String value by key from the specified JSON file.
     *
     * @param jsonFile the JSON file to read from
     * @param key      the key whose value to retrieve
     * @return the String value, or null if not found or on error
     */
    public static String readString(@NotNull File jsonFile, @NotNull String key) {
        if (!jsonFile.exists()) {
            ConsoleUtils.severe("Error when reading as file does not exist: " + jsonFile);
            return null;
        }
        try (FileReader reader = new FileReader(jsonFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement element = root.get(key);
            if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                return element.getAsString();
            }
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to retrieve String value from " + jsonFile + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Reads an int value by key from the specified JSON file.
     *
     * @param jsonFile the JSON file to read from
     * @param key      the key whose value to retrieve
     * @return the int value, or 0 if not found or on error
     */
    public static int readInt(@NotNull File jsonFile, @NotNull String key) {
        if (!jsonFile.exists()) {
            ConsoleUtils.severe("Error when reading as file does not exist: " + jsonFile);
            return 0;
        }
        try (FileReader reader = new FileReader(jsonFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement element = root.get(key);
            if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                return element.getAsInt();
            }
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to retrieve int value from " + jsonFile + ": " + e.getMessage());
        }
        return 0;
    }

    /**
     * Reads a double value by key from the specified JSON file.
     *
     * @param jsonFile the JSON file to read from
     * @param key      the key whose value to retrieve
     * @return the double value, or 0.0 if not found or on error
     */
    public static double readDouble(@NotNull File jsonFile, @NotNull String key) {
        if (!jsonFile.exists()) {
            ConsoleUtils.severe("Error when reading as file does not exist: " + jsonFile);
            return 0.0;
        }
        try (FileReader reader = new FileReader(jsonFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement element = root.get(key);
            if (element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                return element.getAsDouble();
            }
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to retrieve double value from " + jsonFile + ": " + e.getMessage());
        }
        return 0.0;
    }

    /**
     * Reads a nested object by key from the specified JSON file into a Map.
     *
     * @param jsonFile the JSON file to read from
     * @param key      the key whose object to retrieve
     * @return a Map of values, or an empty map if not found or on error
     */
    public static Map<String, Object> readMap(@NotNull File jsonFile, @NotNull String key) {
        if (!jsonFile.exists()) {
            ConsoleUtils.severe("Error when reading as file does not exist: " + jsonFile);
            return Collections.emptyMap();
        }
        try (FileReader reader = new FileReader(jsonFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonElement jsonElement = root.get(key);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject obj = jsonElement.getAsJsonObject();
                Map<String, Object> map = new HashMap<>();
                for (var entry : obj.entrySet()) {
                    JsonElement v = entry.getValue();
                    if (v.isJsonPrimitive()) {
                        var prim = v.getAsJsonPrimitive();
                        if (prim.isBoolean()) {
                            map.put(entry.getKey(), prim.getAsBoolean());
                        } else if (prim.isNumber()) {
                            Number num = prim.getAsNumber();
                            double d = num.doubleValue();
                            if (d == Math.rint(d)) map.put(entry.getKey(), num.longValue());
                            else map.put(entry.getKey(), d);
                        } else {
                            map.put(entry.getKey(), prim.getAsString());
                        }
                    } else if (v.isJsonObject()) {
                        map.put(entry.getKey(), gson.fromJson(v, Map.class));
                    } else if (v.isJsonArray()) {
                        map.put(entry.getKey(), gson.fromJson(v, List.class));
                    } else {
                        map.put(entry.getKey(), null);
                    }
                }
                return map;
            }
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to retrieve map values from " + jsonFile + ": " + e.getMessage());
        }
        return Collections.emptyMap();
    }

    /**
     * Deletes the given key (and its value) from the specified JSON file.
     *
     * @param jsonFile the JSON file to modify
     * @param key      the key to remove
     * @return true if the key was present and removed, false otherwise
     */
    public static boolean delete(@NotNull File jsonFile, @NotNull String key) {
        if (!jsonFile.exists()) {
            return false;
        }
        try {
            JsonObject root;
            try (FileReader reader = new FileReader(jsonFile)) {
                root = JsonParser.parseReader(reader).getAsJsonObject();
            }
            if (!root.has(key)) {
                return false;
            }
            root.remove(key);
            try (FileWriter writer = new FileWriter(jsonFile)) {
                gson.toJson(root, writer);
            }
            return true;
        } catch (Exception e) {
            ConsoleUtils.severe("Unable to delete " + key + " from " + jsonFile + ": " + e.getMessage());
            return false;
        }
    }
}

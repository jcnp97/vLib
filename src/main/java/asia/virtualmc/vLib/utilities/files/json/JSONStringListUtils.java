package asia.virtualmc.vLib.utilities.files.json;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.List;

public class JSONStringListUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Reads a JSON array of strings from the given file and returns it as a List<String>.
     *
     * @param file the .json file to read from (must contain a JSON array of strings)
     * @return a List of Strings parsed from the JSON array in the file
     */
    public static List<String> read(File file) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        try {
            var reader = Files.newBufferedReader(file.toPath());
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            ConsoleUtils.severe("Unable to read " + file.getName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Writes the given List<String> as a JSON array to a file in the specified output directory.
     * Creates any necessary parent directories.
     *
     * @param outputDir the directory where the JSON file should be created
     * @param content   the List of Strings to serialize as a JSON array
     * @param fileName  the name of the JSON file to create (e.g. "clone.json")
     * @return true if the file was successfully written; false otherwise
     */
    public static boolean generate(List<String> content, File outputDir, String fileName) {
        try {
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileName);
            try (var writer = Files.newBufferedWriter(outputFile.toPath())) {
                gson.toJson(content, writer);
            }

            return true;

        } catch (IOException e) {
            ConsoleUtils.severe("Unable to create a new JSON file: " + fileName + " because " + e.getMessage());
        }

        return false;
    }
}

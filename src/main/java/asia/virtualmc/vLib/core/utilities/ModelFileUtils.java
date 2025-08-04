package asia.virtualmc.vLib.core.utilities;

import asia.virtualmc.vLib.utilities.files.json.JSONFileUtils;
import asia.virtualmc.vLib.utilities.files.json.JSONStringListUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.string.StringListUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;

public class ModelFileUtils {

    public static void generate(Plugin input, String inputPath,
                                Plugin output, String outputPath,
                                String fileName, String texturePath) {

        File modelFile = JSONFileUtils.get(input, inputPath, true);
        if (modelFile == null) {
            ConsoleUtils.severe("Unable to find " + inputPath + ". Skipping model generation..");
            return;
        }

        List<String> content = JSONStringListUtils.read(modelFile);
        if (content == null || content.isEmpty()) {
            ConsoleUtils.severe(modelFile.getName() + " is empty! Skipping model generation..");
            return;
        }

        List<String> modified = StringListUtils.replace(content, "{to_replace}", texturePath);
        File outputDir = new File(output.getDataFolder(), outputPath);
        if (JSONStringListUtils.generate(modified, outputDir, fileName)) {
            ConsoleUtils.info("Successfully generated " + fileName + ".json to " + outputDir.getPath());
        }
    }
}

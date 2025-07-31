package asia.virtualmc.vLib.utilities.files;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
}

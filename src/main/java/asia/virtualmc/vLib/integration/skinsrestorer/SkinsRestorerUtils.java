package asia.virtualmc.vLib.integration.skinsrestorer;

import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import net.skinsrestorer.api.PropertyUtils;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.MojangSkinDataResult;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.SkinStorage;
import org.bukkit.entity.Player;

import java.io.*;
import java.net.URL;
import java.util.Optional;

public class SkinsRestorerUtils {
    private static SkinsRestorer skinsRestorer;

    /**
     * Downloads the current Minecraft skin of the specified player using SkinsRestorer API
     * and saves it as a PNG file in the provided output directory.
     * <p>
     * The file is named using the player's name in lowercase with spaces replaced by underscores,
     * followed by "_plushie.png".
     *
     * @param player    the player whose skin will be downloaded
     * @param outputDir the directory where the skin file will be saved
     */
    public static void getSkin(Player player, File outputDir) {
        if (skinsRestorer == null) {
            skinsRestorer = SkinsRestorerProvider.get();
        }

        SkinStorage storage = skinsRestorer.getSkinStorage();
        try {
            Optional<MojangSkinDataResult> result = storage.getPlayerSkin(player.getName(), false);
            if (result.isEmpty()) {
                MessageUtils.sendMessage(player,
                        "Could not retrieve your skin. Please report this to the administrator.", EnumsLib.MessageType.RED);
                ConsoleUtils.severe("Failed to fetch skin data for " + player.getName());
                return;
            }

            SkinProperty property = result.get().getSkinProperty();
            String textureUrl = PropertyUtils.getSkinTextureUrl(property);

            // Clean up filename (lowercase, underscores) and set .png extension
            String filename = player.getName()
                    .toLowerCase()
                    .replace(" ", "_")
                    + "_plushie" + ".png";
            File outFile = new File(outputDir, filename);

            // Download and write the PNG
            try (InputStream in = new URL(textureUrl).openStream();
                 OutputStream out = new FileOutputStream(outFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

        } catch (DataRequestException e) {
            MessageUtils.sendMessage(player, "Error fetching your skin: " + e.getMessage(), EnumsLib.MessageType.RED);
            ConsoleUtils.severe("DataRequestException for " + player.getName() + ": " + e.getMessage());
        } catch (IOException e) {
            MessageUtils.sendMessage(player, "Error saving your skin: " + e.getMessage(), EnumsLib.MessageType.RED);
            ConsoleUtils.severe("IOException while saving skin for " + player.getName() + ": " + e.getMessage());
        } catch (IllegalStateException e) {
            MessageUtils.sendMessage(player, "There are some error when trying to fetch your skin. Please report this to the administrator.");
            ConsoleUtils.severe("SkinsRestorer API not initialized for skin download of " + player.getName());
        }
    }
}

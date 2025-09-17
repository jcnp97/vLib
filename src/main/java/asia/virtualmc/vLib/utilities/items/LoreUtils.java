package asia.virtualmc.vLib.utilities.items;

import asia.virtualmc.vLib.utilities.messages.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LoreUtils {

    public static List<String> applyMonospaceFont(List<String> inputList) {
        return inputList.stream()
                .map(LoreUtils::convertToMonospaceFont)
                .collect(Collectors.toList());
    }

    private static String convertToMonospaceFont(String input) {
        String NORMAL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String CONVERTED = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder converted = new StringBuilder();
        for (char ch : input.toCharArray()) {
            int index = NORMAL.indexOf(ch);
            converted.append(index != -1 ? CONVERTED.charAt(index) : ch);
        }
        return converted.toString();
    }

    public static List<String> applyCharLimit(List<String> lore, int charCount) {
        List<String> formattedLore = new ArrayList<>();

        for (String line : lore) {
            String[] words = line.split(" ");
            StringBuilder currentLine = new StringBuilder();

            for (String word : words) {
                if (currentLine.length() + word.length() + 1 > charCount) {
                    formattedLore.add(currentLine.toString().trim());
                    currentLine.setLength(0);
                }

                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            }

            if (!currentLine.isEmpty()) {
                formattedLore.add(currentLine.toString());
            }
        }

        return formattedLore;
    }

    public static ItemStack appendLore(ItemStack item, List<String> newLore) {
        if (item == null) return null;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<Component> lore = meta.lore();
        if (lore == null) {
            lore = new ArrayList<>();
        } else {
            lore = new ArrayList<>(lore);
        }

        // Append new lore lines
        if (newLore != null && !newLore.isEmpty()) {
            lore.addAll(AdventureUtils.toComponent(newLore));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item.clone();
    }

    public static ItemStack appendLore(ItemStack item, String line) {
        return appendLore(item, Collections.singletonList(line));
    }
}

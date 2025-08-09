package asia.virtualmc.vLib.integration.inventory_framework;

import asia.virtualmc.vLib.core.guis.GUIConfig;
import asia.virtualmc.vLib.core.guis.GUIUtils;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.PaginatedPane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.component.PagingButtons;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class IFUtils {
    private static final Set<UUID> responseCache = ConcurrentHashMap.newKeySet();

    public static void confirmGui(Player player, Consumer<Boolean> callback) {
        UUID uuid = player.getUniqueId();
        responseCache.remove(uuid);

        ChestGui gui = new ChestGui(3, GUIConfig.get("confirm-gui-title"));
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        StaticPane pane = new StaticPane(0, 0, 9, 3);

        // Confirm buttons
        for (int i = 1; i <= 3; i++) {
            ItemStack confirmButton = GUIUtils.createButton(GUIConfig.getItem(), "<green>ᴄᴏɴғɪʀᴍ ᴀᴄᴛɪᴏɴ");
            GuiItem confirm = new GuiItem(confirmButton, event -> {
                callback.accept(true);
                event.getWhoClicked().closeInventory();
            });
            pane.addItem(confirm, i, 1);
        }

        // Cancel buttons
        for (int i = 5; i <= 7; i++) {
            ItemStack cancelButton = GUIUtils.createButton(GUIConfig.getItem(), "<red>ᴄᴀɴᴄᴇʟ ᴀᴄᴛɪᴏɴ");
            GuiItem cancel = new GuiItem(cancelButton, event -> {
                callback.accept(false);
                event.getWhoClicked().closeInventory();
            });
            pane.addItem(cancel, i, 1);
        }

        gui.setOnClose(event -> {
            if (responseCache.add(uuid)) {
                callback.accept(false);
            }
        });

        gui.addPane(pane);
        gui.show(player);
    }

    public static ChestGui getDisplayGui(String title, List<ItemStack> items,
                                         GuiItem prevButton, GuiItem nextButton) {
        ChestGui gui = new ChestGui(6, title);
        PaginatedPane pane = new PaginatedPane(0, 0, 9, 5);

        List<GuiItem> content = new ArrayList<>();
        for (ItemStack item : items) {
            content.add(new GuiItem(item));
        }

        pane.populateWithGuiItems(content);

        PagingButtons pagingButtons = new PagingButtons(Slot.fromXY(0, 5), 9, pane);
        pagingButtons.setBackwardButton(prevButton);
        pagingButtons.setForwardButton(nextButton);
        pagingButtons.setOnClick(event -> {
            Player player = (Player) event.getWhoClicked();
            player.playSound(player, "minecraft:ui.button.click", 1, 1);
        });

        gui.addPane(pane);
        gui.addPane(pagingButtons);
        gui.setOnGlobalClick(event -> event.setCancelled(true));

        return gui;
    }
}

package asia.virtualmc.vLib.core.guis.skills;

import asia.virtualmc.vLib.core.guis.GUIConfig;
import asia.virtualmc.vLib.core.guis.GUIItemUtils;
import asia.virtualmc.vLib.core.guis.GUIUtils;
import asia.virtualmc.vLib.integration.inventory_framework.IFUtils;
import asia.virtualmc.vLib.utilities.items.ItemStackUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RankGUIUtils {

    public static class RankData {
        public int numericalRank;
        public String currentRank;
        public String nextRank;
        public double previous;
        public double current;
        public double next;

        public RankData(int numericalRank, String currentRank, String nextRank,
                          double previous, double current, double next) {
            this.numericalRank = numericalRank;
            this.currentRank = currentRank;
            this.nextRank = nextRank;
            this.previous = previous;
            this.current = current;
            this.next = next;
        }
    }

    public static ChestGui get(Player player, RankData data, ItemStack progressLore) {
        ChestGui gui = new ChestGui(3, GUIConfig.get("rank-gui-title"));
        gui.setOnGlobalClick(event -> event.setCancelled(true));
        StaticPane staticPane = RankGUIUtils.getProgressBar(data.current, data.next,
                progressLore);

        if (data.current >= data.next) {
            for (int x = 3; x <= 5; x++) {
                ItemStack confirmButton = RankGUIUtils.allow();
                staticPane.addItem(new GuiItem(confirmButton, event ->
                        IFUtils.confirmGui(player, confirmed -> {
                            if (confirmed) {
                                process(player, data);
                            } else {
                                event.getWhoClicked().closeInventory();
                            }
                        })), x, 2);
            }
        } else {
            for (int x = 3; x <= 5; x++) {
                staticPane.addItem(new GuiItem(RankGUIUtils.deny()), x, 2);
            }
        }

        // Add stats lore
        staticPane.addItem(new GuiItem(progressLore), 8, 0);
        gui.addPane(staticPane);
        return gui;
    }

    public static void process(Player player, RankData data) {
        MessageUtils.sendMessage(player, "Rank-up success!");
        player.closeInventory();
    }

    public static ItemStack allow() {
        return GUIUtils.createButton(GUIConfig.getItem(), "<green>Process rank-up");
    }

    public static ItemStack deny() {
        return GUIUtils.createButton(GUIConfig.getItem(), "<red>Not enough points to rank-up!");
    }



    public static StaticPane getProgressBar(double current, double next, ItemStack lore) {
        StaticPane staticPane = new StaticPane(0, 0, 9, 4);

        double progress = Math.min(100, (current/next) * 100);
        int[] additions = GUIItemUtils.getBarItem(progress, 7);

        for (int x = 1; x <= 7; x++) {
            ItemStack item = ItemStackUtils.setModelData(lore.clone(), 100000 + additions[x - 1]);
            GuiItem guiItem = new GuiItem(item);
            staticPane.addItem(guiItem, x, 0);
        }

        return staticPane;
    }
}

package asia.virtualmc.vLib.core.guis.skills.sell;

import asia.virtualmc.vLib.core.guis.GUIConfig;
import asia.virtualmc.vLib.integration.inventory_framework.IFUtils;
import asia.virtualmc.vLib.integration.vault.EconomyUtils;
import asia.virtualmc.vLib.services.bukkit.ComponentService;
import asia.virtualmc.vLib.utilities.bukkit.SoundUtils;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.items.ItemStackUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.OutlinePane;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class SellGUI {
    private final SellGUIHandler handler;
    private final String prefix;

    public SellGUI(Plugin plugin, SellGUIHandler handler) {
        this.handler = handler;
        this.prefix = "[" + plugin.getName() + "]";
    }

    public void open(Player player) {
        ChestGui gui = buildGui(player);
        if (gui != null) {
            gui.show(player);
            return;
        }

        MessageUtils.sendMessage(player, "You don't have any sellables.", EnumsLib.MessageType.RED);
    }

    private ChestGui buildGui(Player player) {
        return new SellProcess(player).getGui();
    }

    public class SellProcess {
        private final Player player;
        private final double sellMultiplier;
        private final Map<Integer, ItemStack> snapshot;

        private double totalAmount;
        private final ChestGui gui = new ChestGui(6, "§f<shift:-48>\uE0E8");
        private final StaticPane staticPane = new StaticPane(9, 6);

        SellProcess(Player player) {
            this.player = player;
            this.sellMultiplier = handler.getSellMultiplier(player.getUniqueId());
            this.snapshot = new HashMap<>();
            totalAmount = 0;
        }

        public ChestGui getGui() {
            // Loop through inventory
            int guiSlot = 0;
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null || !item.hasItemMeta()) continue;

                ItemStack clone = item.clone();
                double value = handler.getValue(clone) * clone.getAmount();
                if (value > 0) {
                    // Create a snapshot: Used to check if player has the same inventory before processing sell.
                    snapshot.put(i, clone);

                    // Add value to total amount
                    totalAmount += value;

                    // Create GuiItem for IF GUI
                    staticPane.addItem(sellableItem(clone, i, value, guiSlot), Slot.fromIndex(guiSlot));

                    // Increment slot for static pane
                    guiSlot++;
                }
            }

            if (snapshot.isEmpty() || totalAmount <= 0) return null;

            // Add buttons
            confirmButton();
            exitButton();

            gui.addPane(staticPane);
            gui.setOnGlobalClick(event -> event.setCancelled(true));
            return gui;
        }

//        private GuiItem sellableItem(ItemStack item, int slot, double value) {
//            final GuiItem[] holder = new GuiItem[1]; // holder for the reference
//
//            holder[0] = new GuiItem(item, event -> {
//                // Remove value from total amount
//                totalAmount -= value;
//                updateTotalAmount();
//
//                // Remove from snapshot
//                snapshot.remove(slot);
//
//                // Remove from the pane
//                outlinePane.removeItem(holder[0]);
//
//                // Refresh GUI
//                gui.update();
//            });
//
//            return holder[0];
//        }

        private GuiItem sellableItem(ItemStack item, int snapshotSlot, double value, int guiSlot) {
            return new GuiItem(item, event -> {
                // Remove value from total amount
                totalAmount -= value;
                updateTotalAmount();

                // Remove from snapshot
                snapshot.remove(snapshotSlot);

                // Remove from the pane
                staticPane.removeItem(Slot.fromIndex(guiSlot));

                // Refresh GUI
                gui.update();
            });
        }

        private void confirmButton() {
            List<String> lore = new ArrayList<>(List.of("<white>\uE0AE to sell"));
            staticPane.addItem(new GuiItem(ComponentService.get(Material.PAPER,
                    "<gray>Total Amount: <green>$" + totalAmount,
                    lore, GUIConfig.getItemModel()), event -> {
                process(player);
                event.getWhoClicked().closeInventory();
            }), Slot.fromIndex(52));
        }

        private void exitButton() {
            staticPane.addItem(new GuiItem(GUIConfig.getItem("<red>Exit"), event -> {
                event.getWhoClicked().closeInventory();
            }), Slot.fromIndex(53));
        }

        private void updateTotalAmount() {
            staticPane.removeItem(Slot.fromIndex(52));
            confirmButton();
        }

        private void process(Player player) {
            if (totalAmount <= 0) {
                MessageUtils.sendMessage(player, "Couldn't process your request because there are no sellables found.", EnumsLib.MessageType.RED);
                return;
            }

            if (checkIntegrity() && deleteItems()) {
                EconomyUtils.add(player, totalAmount * sellMultiplier);
                SoundUtils.play(player, "cozyvanilla:money_gain");
            }
        }

        private boolean deleteItems() {
            for (int slot : snapshot.keySet()) {
                try {
                    player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                } catch (Exception e) {
                    ConsoleUtils.severe(prefix, "An error occurred when trying to delete and sell items on slot " + slot + ": " + e);
                    return false;
                }
            }

            return true;
        }

        // Check if player has the same inventory from snapshot
        private boolean checkIntegrity() {
            for (Map.Entry<Integer, ItemStack> entry : snapshot.entrySet()) {
                ItemStack currentItem = player.getInventory().getItem(entry.getKey());
                if (!entry.getValue().equals(currentItem)) {
                    MessageUtils.sendMessage(player, "Your inventory had changed. Please re-open GUI.", EnumsLib.MessageType.RED);
                    return false;
                }
            }

            return true;
        }
    }
}
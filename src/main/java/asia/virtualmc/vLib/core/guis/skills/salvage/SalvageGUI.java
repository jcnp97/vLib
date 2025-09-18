package asia.virtualmc.vLib.core.guis.skills.salvage;

import asia.virtualmc.vLib.core.guis.GUIConfig;
import asia.virtualmc.vLib.utilities.bukkit.SoundUtils;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.items.LoreUtils;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import asia.virtualmc.vLib.utilities.text.StringUtils;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class SalvageGUI {
    private final SalvageGUIHandler handler;
    private final String prefix;
    private record Salvageable(int rarityId, int amount) {}

    public SalvageGUI(Plugin plugin, SalvageGUIHandler handler) {
        this.handler = handler;
        this.prefix = "[" + plugin.getName() + "]";
    }

    public void open(Player player) {
        ChestGui gui = buildGui(player);
        if (gui != null) {
            gui.show(player);
            return;
        }

        MessageUtils.sendMessage(player, "You don't have any salvageables.", EnumsLib.MessageType.RED);
    }

    private ChestGui buildGui(Player player) {
        return new SalvageProcess(player).getGui();
    }

    public class SalvageProcess {
        private final Player player;
        private final Map<Integer, Salvageable> snapshot;
        private final Map<Integer, Integer> components;

        private final ChestGui gui = new ChestGui(6, GUIConfig.getMenu("salvage_menu"));
        private final StaticPane staticPane = new StaticPane(9, 6);

        SalvageProcess(Player player) {
            this.player = player;
            this.snapshot = new HashMap<>();
            this.components = new HashMap<>();
        }

        public ChestGui getGui() {
            // Loop through inventory
            int guiSlot = 0;
            for (int i = 0; i < 36; i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item == null || !item.hasItemMeta()) continue;

                ItemStack clone = item.clone();
                int rarityId = handler.getRarityId(clone);
                if (rarityId > 0) {
                    int amount = clone.getAmount();

                    // Create a snapshot: Used to check if player has the same inventory before processing sell.
                    snapshot.put(i, new Salvageable(rarityId, amount));

                    // Add value to total amount
                    components.merge(rarityId, amount, Integer::sum);

                    // Append value to clone's lore
                    clone = LoreUtils.appendLore(clone, "<green>" +
                            getComponentName(handler.getComponent(rarityId)) + " <gray>× <green>" + amount);

                    // Create GuiItem for IF GUI
                    staticPane.addItem(salvageableItem(clone, i, guiSlot, rarityId, amount), Slot.fromIndex(guiSlot));

                    // Increment slot for static pane
                    guiSlot++;
                }
            }

            if (snapshot.isEmpty() || components.isEmpty()) return null;

            // Add buttons
            confirmButton();
            exitButton();

            gui.addPane(staticPane);
            gui.setOnGlobalClick(event -> event.setCancelled(true));
            return gui;
        }

        private GuiItem salvageableItem(ItemStack item, int snapshotSlot, int guiSlot, int rarityId, int amount) {
            return new GuiItem(item, event -> {
                // Remove from snapshot
                snapshot.remove(snapshotSlot);

                // Remove from components
                components.merge(rarityId, amount, (oldValue, newValue) -> {
                    int updated = oldValue + newValue;
                    return updated > 0 ? updated : null;
                });

                // Remove from the pane
                staticPane.removeItem(Slot.fromIndex(guiSlot));

                // Refresh GUI
                gui.update();
            });
        }

        private void confirmButton() {
            staticPane.addItem(new GuiItem(GUIConfig.getLeftClickItem("<green>Confirm process"), event -> {
                process(player);
                event.getWhoClicked().closeInventory();
            }), Slot.fromIndex(52));
        }

        private void exitButton() {
            staticPane.addItem(new GuiItem(GUIConfig.getLeftClickItem("<red>Exit"), event -> {
                event.getWhoClicked().closeInventory();
            }), Slot.fromIndex(53));
        }

        private String getComponentName(String componentName) {
            return StringUtils.format(componentName);
        }

        private void process(Player player) {
            if (components.isEmpty()) {
                MessageUtils.sendMessage(player, "Your salvage inventory is empty!", EnumsLib.MessageType.RED);
                return;
            }

            if (checkIntegrity() && deleteItems()) {
                for (Map.Entry<Integer, Integer> entry : components.entrySet()) {
                    String componentName = handler.getComponent(entry.getKey());
                    int amount = entry.getValue();

                    handler.addComponent(player.getUniqueId(), componentName, amount);
                    MessageUtils.sendMessage(player, "You have received: "
                            + getComponentName(componentName) + " × " + amount, EnumsLib.MessageType.GREEN);
                }

                SoundUtils.play(player, "minecraft:block.anvil.use");
            }
        }

        private boolean deleteItems() {
            for (int slot : snapshot.keySet()) {
                try {
                    player.getInventory().setItem(slot, new ItemStack(Material.AIR));
                } catch (Exception e) {
                    ConsoleUtils.severe(prefix, "An error occurred when trying to delete and salvage items on slot " + slot + ": " + e);
                    return false;
                }
            }

            return true;
        }

        // Check if player has the same inventory from snapshot
        private boolean checkIntegrity() {
            for (Map.Entry<Integer, Salvageable> entry : snapshot.entrySet()) {
                ItemStack currentItem = player.getInventory().getItem(entry.getKey());
                if (currentItem == null) return false;

                int rarityId = handler.getRarityId(currentItem);
                int amount = currentItem.getAmount();
                Salvageable salvageable = entry.getValue();

                if (salvageable.amount != amount || salvageable.rarityId != rarityId) {
                    MessageUtils.sendMessage(player, "Your inventory had changed. Please re-open GUI.", EnumsLib.MessageType.RED);
                    return false;
                }
            }

            return true;
        }
    }
}
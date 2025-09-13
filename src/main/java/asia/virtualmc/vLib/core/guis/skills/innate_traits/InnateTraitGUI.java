package asia.virtualmc.vLib.core.guis.skills.innate_traits;

import asia.virtualmc.vLib.core.configs.InnateTraitConfig;
import asia.virtualmc.vLib.core.guis.GUIConfig;
import asia.virtualmc.vLib.core.utilities.ProgressBarUtils;
import asia.virtualmc.vLib.integration.inventory_framework.IFUtils;
import asia.virtualmc.vLib.services.bukkit.ComponentService;
import asia.virtualmc.vLib.utilities.bukkit.SoundUtils;
import asia.virtualmc.vLib.utilities.digit.MathUtils;
import asia.virtualmc.vLib.utilities.paper.AsyncUtils;
import asia.virtualmc.vLib.utilities.paper.SyncUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class InnateTraitGUI {
    private final Plugin plugin;
    private final InnateTraitHandler handler;
    private final Cache<UUID, ChestGui> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public InnateTraitGUI(Plugin plugin, InnateTraitHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    private record PlayerData(
            UUID uuid,
            int[] currentTraits,
            int traitPoints,
            Map<String, InnateTraitConfig.InnateTrait> traits
    ) {}

    public void open(Player player) {
        UUID uuid = player.getUniqueId();
        ChestGui cached = cache.getIfPresent(uuid);
        if (cached != null) {
            cached.show(player);
            return;
        }

        AsyncUtils.runAsyncThenSync(plugin,
                () -> getData(uuid),
                (snapshot) -> {
                    ChestGui gui = buildGui(player, snapshot);
                    cache.put(uuid, gui);
                    gui.show(player);
                }
        );
    }

    private PlayerData getData(UUID uuid) {
        return new PlayerData(
                uuid,
                handler.getAllTraits(uuid),
                handler.getTraitPoints(uuid),
                handler.getTraits()
        );
    }

    private ChestGui buildGui(Player player, PlayerData snap) {
        Rank rank = new Rank(player, snap);
        return rank.getGui();
    }

    public class Rank {
        private final Player player;
        private final PlayerData data;
        private final StaticPane pane = new StaticPane(9, 6);
        private final ChestGui gui = new ChestGui(5, "");

        // Trait Values (On Upgrade Mode)
        private final int[] traitValues = new int[]{0, 0, 0, 0};
        private int remainingPoints;

        public Rank(Player player, PlayerData data) {
            this.player = player;
            this.data = data;
            this.remainingPoints = data.traitPoints;
        }

        public ChestGui getGui() {
            if (data.traitPoints > 0) {
                gui.setTitle(GUIConfig.get("traits_gui_allow"));
                upgradeButton();
            } else {
                gui.setTitle(GUIConfig.get("traits_gui_deny"));
            }

            // Progress Bar & Items
            for (String traitName : data.traits.keySet()) {
                applyProgressBar(traitName);
                applyTraitItem(traitName);
            }

            // Exit
            exitButton();

            // Info
            //pane.addItem(new GuiItem(getInfo()), Slot.fromIndex(17));

            gui.addPane(pane);
            gui.setOnGlobalClick(event -> event.setCancelled(true));
            return gui;
        }

        private int getTraitId(String traitName) {
            return switch (traitName) {
                case "wisdom_trait" -> 1;
                case "charisma_trait" -> 2;
                case "karma_trait" -> 3;
                case "dexterity_trait" -> 4;
                default -> 0;
            };
        }

        private String getTraitColor(String traitName) {
            return switch (traitName) {
                case "wisdom_trait" -> "blue";
                case "charisma_trait" -> "yellow";
                case "karma_trait" -> "red";
                case "dexterity_trait" -> "green";
                default -> "";
            };
        }

        private void applyProgressBar(String traitName) {
            int id = getTraitId(traitName);
            if (id == 0) return;

            int current = data.currentTraits[id - 1];
            InnateTraitConfig.InnateTrait trait = data.traits.get(traitName);
            if (trait == null) return;

            int maxLevel = trait.maxLevel();
            String title = trait.name() + " Level: <green>" +
                    current + "<gray>/<red>" + maxLevel;
            double progress = MathUtils.percent(current, maxLevel);
            ProgressBarUtils.getAsItem(pane, progress, 6,
                    2, id - 1, title, "cozyvanilla_guiitems:bar_outlined_" +
                            getTraitColor(traitName), trait.lore());
        }

        private void applyTraitItem(String traitName) {
            int id = getTraitId(traitName);
            if (id == 0) return;

            InnateTraitConfig.InnateTrait trait = data.traits.get(traitName);
            if (trait == null) return;

            GuiItem item = new GuiItem(ComponentService.get(Material.PAPER, "<gray>" + trait.name(),
                    new ArrayList<>(), trait.itemModel()));
            pane.addItem(item, 1, id - 1);
        }

        private void upgradeButton() {
            for (int i = 1; i <= 3; i++) {
                pane.addItem(new GuiItem(GUIConfig.getItem(
                        "<green>Enable Upgrade Mode"), event -> {
                    upgradeMode();
                }), i, 4);
            }
        }

        private void exitButton() {
            for (int i = 1; i <= 3; i++) {
                pane.addItem(new GuiItem(GUIConfig.getItem(
                        "<red>Exit"), event -> {
                    event.getWhoClicked().closeInventory();
                }), i + 4, 4);
            }
        }

        private void applyUpgradeToTraitItem(String traitName) {
            int id = getTraitId(traitName);
            if (id == 0) return;

            InnateTraitConfig.InnateTrait trait = data.traits.get(traitName);
            if (trait == null) return;

            // Remove existing item first
            pane.removeItem(1, id - 1);
            List<String> lore = new ArrayList<>(Arrays.asList("<white>\uE0AE to add", "<white>\uE0C4 to remove"));

            ItemStack item = ComponentService.get(Material.PAPER, trait.name(), lore, trait.itemModel());
            if (remainingPoints > 0) {
                pane.addItem(new GuiItem(item, event -> {
                    int traitValue = traitValues[id - 1];
                    if (event.isLeftClick()) {
                        traitValues[id - 1] = traitValue + 1;
                        remainingPoints--;
                        gui.update();
                    } else if (event.isRightClick() && traitValue > 0) {
                        traitValues[id - 1] = traitValue - 1;
                        remainingPoints++;
                        gui.update();
                    }
                }), 1, (id - 1));
            } else {
                pane.addItem(new GuiItem(item, event -> {
                    int traitValue = traitValues[id - 1];
                    if (event.isRightClick() && traitValue > 0) {
                        traitValues[id - 1] = traitValue - 1;
                        remainingPoints++;
                        gui.update();
                    }
                }), 1, (id - 1));
            }
        }

        private void confirmButton() {
            for (int i = 1; i <= 3; i++) {
                // Remove existing item first
                pane.removeItem(i, 4);
                pane.addItem(new GuiItem(GUIConfig.getItem(
                        "<green>Confirm"), event -> {
                    IFUtils.confirmGui(player, result -> {
                        if (result) {
                            process();
                            event.getWhoClicked().closeInventory();
                        } else {
                            event.getWhoClicked().closeInventory();
                        }
                    });
                }), i, 4);
            }
        }

        private void upgradeMode() {
            gui.setTitle(GUIConfig.get("traits_gui_upgrade"));
            for (String traitName : data.traits.keySet()) {
                applyUpgradeToTraitItem(traitName);
            }

            gui.update();
        }

        private void process() {
            SyncUtils.runSync(plugin, () -> {
                SoundUtils.play(player, "cozyvanilla:misc_levelup");
            });

            handler.addTraits(data.uuid, traitValues);
            handler.setTraitPoints(data.uuid, remainingPoints);
            cache.invalidate(data.uuid);

            // Re-open GUI
            open(player);
        }
    }
}
package asia.virtualmc.vLib.core.guis.skills.ranks;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.core.guis.GUIConfig;
import asia.virtualmc.vLib.core.utilities.ProgressBarUtils;
import asia.virtualmc.vLib.integration.inventory_framework.IFUtils;
import asia.virtualmc.vLib.services.bukkit.ComponentService;
import asia.virtualmc.vLib.utilities.bukkit.SoundUtils;
import asia.virtualmc.vLib.utilities.digit.MathUtils;
import asia.virtualmc.vLib.utilities.digit.StringDigitUtils;
import asia.virtualmc.vLib.utilities.messages.TitleUtils;
import asia.virtualmc.vLib.utilities.paper.AsyncUtils;
import asia.virtualmc.vLib.utilities.paper.SyncUtils;
import asia.virtualmc.vLib.utilities.text.StringUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RankGUI {
    private final RankGUIHandler handler;
    private final Cache<UUID, ChestGui> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public RankGUI(RankGUIHandler handler) {
        this.handler = handler;
    }

    public class Rank {
        private final Player player;
        private final UUID uuid;
        private final int numericalRank;
        private final String currentRankName;
        private final String nextRankName;
        private final double currentPts;
        private final double nextPts;
        private final String nextRankTag;

        private final StaticPane pane = new StaticPane(9, 6);

        public Rank(Player player) {
            this.player = player;
            this.uuid = player.getUniqueId();
            this.numericalRank = handler.getRankId(uuid);
            this.currentRankName = handler.getRankName(numericalRank);
            this.nextRankName = handler.getRankName(numericalRank + 1);
            this.currentPts = handler.getCurrentPts(uuid);
            this.nextPts = handler.getNextPts(numericalRank + 1);
            this.nextRankTag = handler.getNextRankTag(numericalRank + 1);
        }

        public ChestGui getGui() {
            ChestGui gui;
            if (currentPts >= nextPts) {
                gui = new ChestGui(2, GUIConfig.get("ranks_gui_allow"));
            } else {
                gui = new ChestGui(2, GUIConfig.get("ranks_gui_deny"));
            }

            // Progress Bar
            String title = "<gray>Rank Points: <green>" + StringDigitUtils.formatDouble(currentPts, false) +
                    "<gray>/<red>" + StringDigitUtils.formatDouble(nextPts, false);
            double progress = MathUtils.percent(currentPts, nextPts);
            ProgressBarUtils.getAsItem(pane, progress, 5,
                    2, 0, title, "cozyvanilla_guiitems:bar_outlined", getBonusesLore());

            // Rank Items
            pane.addItem(new GuiItem(getRankItem()), Slot.fromIndex(1));
            pane.addItem(new GuiItem(getNextRankItem()), Slot.fromIndex(7));

            // Rank-up
            if (currentPts >= nextPts) {
                pane.addItem(new GuiItem(GUIConfig.getItem("<green>Click to Rank-up"), event -> {
                    IFUtils.confirmGui(player, result -> {
                        if (result) {
                            process();
                            event.getWhoClicked().closeInventory();
                        } else {
                            event.getWhoClicked().closeInventory();
                        }
                    });
                }), Slot.fromIndex(11));
            } else {
                pane.addItem(new GuiItem(GUIConfig.getItem("<red>Not enough points!")), Slot.fromIndex(11));
            }

            // Exit
            pane.addItem(new GuiItem(GUIConfig.getItem("<red>Exit"), event -> {
                event.getWhoClicked().closeInventory();
            }), Slot.fromIndex(15));

            // Info
            pane.addItem(new GuiItem(getInfo()), Slot.fromIndex(17));

            gui.addPane(pane);
            gui.setOnGlobalClick(event -> event.setCancelled(true));
            return gui;
        }

        private List<String> getBonusesLore() {
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("<gray>Bonuses:");
            for (Map.Entry<String, Double> entry : handler.getBonuses().entrySet()) {
                lore.add("<gray>• <yellow>" + StringUtils.format(entry.getKey()) + ": <green>" +
                        StringDigitUtils.formatDouble(entry.getValue() * numericalRank, false) + "%");
            }

            return lore;
        }

        private ItemStack getInfo() {
            List<String> lore = new ArrayList<>();
            String title = "<gray>The following statistics affect your rank points: ";
            Set<String> statistics = handler.getStatistics();

            for (String statistic : statistics) {
                if (!statistic.equals("rank_id")) {
                    lore.add("<gray>• <yellow>" + StringUtils.format(statistic));
                }
            }

            return ComponentService.get(Material.BOOK, title, lore, "");
        }

        private ItemStack getRankItem() {
            if (numericalRank == 0) return GUIConfig.getItem(currentRankName);
            String itemModel = "cozyvanilla_guiitems:skill_rank_" + Math.min(numericalRank, 70);
            return ComponentService.get(Material.PAPER, "<gray>Current Rank: " + currentRankName, new ArrayList<>(), itemModel);
        }

        private ItemStack getNextRankItem() {
            String itemModel = "cozyvanilla_guiitems:skill_rank_" + Math.min(numericalRank + 1, 70);
            return ComponentService.get(Material.PAPER, "<gray>Next Rank: " + nextRankName, new ArrayList<>(), itemModel);
        }

        private void process() {
            SyncUtils.runSync(Main.getInstance(), () -> {
                SoundUtils.play(player, "cozyvanilla:misc_levelup");
                TitleUtils.send(player, "<white><!shadow>" + nextRankTag, "<gray>New Rank: " + nextRankName);
            });
            handler.incrementRankId(uuid);
        }
    }

    private ChestGui getGui(Player player) {
        return new Rank(player).getGui();
    }

    private void get(Player player) {
        AsyncUtils.runAsyncThenSync(Main.getInstance(),
                () -> getGui(player), (result) -> {
                    cache.put(player.getUniqueId(), result);
                    result.show(player);
                });
    }

    public void open(Player player) {
        UUID uuid = player.getUniqueId();
        ChestGui gui = cache.getIfPresent(uuid);
        if (gui == null) {
            get(player);
            return;
        }

        gui.show(player);
    }
}

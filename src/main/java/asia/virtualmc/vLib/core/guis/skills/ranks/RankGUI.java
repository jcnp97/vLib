package asia.virtualmc.vLib.core.guis.skills.ranks;

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
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RankGUI {
    private final Plugin plugin;
    private final RankGUIHandler handler;
    private final Cache<UUID, ChestGui> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public RankGUI(Plugin plugin, RankGUIHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    private record RankSnapshot(
            UUID uuid,
            int rankId,
            String currentRankName,
            String nextRankName,
            double currentPts,
            double nextPts,
            String nextRankTag,
            Set<String> statistics,
            Map<String, Double> bonuses
    ) {}

    public void open(Player player) {
        UUID uuid = player.getUniqueId();
        ChestGui cached = cache.getIfPresent(uuid);
        if (cached != null) {
            cached.show(player);
            return;
        }

        AsyncUtils.runAsyncThenSync(plugin,
                () -> buildSnapshot(uuid),
                (snapshot) -> {
                    ChestGui gui = buildGui(player, snapshot);
                    cache.put(uuid, gui);
                    gui.show(player);
                }
        );
    }

    private RankSnapshot buildSnapshot(UUID uuid) {
        int rankId = handler.getRankId(uuid);
        return new RankSnapshot(
                uuid,
                rankId,
                handler.getRankName(rankId),
                handler.getRankName(rankId + 1),
                handler.getCurrentPts(uuid),
                handler.getRankPoints(rankId + 1),
                handler.getRankTag(rankId + 1),
                handler.getStatistics(),
                handler.getBonuses()
        );
    }

    private ChestGui buildGui(Player player, RankSnapshot snap) {
        Rank rank = new Rank(player, snap);
        return rank.getGui();
    }

    public class Rank {
        private final Player player;
        private final RankSnapshot snap;
        private final StaticPane pane = new StaticPane(9, 6);

        public Rank(Player player, RankSnapshot snap) {
            this.player = player;
            this.snap = snap;
        }

        public ChestGui getGui() {
            ChestGui gui;
            if (snap.currentPts >= snap.nextPts) {
                gui = new ChestGui(2, GUIConfig.getMenu("ranks_menu_allow"));
            } else {
                gui = new ChestGui(2, GUIConfig.getMenu("ranks_menu_deny"));
            }

            // Progress Bar
            String title = "<gray>Rank Points: <green>" +
                    StringDigitUtils.formatDouble(snap.currentPts, false) +
                    "<gray>/<red>" + StringDigitUtils.formatDouble(snap.nextPts, false);
            double progress = MathUtils.percent(snap.currentPts, snap.nextPts);
            ProgressBarUtils.getAsItem(pane, progress, 5,
                    2, 0, title, "cozyvanilla_guiitems:bar_outlined", getBonusesLore());

            // Rank Items
            pane.addItem(new GuiItem(getRankItem()), Slot.fromIndex(1));
            pane.addItem(new GuiItem(getNextRankItem()), Slot.fromIndex(7));

            // Rank-up button
            if (snap.currentPts >= snap.nextPts) {
                pane.addItem(new GuiItem(GUIConfig.getLeftClickItem("<green>Click to Rank-up"), event -> {
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
                pane.addItem(new GuiItem(GUIConfig.getInvisibleItem("<red>Not enough points!")), Slot.fromIndex(11));
            }

            // Exit
            pane.addItem(new GuiItem(GUIConfig.getLeftClickItem("<red>Exit"), event -> {
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
            for (Map.Entry<String, Double> entry : snap.bonuses.entrySet()) {
                lore.add("<gray>• <yellow>" + StringUtils.format(entry.getKey()) + ": <green>" +
                        StringDigitUtils.formatDouble(entry.getValue() * snap.rankId, false) + "%");
            }
            return lore;
        }

        private ItemStack getInfo() {
            List<String> lore = new ArrayList<>();
            String title = "<gray>The following statistics affect your rank points: ";
            for (String statistic : snap.statistics) {
                if (!statistic.equals("rank_id")) {
                    lore.add("<gray>• <yellow>" + StringUtils.format(statistic));
                }
            }
            return ComponentService.get(Material.BOOK, title, lore, "");
        }

        private ItemStack getRankItem() {
            if (snap.rankId == 0) return GUIConfig.getInvisibleItem(snap.currentRankName);
            String itemModel = "cozyvanilla_guiitems:skill_rank_" + Math.min(snap.rankId, 70);
            return ComponentService.get(Material.PAPER, "<gray>Current Rank: " + snap.currentRankName, new ArrayList<>(), itemModel);
        }

        private ItemStack getNextRankItem() {
            String itemModel = "cozyvanilla_guiitems:skill_rank_" + Math.min(snap.rankId + 1, 70);
            return ComponentService.get(Material.PAPER, "<gray>Next Rank: " + snap.nextRankName, new ArrayList<>(), itemModel);
        }

        private void process() {
            SyncUtils.runSync(plugin, () -> {
                SoundUtils.play(player, "cozyvanilla:misc_levelup");
                TitleUtils.send(player, "<white><!shadow>" + snap.nextRankTag, "<gray>New Rank: " + snap.nextRankName);
            });

            handler.incrementRankId(snap.uuid);
            cache.invalidate(snap.uuid);

            // Re-open GUI after 2.5s
            plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> {
                open(player);
            }, 50L);
        }
    }
}
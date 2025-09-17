package asia.virtualmc.vLib.core.guis.skills.talent_tree;

import asia.virtualmc.vLib.core.configs.TalentTreeConfig;
import asia.virtualmc.vLib.core.guis.GUIConfig;
import asia.virtualmc.vLib.integration.inventory_framework.IFUtils;
import asia.virtualmc.vLib.services.bukkit.ComponentService;
import asia.virtualmc.vLib.utilities.bukkit.SoundUtils;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.digit.StringDigitUtils;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import asia.virtualmc.vLib.utilities.paper.AsyncUtils;
import asia.virtualmc.vLib.utilities.paper.SyncUtils;
import asia.virtualmc.vLib.utilities.text.StringListUtils;
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

public class TalentTreeGUI {
    private final Plugin plugin;
    private final TalentGUIHandler handler;
    private final Cache<UUID, ChestGui> cache = Caffeine.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public TalentTreeGUI(Plugin plugin, TalentGUIHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    private record TalentSnapshot(
            UUID uuid,
            Map<String, Integer> talentData,
            Map<String, TalentTreeConfig.Talent> talents,
            int skillLevel,
            int talentPoints,
            String masterTalent
    ) {}

    public void open(Player player) {
        UUID uuid = player.getUniqueId();
        ChestGui cached = cache.getIfPresent(uuid);
        if (cached != null) {
            cached.show(player);
            return;
        }

        // Step 1: Gather data async
        AsyncUtils.runAsyncThenSync(plugin,
                () -> buildSnapshot(uuid),
                (snapshot) -> {
                    // Step 2: Build GUI sync
                    ChestGui gui = buildGui(player, snapshot);
                    cache.put(uuid, gui);
                    gui.show(player);
                });
    }

    private TalentSnapshot buildSnapshot(UUID uuid) {
        return new TalentSnapshot(
                uuid,
                handler.getTalentData(uuid),
                handler.getTalents(),
                handler.getSkillLevel(uuid),
                handler.getTalentPoints(uuid),
                handler.getSpecialTalentKey()
        );
    }

    private ChestGui buildGui(Player player, TalentSnapshot snap) {
        return new TalentTree(player, snap).getGui();
    }

    /**
     * Inner sync-only class. Consumes snapshot to build IF GUI.
     */
    public class TalentTree {
        private final Player player;
        private final TalentSnapshot snap;
        private final boolean hasTalent19;
        private final ChestGui gui = new ChestGui(6, GUIConfig.getMenu("talent_menu"));
        private final StaticPane pane = new StaticPane(9, 6);

        TalentTree(Player player, TalentSnapshot snap) {
            this.player = player;
            this.snap = snap;
            this.hasTalent19 = snap.masterTalent != null
                    && snap.talentData.getOrDefault(snap.masterTalent, 0) >= 1;
        }

        public ChestGui getGui() {
            for (Map.Entry<String, TalentTreeConfig.Talent> entry : snap.talents.entrySet()) {
                String talentName = entry.getKey();
                TalentTreeConfig.Talent talent = entry.getValue();

                if (talentName.equals("information")) {
                    pane.addItem(getInformation(talent), Slot.fromIndex(53));
                    continue;
                }


                int talentLevel = snap.talentData.getOrDefault(talentName, 0);
                boolean isUnlocked = hasRequiredTalents(talent) &&
                        hasRequiredSkillLevel(talent.reqLevel());
                boolean isMaxLevel = isMaxLevel(talentLevel, talent);
                boolean hasEnoughPoints = snap.talentPoints >= talent.cost();

                List<String> lore = getLore(talent.lore(), isMaxLevel, isUnlocked,
                        talentLevel, hasEnoughPoints, talent);
                lore = replaceValues(talentLevel, lore, talent);

                String itemModel = getItemModel(talent.itemModel(), isUnlocked, isMaxLevel, talentLevel, talent.maxLevel());
                ItemStack item = ComponentService.get(talent.material(), talent.displayName(), lore, itemModel);

                if (talentLevel > 0 && talentLevel < 64) {
                    item.setAmount(talentLevel);
                }

                if (isUnlocked && hasEnoughPoints && !isMaxLevel) {
                    pane.addItem(new GuiItem(item.clone(), event -> {
                        IFUtils.confirmGui(player, result -> {
                            if (result) {
                                handler.subtractTalentPoints(snap.uuid, talent.cost());
                                handler.incrementTalent(snap.uuid, talentName);
                                process(talent.displayName(), talentLevel + 1);
                                event.getWhoClicked().closeInventory();
                            } else {
                                event.getWhoClicked().closeInventory();
                            }
                        });
                    }), Slot.fromIndex(talent.slot()));
                } else {
                    pane.addItem(new GuiItem(item.clone()), Slot.fromIndex(talent.slot()));
                }

                addPaths(isUnlocked, talent);
            }

            gui.addPane(pane);
            gui.setOnGlobalClick(event -> event.setCancelled(true));
            return gui;
        }

        private GuiItem getInformation(TalentTreeConfig.Talent talent) {
            String title = talent.displayName();
            title = StringUtils.replace(title, "{talent_points}", String.valueOf(snap.talentPoints));
            List<String> lore = talent.lore();
            return new GuiItem(ComponentService.get(talent.material(), title, new ArrayList<>(lore), talent.itemModel()));
        }

        private List<String> getLore(List<String> lore, boolean isMaxLevel, boolean isUnlocked,
                                     int talentLevel, boolean hasEnoughPoints,
                                     TalentTreeConfig.Talent talent) {
            List<String> newLore = new ArrayList<>(lore);
            newLore.add("");

            if (!isUnlocked) {
                newLore.add("<gray>Requires:");
                if (snap.skillLevel < talent.reqLevel()) {
                    newLore.add("<red>• Farming Level " + talent.reqLevel());
                } else {
                    newLore.add("<green>• <st>Farming Level " + talent.reqLevel() + "</st>");
                }

                for (Map.Entry<String, Integer> entry : talent.required().entrySet()) {
                    String talentName = entry.getKey();
                    String formattedTN = StringUtils.format(talentName);
                    int reqTalentLevel = entry.getValue();

                    if (talentLevel < reqTalentLevel) {
                        newLore.add("<red>• " + formattedTN + " Level " + reqTalentLevel);
                    } else {
                        newLore.add("<green>• <st>" + formattedTN + " Level " + reqTalentLevel + "</st>");
                    }
                }

                return newLore;
            }

            if (isMaxLevel) {
                newLore.add("<red>You already reached the maximum level!");
                return newLore;
            } else if (hasEnoughPoints) {
                newLore.add("<white>\uE0AE <green>to upgrade");
                return newLore;
            } else {
                newLore.add("<red>You don't have enough talent points!");
            }

            return newLore;
        }

        private String getItemModel(String itemModel, boolean isUnlocked, boolean isMaxLevel, int talentLevel, int maxTalentLevel) {
            if (isMaxLevel || (maxTalentLevel == 0 && talentLevel >= 10)) return itemModel + "_max";
            if (isUnlocked) return itemModel;

            return itemModel + "_locked";
        }

        private void addPaths(boolean isUnlocked, TalentTreeConfig.Talent talent) {
            if (talent.paths() == null || talent.paths().isEmpty()) return;

            for (String path : talent.paths()) {
                String[] parts = path.split(";");
                if (parts.length != 2) continue;

                String itemModel;
                int slot = IntegerUtils.toInt(parts[1]);

                if (isUnlocked) {
                    itemModel = parts[0] + "_red";
                } else {
                    itemModel = parts[0];
                }

                ItemStack item = ComponentService.get(Material.PAPER, "", new ArrayList<>(), itemModel);
                if (item != null) {
                    pane.addItem(new GuiItem(item), Slot.fromIndex(slot));
                }
            }
        }

        private void process(String talentName, int newLevel) {
            SyncUtils.runSync(plugin, () -> {
                SoundUtils.play(player, "cozyvanilla:misc_levelup");
                MessageUtils.sendMessage(player,
                        "Your " + handler.getSkillName() + "'s " + talentName +
                                " <#8BFFA9> is now Level " + newLevel + ".", EnumsLib.MessageType.GREEN);
            });

            cache.invalidate(snap.uuid);

            // Re-open GUI after 1.5s
            plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> {
                open(player);
            }, 30L);
        }

        private boolean hasRequiredTalents(TalentTreeConfig.Talent talent) {
            if ((talent.required() == null || talent.required().isEmpty())
                    && isBasicTalent(talent)) return true;

            if (talent.required() == null) return false;
            for (Map.Entry<String, Integer> entry : talent.required().entrySet()) {
                int level = snap.talentData.get(entry.getKey());
                if (level < entry.getValue()) return false;
            }
            return true;
        }

        private boolean hasRequiredSkillLevel(int requiredLevel) {
            return snap.skillLevel >= requiredLevel;
        }

        private boolean isMaxLevel(int talentLevel, TalentTreeConfig.Talent talent) {
            if (isBasicTalent(talent) && hasTalent19 && talent.maxLevel() != 0) {
                return talentLevel >= talent.maxLevel() + snap.talents.get(snap.masterTalent).value();
            } else if (talent.maxLevel() == 0) {
                return false;
            }
            return talentLevel >= talent.maxLevel();
        }

        private boolean isBasicTalent(TalentTreeConfig.Talent talent) {
            return talent.maxLevel() != 1;
        }

        private List<String> replaceValues(int talentLevel, List<String> lore, TalentTreeConfig.Talent talent) {
            String effect = "{" + talent.effect() + "}";
            if (talentLevel == 0) {
                return StringListUtils.replace(lore, effect, StringDigitUtils.formatDouble(talent.value(), false));
            }
            String value = StringDigitUtils.formatDouble(talent.value() * talentLevel, false);
            return StringListUtils.replace(lore, effect, value);
        }
    }
}
package asia.virtualmc.vLib.core.guis.skills.talent_tree;

import asia.virtualmc.vLib.core.configs.TalentTreeConfig;
import asia.virtualmc.vLib.integration.inventory_framework.IFUtils;
import asia.virtualmc.vLib.services.bukkit.ComponentService;
import asia.virtualmc.vLib.utilities.digit.IntegerUtils;
import asia.virtualmc.vLib.utilities.digit.StringDigitUtils;
import asia.virtualmc.vLib.utilities.text.StringListUtils;
import asia.virtualmc.vLib.utilities.text.StringUtils;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.github.stefvanschie.inventoryframework.pane.util.Slot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TalentTreeGUI {
    private final TalentGUIHandler handler;

    public TalentTreeGUI(TalentGUIHandler handler) {
        this.handler = handler;
    }

    public class TalentTree {
        private final Player player;
        private final UUID uuid;
        private final Map<String, Integer> talentData;
        private final Map<String, TalentTreeConfig.Talent> talents;
        private final int skillLevel;
        private final int talentPoints;
        private final String masterTalent;
        private final boolean hasTalent19;

        private final ChestGui gui = new ChestGui(6, "§f<shift:-48>\uE0E8");
        private final StaticPane pane = new StaticPane(9, 6);

        TalentTree(Player player) {
            this.player = player;
            this.uuid = player.getUniqueId();
            this.talentData = handler.getTalentData(uuid);
            this.talents = handler.getTalents();
            this.skillLevel = handler.getSkillLevel(uuid);
            this.talentPoints = handler.getTalentPoints(uuid);
            this.masterTalent = handler.getSpecialTalentKey();

            this.hasTalent19 = masterTalent != null
                    && talentData.getOrDefault(masterTalent, 0) >= 1;
        }

        public ChestGui getGui() {
            for (Map.Entry<String, TalentTreeConfig.Talent> entry : talents.entrySet()) {
                String talentName = entry.getKey();
                TalentTreeConfig.Talent talent = entry.getValue();

                int talentLevel = talentData.getOrDefault(talentName, 0);
                boolean isUnlocked = hasRequiredTalents(talent) &&
                        hasRequiredSkillLevel(talent.reqLevel());
                boolean isMaxLevel = isMaxLevel(talentLevel, talent);
                boolean hasEnoughPoints = talentPoints >= talent.cost();

                List<String> lore = getLore(talent.lore(), isMaxLevel, isUnlocked,
                        talentLevel, hasEnoughPoints, talent);
                // modify lore
                lore = replaceValues(talentLevel, lore, talent);

                String itemModel = getItemModel(talent.itemModel(), isUnlocked, isMaxLevel, talentLevel, talent.maxLevel());
                ItemStack item = ComponentService.get(talent.material(), talent.displayName(),
                        lore, itemModel);

                // modify quantity
                if (talentLevel > 0 && talentLevel < 64) item.setAmount(talentLevel);

                if (isUnlocked && hasEnoughPoints && !isMaxLevel) {
                    pane.addItem(new GuiItem(item.clone(), event -> {
                        IFUtils.confirmGui(player, result -> {
                            if (result) {
                                handler.subtractTalentPoints(player, talent.cost());
                                handler.incrementTalent(uuid, talentName);
                                handler.upgradeEffects(player, talent.displayName(), talentLevel + 1);
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

            // add book
            pane.addItem(getBook(), Slot.fromIndex(53));

            gui.addPane(pane);
            gui.setOnGlobalClick(event -> event.setCancelled(true));
            return gui;
        }

        private GuiItem getBook() {
            String title = "<gray>Talent Points: <green>" + talentPoints;
            List<String> lore = new ArrayList<>(Arrays.asList("", "<gray>You can obtain Talent Points by doing",
                    "deliveries."));

            return new GuiItem(ComponentService.get(Material.BOOK, title, lore, null));
        }

        private List<String> getLore(List<String> lore, boolean isMaxLevel, boolean isUnlocked,
                                     int talentLevel, boolean hasEnoughPoints,
                                     TalentTreeConfig.Talent talent) {
            List<String> newLore = new ArrayList<>(lore);
            newLore.add("");

            if (!isUnlocked) {
                newLore.add("<gray>Requires:");
                if (skillLevel < talent.reqLevel()) {
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

        private boolean isBasicTalent(TalentTreeConfig.Talent talent) {
            return talent.maxLevel() != 1;
        }

        private boolean hasRequiredTalents(TalentTreeConfig.Talent talent) {
            if ((talent.required() == null || talent.required().isEmpty())
                    && isBasicTalent(talent)) return true;

            if (talent.required() == null) return false;
            for (Map.Entry<String, Integer> entry : talent.required().entrySet()) {
                int level = talentData.get(entry.getKey());
                if (level < entry.getValue()) return false;
            }

            return true;
        }

        private boolean hasRequiredSkillLevel(int requiredLevel) {
            return skillLevel >= requiredLevel;
        }

        private boolean isMaxLevel(int talentLevel, TalentTreeConfig.Talent talent) {
            if (isBasicTalent(talent) && hasTalent19 && talent.maxLevel() != 0) {
                return talentLevel >= talent.maxLevel() + talents.get(masterTalent).value();
            } else if (talent.maxLevel() == 0) {
                return false;
            }

            return talentLevel >= talent.maxLevel();
        }

        private List<String> replaceValues(int talentLevel, List<String> lore, TalentTreeConfig.Talent talent) {
            String effect = "{" + talent.effect() + "}";
            if (talentLevel == 0) {
                return StringListUtils.replace(lore, effect, StringDigitUtils.formatDouble(talent.value(), false));
            }

            String value = StringDigitUtils.formatDouble(talent.value() * talentLevel, false);
            return  StringListUtils.replace(lore, effect, value);
        }
    }

    public ChestGui getGui(Player player) {
        return new TalentTree(player).getGui();
    }
}


package asia.virtualmc.vLib.core.skills.utilities.level_up;

import asia.virtualmc.vLib.utilities.bukkit.FireworkUtils;
import asia.virtualmc.vLib.utilities.bukkit.SoundUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import asia.virtualmc.vLib.utilities.messages.TitleUtils;
import org.bukkit.entity.Player;

import java.util.Set;


public class SkillLevelUpUtils {
    private final SkillLevelUpHandler handler;
    private final Set<Integer> masterLevels;

    public SkillLevelUpUtils(SkillLevelUpHandler handler) {
        this.handler = handler;
        this.masterLevels = handler.getMasterLevels();
    }

    public class LevelUp {
        private final Player player;
        private final int previousLevel;
        private final int newLevel;
        private final int traitPoints;

        LevelUp(Player player, int previousLevel, int newLevel, int traitPoints) {
            this.player = player;
            this.previousLevel = previousLevel;
            this.newLevel = newLevel;
            this.traitPoints = traitPoints;
        }

        public void send() {
            if (player == null || !player.isOnline()) return;
            titleMessage();
            fireworks();
            traitMessage();
        }

        private void titleMessage() {
            TitleUtils.send(player,
                    "<shadow:#C96868:0.5><gradient:#C6FFDD:#FBD786:#f7797d>" + handler.getSkillName(),
                    "<white>ʟᴇᴠᴇʟ " + previousLevel + " <gray>➛ <white>ʟᴇᴠᴇʟ " + newLevel);
        }

        private void fireworks() {
            if (masterLevels.contains(newLevel)) {
                FireworkUtils.spawn(player, 12, 3);
                SoundUtils.play(player, handler.getMasterSound());
            } else {
                FireworkUtils.spawn(player, 5, 5);
                SoundUtils.play(player, handler.getSound());
            }
        }

        private void traitMessage() {
            MessageUtils.sendMessage(player,"You have " + traitPoints +
                    " trait points that you can spend on " + handler.getTraitCommand());
        }
    }

    public LevelUp get(Player player, int prevLevel, int newLevel, int traitPoints) {
        return new LevelUp(player, prevLevel, newLevel, traitPoints);
    }
}

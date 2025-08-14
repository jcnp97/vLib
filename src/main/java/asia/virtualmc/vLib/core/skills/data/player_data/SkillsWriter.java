package asia.virtualmc.vLib.core.skills.data.player_data;

import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import org.bukkit.entity.Player;

public interface SkillsWriter {
    void updateEXP(Player player, EnumsLib.UpdateType type, double value);
    void updateLevel(Player player, EnumsLib.UpdateType type, int value);
    void updateName(Player player, String value);
    void updateXPM(Player player, EnumsLib.UpdateType type, double value);
    void updateBXP(Player player, EnumsLib.UpdateType type, double value);
    void updateTraitPoints(Player player, EnumsLib.UpdateType type, int value);
    void updateTalentPoints(Player player, EnumsLib.UpdateType type, int value);
    void updateLuck(Player player, EnumsLib.UpdateType type, int value);
    void updateWisdom(Player player, EnumsLib.UpdateType type, int value);
    void updateKarma(Player player, EnumsLib.UpdateType type, int value);
    void updateDexterity(Player player, EnumsLib.UpdateType type, int value);
    void updateCharisma(Player player, EnumsLib.UpdateType type, int value);
    void addAllTraits(Player player, int[] value);
}

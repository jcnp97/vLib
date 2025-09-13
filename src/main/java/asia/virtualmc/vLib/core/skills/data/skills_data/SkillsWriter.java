package asia.virtualmc.vLib.core.skills.data.skills_data;

import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface SkillsWriter {
    void updateEXP(Player player, EnumsLib.UpdateType type, double value);
    void updateLevel(UUID uuid, EnumsLib.UpdateType type, int value);
    void updateName(UUID uuid, String value);
    void updateXPM(UUID uuid, EnumsLib.UpdateType type, double value);
    void updateBXP(UUID uuid, EnumsLib.UpdateType type, double value);
    void updateTraitPoints(UUID uuid, EnumsLib.UpdateType type, int value);
    void updateTalentPoints(UUID uuid, EnumsLib.UpdateType type, int value);
    void updateLuck(UUID uuid, EnumsLib.UpdateType type, int value);
    void updateWisdom(UUID uuid, EnumsLib.UpdateType type, int value);
    void updateKarma(UUID uuid, EnumsLib.UpdateType type, int value);
    void updateDexterity(UUID uuid, EnumsLib.UpdateType type, int value);
    void updateCharisma(UUID uuid, EnumsLib.UpdateType type, int value);
    void addAllTraits(UUID uuid, int[] value);
}

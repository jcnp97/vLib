package asia.virtualmc.vLib.core.guis.skills.talent_tree;

import asia.virtualmc.vLib.core.configs.TalentTreeConfig;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public interface TalentGUIHandler {
    String getSkillName();
    String getSpecialTalentKey();
    int getSkillLevel(UUID uuid);
    int getTalentPoints(UUID uuid);
    Map<String, Integer> getTalentData(UUID uuid);
    Map<String, TalentTreeConfig.Talent> getTalents();

    // Processing Methods
    void incrementTalent(UUID uuid, String talentName);
    void subtractTalentPoints(UUID uuid, int amount);
}
package asia.virtualmc.vLib.core.skills.utilities.level_up;

import java.util.Set;

public interface SkillLevelUpHandler {
    String getSkillName();
    String getTraitCommand();
    String getSound();
    String getMasterSound();
    Set<Integer> getMasterLevels();
}
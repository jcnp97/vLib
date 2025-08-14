package asia.virtualmc.vLib.core.skills.data.player_data;

import java.util.UUID;

public interface SkillsReader {
    String getPlayerName(UUID uuid);
    double getEXP(UUID uuid);
    int getLevel(UUID uuid);
    double getBXP(UUID uuid);
    double getXPM(UUID uuid);
    int getLuck(UUID uuid);
    int getTraitPoints(UUID uuid);
    int getTalentPoints(UUID uuid);
    int[] getAllTraits(UUID uuid);
    int getWisdom(UUID uuid);
    int getCharisma(UUID uuid);
    int getKarma(UUID uuid);
    int getDexterity(UUID uuid);
    int getNextEXP(int level);
    double getTraitEffect(String traitName, String effectName);
}

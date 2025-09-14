package asia.virtualmc.vLib.core.skills.utilities.misc;

import java.util.UUID;

public interface EXPNotificationHandler {
    String getSkillName();
    double getCurrentExp(UUID uuid);
    double getNextLevelExp(UUID uuid);
    int getCurrentLevel(UUID uuid);
}
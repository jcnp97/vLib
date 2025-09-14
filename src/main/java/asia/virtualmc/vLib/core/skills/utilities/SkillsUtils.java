package asia.virtualmc.vLib.core.skills.utilities;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.bukkit.FireworkUtils;
import asia.virtualmc.vLib.utilities.bukkit.SoundUtils;
import asia.virtualmc.vLib.utilities.digit.StringDigitUtils;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.messages.ActionBarUtils;
import asia.virtualmc.vLib.utilities.messages.BossbarUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import asia.virtualmc.vLib.utilities.messages.TitleUtils;
import asia.virtualmc.vLib.utilities.paper.AsyncUtils;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkillsUtils {

    /**
     * Generates an EXP progress message for a skill, showing the current level,
     * progress percentage, EXP gained, and hourly EXP rate.
     *
     * @param skillName     The name of the skill.
     * @param currentLevel  The current skill level.
     * @param addedEXP      The amount of EXP gained.
     * @param progress      The progress towards the next level (0.0 to 1.0).
     * @return A formatted progress message string.
     */
    public static String getEXPMessage(String skillName, int currentLevel, double addedEXP, float progress) {
        String percentage = StringDigitUtils.formatDouble(Math.min(100.0, (progress) * 100.0), true) + "%";
        if (currentLevel >= 120) {
            percentage = "0.0%";
        }

        String hourlyExp = StringDigitUtils.formatDouble(addedEXP * 240, true);
        return "<white>" + skillName + " Lv. " + currentLevel + " <gray>(<yellow>" + percentage + "<gray>) | <green>+ "
                + addedEXP + " XP <gray>| <red>" + hourlyExp + " XP/HR";
    }

    /**
     * Builds and displays a boss bar showing the player's skill EXP progress.
     * Runs the calculation asynchronously before displaying it on the main thread.
     *
     * @param player        The player to show the boss bar to.
     * @param skillName     The name of the skill.
     * @param currentEXP    The player's current EXP in the skill.
     * @param currentLevel  The player's current skill level.
     * @param nextLevelEXP  The EXP required for the next level.
     * @param addedEXP      The amount of EXP gained.
     */
    public static void buildEXPBossBar(Player player, String skillName, double currentEXP,
                                       int currentLevel, int nextLevelEXP, double addedEXP) {
        AsyncUtils.runAsyncThenSync(Main.getInstance(),
                () -> {
                    float progress = Math.max(0.0f, Math.min(1.0f, (float) currentEXP / nextLevelEXP));
                    String message = getEXPMessage(skillName, currentLevel, addedEXP, progress);
                    return new Object[] { progress, message };
                },
                result -> {
                    float progress = (float) result[0];
                    String message = (String) result[1];

                    BossBar bossBar = BossbarUtils.get(message, EnumsLib.BossBarColor.GREEN, progress);
                    if (player != null) {
                        BossbarUtils.show(player, bossBar, 5.0);
                    }
                }
        );
    }

    /**
     * Builds and displays an action bar message showing the EXP gained
     * and any bonus EXP for a skill.
     *
     * @param player    The player to show the action bar to.
     * @param skillName The name of the skill.
     * @param addedEXP  The amount of EXP gained.
     * @param bonusEXP  The bonus EXP gained.
     */
    public static void buildEXPActionBar(@NotNull Player player, String skillName, double addedEXP, double bonusEXP) {
        String message = "<green>+" + addedEXP + " " + skillName + " EXP <gray>(<aqua>" + bonusEXP + " Bonus EXP<gray>)";
        ActionBarUtils.send(player, message);
    }
}

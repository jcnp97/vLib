package asia.virtualmc.vLib.core.skills.utilities.misc;

import asia.virtualmc.vLib.utilities.digit.StringDigitUtils;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.messages.ActionBarUtils;
import asia.virtualmc.vLib.utilities.messages.BossbarUtils;
import asia.virtualmc.vLib.utilities.paper.TaskUtils;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EXPNotificationUtils {
    private final EXPNotificationHandler handler;
    private final String skillName;
    private final ScheduledTask task;
    private final Map<UUID, Double> cache = new ConcurrentHashMap<>();

    public EXPNotificationUtils(Plugin plugin, EXPNotificationHandler handler) {
        this.handler = handler;
        this.skillName = handler.getSkillName();
        this.task = TaskUtils.repeating(plugin, this::sendAll, 15);
    }

    public void send(Player player, double exp, double bxp) {
        UUID uuid = player.getUniqueId();
        // Adds player to BossBar Queue
        cache.merge(uuid, exp, Double::sum);

        // Sends an Action Bar immediately
        String message;
        if (bxp > 0) {
            message = "<green>+" + exp + " " + skillName + " EXP <gray>(<aqua>" + bxp + " Bonus EXP<gray>)";
        } else {
            message = "<green>+" + exp + " " + skillName + " EXP";
        }

        ActionBarUtils.send(player, message);
    }

    public ScheduledTask getTask() {
        return task;
    }

    public void end() {
        if (task != null) task.cancel();
    }

    private void sendAll() {
        if (cache.isEmpty()) return;

        cache.forEach((uuid, totalExp) -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null && player.isOnline()) {
                show(player, totalExp);
            }
        });

        cache.clear();
    }

    private void show(Player player, double addedExp) {
        UUID uuid = player.getUniqueId();
        double currentExp = handler.getCurrentExp(uuid);
        int currentLevel = handler.getCurrentLevel(uuid);
        double nextLevelExp = handler.getNextLevelExp(currentLevel);
        float progress = (float) (currentExp / nextLevelExp);

        String message = getMessage(skillName, progress, addedExp, currentLevel);
        BossBar bossBar = BossbarUtils.get(message, EnumsLib.BossBarColor.GREEN, progress);
        BossbarUtils.show(player, bossBar, 5.0);
    }

    private String getMessage(String skillName, float progress, double addedEXP, int currentLevel) {
        String percentage = StringDigitUtils.formatDouble(
                Math.min(100.0, (progress) * 100.0), true) + "%";
        String hourlyExp = StringDigitUtils.formatDouble(addedEXP * 240, true);
        return "<white>" + skillName + " Lv. " + currentLevel + " <gray>(<yellow>" + percentage +
                "<gray>) | <green>+ " + StringDigitUtils.formatDouble(addedEXP, true) + " XP <gray>| <red>" + hourlyExp + " XP/HR";
    }
}

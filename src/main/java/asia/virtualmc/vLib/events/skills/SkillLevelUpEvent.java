package asia.virtualmc.vLib.events.skills;

import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class SkillLevelUpEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final EnumsLib.Skills skill;
    private final int previousLevel;
    private final int newLevel;
    private final int traitPoints;

    public SkillLevelUpEvent(UUID uuid, EnumsLib.Skills skill, int previousLevel, int newLevel, int traitPoints) {
        this.player = Bukkit.getPlayer(uuid);
        this.skill = skill;
        this.previousLevel = previousLevel;
        this.newLevel = newLevel;
        this.traitPoints = traitPoints;
    }

    public Player getPlayer() { return player; }
    public EnumsLib.Skills getSkill() { return skill; }
    public int getPreviousLevel() { return previousLevel; }
    public int getNewLevel() { return newLevel; }
    public int getTraitPoints() { return traitPoints; }

    @Override public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}

package asia.virtualmc.vLib.integration.worldguard.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RegionsLeaveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled=false;

    private final UUID uuid;
    private final Set<ProtectedRegion> regions;
    private final Set<String> regionsNames;

    /**
     * This even is fired whenever one or several regions are left.
     * @param playerUUID The UUID of the player leaving the regions.
     * @param regions Set of WorldGuard's ProtectedRegion regions.
     */
    public RegionsLeaveEvent(UUID playerUUID, @Nullable Set<ProtectedRegion> regions) {
        this.uuid = playerUUID;
        this.regionsNames = new HashSet<>();
        this.regions = new HashSet<>();

        if (regions != null) {
            this.regions.addAll(regions);
            for (ProtectedRegion region : regions) {
                this.regionsNames.add(region.getId());
            }
        }
    }

    @Contract (pure = true)
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public UUID getUUID() {
        return uuid;
    }

    @Nullable
    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    @NotNull
    public Set<ProtectedRegion> getRegions() {
        return regions;
    }

    @NotNull
    public Set<String> getRegionsNames() {
        return regionsNames;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled=cancelled;
    }
}
package asia.virtualmc.vLib.listeners;

import asia.virtualmc.vLib.Main;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

public class FireworkDamageListener implements Listener {

    public FireworkDamageListener(@NotNull Main plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework firework) {
            if (firework.hasMetadata("no_damage")) {
                event.setCancelled(true);
            }
        }
    }
}
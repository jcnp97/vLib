package asia.virtualmc.vLib.listeners;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.paper.AsyncUtils;
import asia.virtualmc.vLib.utilities.player.SkullUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class ServerJoinListener implements Listener {
    private final Main plugin;

    public ServerJoinListener(@NotNull Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        SkullUtils.savePlayerHeadAsync(plugin, event.getPlayer());
    }
}
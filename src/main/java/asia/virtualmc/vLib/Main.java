package asia.virtualmc.vLib;

import com.maximde.hologramlib.HologramLib;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main plugin;
    private static final String name = "vLib";
    private static final String prefix = "[vLib] ";
    private Registry registry;

    @Override
    public void onEnable() {
        plugin = this;
        CommandAPI.onEnable();

        this.registry = new Registry(this);
    }

    @Override
    public void onLoad() {
        HologramLib.onLoad(this);
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .verboseOutput(false)
                .silentLogs(true)
        );
    }

    @Override
    public void onDisable() {
        registry.disable();
    }

    public static Main getInstance() {
        return plugin;
    }

    public static String getPluginName() {
        return name;
    }

    public static String getPrefix() {
        return prefix;
    }
}

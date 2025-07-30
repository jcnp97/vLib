package asia.virtualmc.vLib;

import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private static Main plugin;
    private static final String name = "vLib";
    private static final String prefix = "[vLib] ";

    @Override
    public void onEnable() {
        plugin = this;

    }

    @Override
    public void onDisable() {

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

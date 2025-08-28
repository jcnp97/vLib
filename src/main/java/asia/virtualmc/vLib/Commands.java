package asia.virtualmc.vLib;

import asia.virtualmc.vLib.integration.better_model.BMCommands;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Commands {

    public Commands() {
        enable();
    }

    public void enable() {
        Map<String, Boolean> modules = Registry.getModules();
        List<String> enabledCommands = new ArrayList<>();
        CommandAPICommand command = new CommandAPICommand("vlib");

        if (Boolean.TRUE.equals(modules.get("better_model")) && BMCommands.isEnabled()) {
            command.withSubcommand(BMCommands.changeModel())
                    .withSubcommand(BMCommands.spawnModel())
                    .withSubcommand(BMCommands.playAnimation());

            enabledCommands.add("BetterModel");
        }

        if (!enabledCommands.isEmpty()) {
            command.register();
            ConsoleUtils.info("Registered commands: " + enabledCommands);
        }
    }

    public void disable() {
        CommandAPI.onDisable();
    }
}

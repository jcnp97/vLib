package asia.virtualmc.vLib;

import asia.virtualmc.vLib.integration.better_model.BMCommands;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import dev.jorel.commandapi.CommandAPICommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandManager {

    public CommandManager(Map<String, Boolean> modules) {
        List<String> enabledCommands = new ArrayList<>();
        CommandAPICommand command = new CommandAPICommand("vlib");

        if (Boolean.TRUE.equals(modules.get("better_model"))) {
            command.withSubcommand(BMCommands.changeModel())
                    .withSubcommand(BMCommands.spawnModel());
            enabledCommands.add("BetterModel");
        }

        command.register();
        ConsoleUtils.info("Registered commands: " + enabledCommands);
    }
}

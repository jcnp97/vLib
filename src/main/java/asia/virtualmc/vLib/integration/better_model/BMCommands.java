package asia.virtualmc.vLib.integration.better_model;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.bukkit.EntityUtils;
import asia.virtualmc.vLib.utilities.enums.EnumsLib;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import asia.virtualmc.vLib.utilities.messages.MessageUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BMCommands {
    private static boolean isEnable = false;

    public static void load() {
        Plugin plugin = Main.getInstance();
        if (!plugin.getServer().getPluginManager().isPluginEnabled("BetterModel")) {
            ConsoleUtils.severe("BetterModel not found. Disabling integration hooks..");
            return;
        }

        isEnable = true;
        ConsoleUtils.info("Successfully hooked into: BetterModel");
    }

    public static CommandAPICommand spawnModel() {
        if (!isEnable) {
            ConsoleUtils.severe("Trying to use bettermodel module but BetterModel is not found!");
            return null;
        }

        return new CommandAPICommand("spawn_bm")
                .withPermission("vlib.admin")
                .withArguments(new StringArgument("modelName"))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        String modelName = (String) args.get("modelName");

                        Block targetBlock = player.getTargetBlockExact(10);
                        if (targetBlock == null) {
                            MessageUtils.sendMessage(player, "No block in sight to place the model on.", EnumsLib.MessageType.RED);
                            return;
                        }

                        Location location = targetBlock.getLocation();
                        Entity result = BMEntityUtils.spawn(location, modelName);

                        if (result == null) {
                            MessageUtils.sendMessage(player, "Failed to spawn model. Model '" + modelName + "' does not exist.", EnumsLib.MessageType.RED);
                        } else {
                            MessageUtils.sendMessage(player, "Spawned model: " + modelName, EnumsLib.MessageType.GREEN);
                        }
                    } else {
                        sender.sendMessage("This command can only be used by players.");
                    }
                });
    }

    public static CommandAPICommand changeModel() {
        if (!isEnable) {
            ConsoleUtils.severe("Trying to use bettermodel module but BetterModel is not found!");
            return null;
        }

        return new CommandAPICommand("change_bm")
                .withPermission("vlib.admin")
                .withArguments(new StringArgument("modelName"))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        String modelName = (String) args.get("modelName");

                        Entity target = EntityUtils.getNearest(player, 3, EntityType.ITEM_DISPLAY);
                        if (target == null) {
                            MessageUtils.sendMessage(player, "No valid entity nearby found.", EnumsLib.MessageType.RED);
                            return;
                        }

                        boolean success = BMEntityUtils.change(target, modelName);
                        if (!success) {
                            MessageUtils.sendMessage(player, "Failed to apply model. Model '" + modelName + "' does not exist.", EnumsLib.MessageType.RED);
                        } else {
                            MessageUtils.sendMessage(player, "Changed model to: " + modelName, EnumsLib.MessageType.GREEN);
                        }
                    } else {
                        sender.sendMessage("This command can only be used by players.");
                    }
                });
    }
}

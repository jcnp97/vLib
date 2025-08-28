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

public class BMCommands {
    private static boolean enabled = false;

    public static void load() {
        Main plugin = Main.getInstance();
        if (!plugin.getServer().getPluginManager().isPluginEnabled("BetterModel")) {
            ConsoleUtils.severe("BetterModel not found. Disabling integration hooks..");
            return;
        }

        enabled = true;
        ConsoleUtils.info("Successfully hooked into: BetterModel");
    }

    public static CommandAPICommand spawnModel() {
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

    public static CommandAPICommand playAnimation() {
        return new CommandAPICommand("play_anim")
                .withPermission("vlib.admin")
                .withArguments(new StringArgument("animation_name"))
                .executes((sender, args) -> {
                    if (sender instanceof Player player) {
                        String animName = (String) args.get("animation_name");

                        Entity target = EntityUtils.getNearest(player, 3, EntityType.ITEM_DISPLAY);
                        if (target == null) {
                            MessageUtils.sendMessage(player, "No valid entity nearby found.", EnumsLib.MessageType.RED);
                            return;
                        }

                        boolean success = BMAnimUtils.playOnce(target, animName);
                        if (!success) {
                            MessageUtils.sendMessage(player, "Failed to play animation because Animation '" + animName + "' does not exist.", EnumsLib.MessageType.RED);
                        } else {
                            MessageUtils.sendMessage(player, "Played animation: " + animName, EnumsLib.MessageType.GREEN);
                        }
                    } else {
                        sender.sendMessage("This command can only be used by players.");
                    }
                });
    }

    public static boolean isEnabled() {
        return enabled;
    }
}

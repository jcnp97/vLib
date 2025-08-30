package asia.virtualmc.vLib.integration.craftengine;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.CustomItem;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Optional;

public class CraftEngineUtils {

    public static ItemStack get(String namespace, String itemName) {
        CustomItem<ItemStack> item = CraftEngineItems.byId(Key.of(namespace, itemName));
        if (item != null) {
            return item.buildItemStack();
        }

        return null;
    }

    public static String getItemName(String id) {
        String[] parts = id.split(":");
        if (parts.length > 1) {
            return id.split(":")[1];
        }

        return id;
    }

    public static void give(Player player, String id, int amount) {
        CustomItem<ItemStack> customItem = CraftEngineItems.byId(Key.of(id));
        if (customItem == null || amount <= 0) return;

        try {
            ItemStack item = customItem.buildItemStack();
            PlayerInventory inventory = player.getInventory();
            Location dropLocation = player.getLocation();

            while (amount > 0) {
                int stackSize = Math.min(item.getMaxStackSize(), amount);
                ItemStack stackToGive = item.clone();
                stackToGive.setAmount(stackSize);

                HashMap<Integer, ItemStack> leftover = inventory.addItem(stackToGive);
                if (!leftover.isEmpty()) {
                    for (ItemStack leftoverItem : leftover.values()) {
                        player.getWorld().dropItemNaturally(dropLocation, leftoverItem);
                    }
                }

                amount -= stackSize;
            }

            player.playSound(player, "minecraft:entity.item.pickup", 1, 1);
        } catch (Exception e) {
            ConsoleUtils.severe("An error occurred when trying to give an item to " + player.getName() + ": " + e);
        }
    }

    public static void give(Player player, String namespace, String itemName, int amount) {
        give(player, namespace + ":" + itemName, amount);
    }

    @Nullable
    public static String getId(Block block) {
        return Optional.ofNullable(CraftEngineBlocks.getCustomBlockState(block))
                .map(it -> it.owner().value().id().toString())
                .orElse(null);
    }

    @Nullable
    public static String getId(ItemStack item) {
        return Optional.ofNullable(CraftEngineItems.getCustomItemId(item))
                .map(Key::toString)
                .orElse(null);
    }
}

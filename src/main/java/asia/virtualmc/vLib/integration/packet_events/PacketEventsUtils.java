package asia.virtualmc.vLib.integration.packet_events;

import asia.virtualmc.vLib.Main;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemCustomModelData;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemModel;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.resources.ResourceLocation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketEventsUtils {

    /**
     * Converts a Bukkit ItemStack to a PacketEvents ItemStack.
     *
     * @param item the Bukkit ItemStack
     * @return the PacketEvents ItemStack
     */
    public static ItemStack getItemStack(@NotNull org.bukkit.inventory.ItemStack item) {
        return SpigotConversionUtil.fromBukkitItemStack(item);
    }

    /**
     * Creates a PacketEvents ItemStack with a given material and item model (namespace:key).
     *
     * @param material  the Bukkit Material
     * @param itemModel the string representation of the item model (namespace:key)
     * @return the PacketEvents ItemStack, or null if the material type is not valid
     */
    public static ItemStack getItemStack(@NotNull Material material, @NotNull String itemModel) {
        ItemType itemType = ItemTypes.getByName(material.name().toLowerCase());
        if (itemType == null) return null;

        return ItemStack.builder()
                .type(itemType)
                .component(ComponentTypes.ITEM_MODEL, new ItemModel(new ResourceLocation(itemModel)))
                .build();
    }

    /**
     * Creates a PacketEvents ItemStack with a given material and custom model data.
     *
     * @param material   the Bukkit Material
     * @param modelData  the custom model data value
     * @return the PacketEvents ItemStack, or null if the material type is not valid
     */
    public static ItemStack getItemStack(@NotNull Material material, int modelData) {
        ItemType itemType = ItemTypes.getByName(material.name().toLowerCase());
        if (itemType == null) return null;

        return ItemStack.builder()
                .type(itemType)
                .component(ComponentTypes.CUSTOM_MODEL_DATA_LISTS, new ItemCustomModelData(modelData))
                .build();
    }

    /**
     * Temporarily shows a custom item model animation in the player's main hand.
     * The item will appear as the specified model for the given number of ticks
     * before reverting back to the original item.
     *
     * @param player    the player to show the animation to
     * @param namespace the namespace of the model (e.g., "minecraft", "plugin")
     * @param key       the key of the model
     * @param ticks     how long (in ticks) the animation should last before reverting
     */
    public static void showAnimation(@NotNull Player player, String namespace, String key, int ticks) {
        org.bukkit.inventory.ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack protocolItem = getItemStack(mainHand);
        if (protocolItem == null) return;

        ItemModel model = new ItemModel(new ResourceLocation(namespace, key));
        protocolItem.setComponent(ComponentTypes.ITEM_MODEL, model);

        int selectedSlot = player.getInventory().getHeldItemSlot();
        int packetSlot = 36 + selectedSlot;
        WrapperPlayServerSetSlot fakePacket = new WrapperPlayServerSetSlot(
                0,
                0,
                packetSlot,
                protocolItem
        );

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            // Send animation
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, fakePacket);

            // Stop animation
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                org.bukkit.inventory.ItemStack updatedItem = player.getInventory().getItem(selectedSlot);
                if (updatedItem == null) return;

                ItemStack updated = getItemStack(updatedItem);
                WrapperPlayServerSetSlot revert = new WrapperPlayServerSetSlot(0, 0, packetSlot, updated);
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, revert);
            }, ticks);
        }, 3L);
    }
}

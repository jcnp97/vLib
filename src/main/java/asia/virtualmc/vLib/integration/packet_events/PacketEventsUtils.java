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
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerHeldItemChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketEventsUtils {

    /**
     * Creates an {@link ItemStack} with the specified {@link ItemType} and custom model data.
     *
     * @param itemType  The item type to use for the ItemStack.
     * @param modelData The custom model data to apply.
     * @return A new ItemStack with the given item type and model data.
     */
    public static ItemStack getItemStack(ItemType itemType, int modelData) {
        return ItemStack.builder()
                .type(itemType)
                .component(ComponentTypes.CUSTOM_MODEL_DATA_LISTS, new ItemCustomModelData(modelData))
                .build();
    }

    /**
     * Creates an {@link ItemStack} from a {@link Material} with the specified custom model data.
     * The material name is converted to lowercase to match the {@link ItemType} registry.
     *
     * @param material  The Bukkit material to use.
     * @param modelData The custom model data to apply.
     * @return A new ItemStack with the resolved item type and model data.
     */
    public static ItemStack getItemStack(Material material, int modelData) {
        ItemType itemType = ItemTypes.getByName(material.name().toLowerCase());
        if (itemType == null) {
            return null;
        }

        return ItemStack.builder()
                .type(itemType)
                .component(ComponentTypes.CUSTOM_MODEL_DATA_LISTS, new ItemCustomModelData(modelData))
                .build();
    }

    public static ItemStack getItemStack(@NotNull org.bukkit.inventory.ItemStack item) {
        return SpigotConversionUtil.fromBukkitItemStack(item);
    }

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

package asia.virtualmc.vLib.integration.packet_events;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemCustomModelData;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import org.bukkit.Material;

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
}

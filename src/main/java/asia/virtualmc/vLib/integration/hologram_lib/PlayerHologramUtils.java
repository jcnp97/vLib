package asia.virtualmc.vLib.integration.hologram_lib;

import asia.virtualmc.vLib.integration.packet_events.PacketEventsUtils;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import com.github.f4b6a3.ulid.UlidCreator;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.maximde.hologramlib.__relocated__.me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import com.maximde.hologramlib.hologram.Hologram;
import com.maximde.hologramlib.hologram.HologramManager;
import com.maximde.hologramlib.hologram.ItemHologram;
import com.maximde.hologramlib.hologram.TextHologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PlayerHologramUtils {
    private static HologramManager hologramManager;
    private static final Map<UUID, List<Hologram<?>>> hologramCache = new HashMap<>();
    private static final Map<UUID, Location> locationCache = new HashMap<>();

    @Internal
    public static void load() {
        hologramManager = HologramLibUtils.getHologramManager();
    }

    public static void register(Player player, String text, Material material,
                                String itemModel, float x, float y, float z, Location location) {
        UUID uuid = player.getUniqueId();
        Location lastLocation = locationCache.get(uuid);
        if (lastLocation != null && lastLocation.equals(location)) {
            return;
        }

        String hologramID = UlidCreator.getUlid().toString().toLowerCase();
        String hologramItem = hologramID + "_item";
        String hologramText = hologramID + "_text";

        ItemStack holoItem = PacketEventsUtils.getItemStack(material, itemModel);
        ItemHologram itemHologram = new ItemHologram(hologramItem)
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .addViewer(player)
                .setScale(x * 0.3f, y * 0.3f, z * 0.3f)
                .setBillboard(Display.Billboard.VERTICAL);

        TextHologram textHologram = new TextHologram(hologramText)
                .setMiniMessageText(text)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);

        // Remove old holograms if it exists first
        removeHolograms(uuid);
        locationCache.put(uuid, location.clone());

        Location holoLocation = location.clone().add(0.5, 1.5, 0.5);
        hologramManager.spawn(textHologram, holoLocation);
        hologramManager.spawn(itemHologram, holoLocation.add(0, 0.2, 0));
        hologramCache.put(uuid, new ArrayList<>(Arrays.asList(itemHologram, textHologram)));
    }

    public static void register(Player player, List<String> texts,
                                float x, float y, float z, Location location) {
        UUID uuid = player.getUniqueId();
        Location lastLocation = locationCache.get(uuid);
        if (lastLocation != null && lastLocation.equals(location)) {
            return;
        }

        // Remove old holograms if it exists first
        removeHolograms(uuid);
        locationCache.put(uuid, location.clone());

        double height = 0;
        Location holoLocation = location.clone().add(0.5, 1.5, 0.5);
        for (String text : texts) {
            String hologramID = UlidCreator.getUlid().toString().toLowerCase();
            TextHologram textHologram = new TextHologram(hologramID)
                    .setMiniMessageText(text)
                    .setAlignment(TextDisplay.TextAlignment.CENTER)
                    .addViewer(player)
                    .setScale(x, y, z)
                    .setBillboard(Display.Billboard.VERTICAL);

            hologramManager.spawn(textHologram, holoLocation.clone().add(0, height, 0));
            hologramCache.computeIfAbsent(uuid, k -> new ArrayList<>()).add(textHologram);
            height += 0.2;
        }
    }

    public static void removeHolograms(UUID uuid) {
        List<Hologram<?>> holograms = hologramCache.get(uuid);
        if (holograms != null && !holograms.isEmpty()) {
            for (Hologram<?> hologram : holograms) {
                hologramManager.remove(hologram);
            }
            hologramCache.remove(uuid);
        }

        locationCache.remove(uuid);
    }
}

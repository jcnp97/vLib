package asia.virtualmc.vLib.integration.hologram_lib;

import asia.virtualmc.vLib.integration.packet_events.PacketEventsUtils;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.f4b6a3.ulid.UlidCreator;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.maximde.hologramlib.HologramLib;
import com.maximde.hologramlib.__relocated__.me.tofaa.entitylib.meta.display.ItemDisplayMeta;
import com.maximde.hologramlib.hologram.*;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HologramUtils {
    private static final long tempHoloDuration = 8;
    private static final Set<UUID> hologramIDs = new HashSet<>();
    private static HologramManager hologramManager;
    private static final Cache<UUID, ScheduledTask> tempHoloCache = Caffeine.newBuilder()
            .expireAfterWrite(tempHoloDuration, TimeUnit.SECONDS)
            .build();

    public static void load() {
        HologramLib.getManager().ifPresentOrElse(
                manager -> hologramManager = manager,
                () -> ConsoleUtils.severe("HologramLib not found. Disabling integration hooks..")
        );

        if (hologramManager != null) {
            ConsoleUtils.info("Successfully hooked into: HologramLib");
        }
    }

    /**
     * Spawns a permanent item hologram visible only to a specific player.
     *
     * @param material   the {@link Material} for the item
     * @param modelData  the custom model data to apply
     * @param player     the player who can see the hologram
     * @param x          X scale of the item
     * @param y          Y scale of the item
     * @param z          Z scale of the item
     * @param location   the spawn location of the hologram
     * @return a generated {@link UUID} representing the hologram ID
     */
    public static UUID item(Material material, int modelData, Player player, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(material, modelData);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString())
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);
        hologramIDs.add(hologramID);

        return hologramID;
    }

    /**
     * Spawns a temporary item hologram visible only to a specific player.
     * The hologram despawns after 30 ticks (1.5 seconds).
     *
     * @param plugin     the plugin instance used for scheduling
     * @param material   the {@link Material} for the item
     * @param modelData  the custom model data
     * @param player     the player who can see the hologram
     * @param x          X scale of the item
     * @param y          Y scale of the item
     * @param z          Z scale of the item
     * @param location   the spawn location of the hologram
     */
    public static void item(Plugin plugin, Material material, int modelData, Player player, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(material, modelData);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString())
                .setItem(holoItem)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);

        new BukkitRunnable() {
            @Override
            public void run() {
                hologramManager.remove(itemHologram);
            }
        }.runTaskLater(plugin, 30L);
    }

    /**
     * Spawns a permanent text hologram visible only to a specific player.
     *
     * @param text       the MiniMessage-formatted text to display
     * @param player     the player who can see the hologram
     * @param x          X scale of the text
     * @param y          Y scale of the text
     * @param z          Z scale of the text
     * @param location   the spawn location of the hologram
     * @return a generated {@link UUID} representing the hologram ID
     */
    public static UUID text(String text, Player player, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        TextHologram textHologram = new TextHologram(hologramID.toString())
                .setMiniMessageText(text)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(textHologram, location);
        hologramIDs.add(hologramID);

        return hologramID;
    }

    /**
     * Spawns a temporary text hologram that automatically disappears after a fixed duration.
     * Uses the player UUID as the hologram ID to avoid duplicates.
     *
     * @param plugin     the plugin instance for scheduling
     * @param text       the MiniMessage-formatted text
     * @param player     the player who will view the hologram
     * @param x          X scale
     * @param y          Y scale
     * @param z          Z scale
     * @param location   the spawn location of the hologram
     */
    public static void temporaryText(Plugin plugin, String text, Player player, float x, float y, float z, Location location) {
        UUID hologramID = player.getUniqueId();
        String idString = hologramID.toString();
        ScheduledTask oldTask = tempHoloCache.getIfPresent(hologramID);

        if (oldTask != null) {
            oldTask.cancel();
        }

        hologramManager.getHologram(idString).ifPresent(h -> {
            hologramManager.remove(idString);
            hologramIDs.remove(hologramID);
        });

        TextHologram textHologram = new TextHologram(idString)
                .setMiniMessageText(text)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(textHologram, location);
        hologramIDs.add(hologramID);

        ScheduledTask delayedTask = plugin.getServer()
                .getGlobalRegionScheduler()
                .runDelayed(plugin, task -> {
                    hologramManager.remove(idString);
                    hologramIDs.remove(hologramID);
                }, tempHoloDuration * 20L);
        tempHoloCache.put(hologramID, delayedTask);
    }

    /**
     * Modifies the text of an existing text hologram, if it's tracked in the internal hologram ID set.
     *
     * @param hologramID the UUID of the hologram to modify
     * @param newText    the new text to display (MiniMessage format)
     */
    public static void modifyText(UUID hologramID, String newText) {
        if (!hologramIDs.contains(hologramID)) return;

        TextHologram textHologram = ((TextHologram) hologramManager.getHologram(hologramID.toString()).get());
        textHologram.setText(newText).update();
    }

    /**
     * Creates a composite hologram that includes both an item and a text hologram stacked together.
     * The two holograms are spawned with linked ULID-based IDs and are visible only to the specified player.
     *
     * @param text       the MiniMessage-formatted text
     * @param player     the player who can view the holograms
     * @param material   the item material for the item hologram
     * @param modelData  the custom model data
     * @param x          X scale of the text hologram
     * @param y          Y scale of the text hologram
     * @param z          Z scale of the text hologram
     * @param location   the base spawn location
     * @return a string ULID representing the base ID used for both the item and text holograms
     */
    public static String composite(String text, Player player, Material material, int modelData,
                                         float x, float y, float z, Location location) {

        String hologramID = UlidCreator.getUlid().toString().toLowerCase();
        String hologramItem = hologramID + "_item";
        String hologramText = hologramID + "_text";

        ItemStack holoItem = PacketEventsUtils.getItemStack(material, modelData);

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

        hologramManager.spawn(itemHologram, location.clone());
        hologramManager.spawn(textHologram, location.clone().add(0, 0.1, 0));

        return hologramID;
    }

    /**
     * Removes both parts of a composite hologram (item and text) by its shared ULID-based ID.
     *
     * @param hologramID the base ID of the composite hologram to remove
     */
    public static void removeComposite(String hologramID) {
        String hologramItem = hologramID + "_item";
        String hologramText = hologramID + "_text";
        hologramManager.remove(hologramItem);
        hologramManager.remove(hologramText);
    }

    /**
     * Removes a tracked hologram by its UUID and unregisters it from the internal cache.
     *
     * @param hologramID the UUID of the hologram to remove
     */
    public static void remove(UUID hologramID) {
        if (hologramIDs.contains(hologramID)) {
            hologramManager.remove(hologramID.toString());
            hologramIDs.remove(hologramID);
        }
    }

    /**
     * Spawns a globally visible item hologram (no viewer restriction).
     *
     * @param material   the {@link Material} of the item
     * @param modelData  the custom model data
     * @param x          X scale
     * @param y          Y scale
     * @param z          Z scale
     * @param location   the spawn location
     * @return the UUID representing the spawned hologram
     */
    public static UUID item(Material material, int modelData, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(material, modelData);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString(), RenderMode.ALL)
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);
        hologramIDs.add(hologramID);

        return hologramID;
    }

    /**
     * Spawns a globally visible text hologram (no viewer restriction).
     *
     * @param text       the MiniMessage-formatted text
     * @param x          X scale
     * @param y          Y scale
     * @param z          Z scale
     * @param location   the spawn location
     * @return the UUID representing the spawned text hologram
     */
    public static UUID text(String text, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        TextHologram textHologram = new TextHologram(hologramID.toString(), RenderMode.ALL)
                .setMiniMessageText(text)
                .setViewRange(10.0)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(textHologram, location);
        hologramIDs.add(hologramID);

        return hologramID;
    }

    /**
     * Adds a viewer to an existing hologram by UUID.
     *
     * @param player     the player to add as a viewer
     * @param hologramID the UUID of the hologram
     */
    public static void addViewer(Player player, UUID hologramID) {
        Optional<Hologram<?>> hologram = hologramManager.getHologram(hologramID.toString());
        hologram.ifPresent(value -> value.addViewer(player));
    }

    /**
     * Removes a viewer from a hologram by UUID.
     *
     * @param player     the player to remove from viewership
     * @param hologramID the UUID of the hologram
     */
    public static void removeViewer(Player player, UUID hologramID) {
        Optional<Hologram<?>> hologram = hologramManager.getHologram(hologramID.toString());
        hologram.ifPresent(value -> value.removeViewer(player));
    }

    /**
     * INTERNAL USE ONLY – Called by vLib on plugin shutdown to clear all HologramLib entities.
     * Do NOT call this method from other plugins.
     */
    @Internal
    public static void clearAll() {
        hologramManager.removeAll();
        hologramIDs.clear();
    }
}
package asia.virtualmc.vLib.integration.hologram_lib;

import asia.virtualmc.vLib.integration.packet_events.PacketEventsUtils;
import asia.virtualmc.vLib.utilities.annotations.Internal;
import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
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

import java.awt.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HologramLibUtils {
    private static HologramManager hologramManager;
    private static final Cache<UUID, ScheduledTask> temporaryCache = Caffeine.newBuilder()
            .expireAfterWrite(3, TimeUnit.SECONDS)
            .build();

    /**
     * Loads and initializes the HologramLib manager if available.
     * Logs the status to console.
     */
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
     * Spawns an item hologram visible only to a specific player.
     *
     * @param player the player to show the hologram to
     * @param material the item material
     * @param itemModel the custom model ID string
     * @param x scale on the X axis
     * @param y scale on the Y axis
     * @param z scale on the Z axis
     * @param location the location to spawn the hologram
     * @return the unique hologram ID
     */
    public static UUID item(Player player, Material material, String itemModel, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(material, itemModel);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString())
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);

        return hologramID;
    }

    /**
     * Spawns an item hologram visible only to a specific player.
     *
     * @param player the player to show the hologram to
     * @param item the Bukkit ItemStack
     * @param x scale on the X axis
     * @param y scale on the Y axis
     * @param z scale on the Z axis
     * @param location the location to spawn the hologram
     * @return the unique hologram ID
     */
    public static UUID item(Player player, org.bukkit.inventory.ItemStack item, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(item);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString())
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);

        return hologramID;
    }

    /**
     * Spawns a text hologram visible only to a specific player.
     *
     * @param player the player to show the hologram to
     * @param text the text content (MiniMessage format supported)
     * @param x scale on the X axis
     * @param y scale on the Y axis
     * @param z scale on the Z axis
     * @param location the location to spawn the hologram
     * @return the unique hologram ID
     */
    public static UUID text(Player player, String text, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        TextHologram textHologram = new TextHologram(hologramID.toString())
                .setMiniMessageText(text)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(textHologram, location);

        return hologramID;
    }

    /**
     * Spawns a temporary text hologram visible only to a player.
     * Automatically removes itself after 3 seconds.
     *
     * @param player the player to show the hologram to
     * @param plugin the plugin instance scheduling the removal
     * @param text the text content (MiniMessage format supported)
     * @param x scale on the X axis
     * @param y scale on the Y axis
     * @param z scale on the Z axis
     * @param location the location to spawn the hologram
     */
    public static void temporaryText(Player player, Plugin plugin, String text, float x, float y, float z, Location location) {
        UUID hologramID = player.getUniqueId();
        String idString = hologramID.toString();
        ScheduledTask oldTask = temporaryCache.getIfPresent(hologramID);

        if (oldTask != null) {
            oldTask.cancel();
        }

        hologramManager.getHologram(idString).ifPresent(h -> {
            hologramManager.remove(idString);
        });

        TextHologram textHologram = new TextHologram(idString)
                .setMiniMessageText(text)
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .addViewer(player)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(textHologram, location);

        ScheduledTask delayedTask = plugin.getServer()
                .getGlobalRegionScheduler()
                .runDelayed(plugin, task -> {
                    hologramManager.remove(idString);
                }, 3 * 20L);
        temporaryCache.put(hologramID, delayedTask);
    }

    /**
     * Spawns an item hologram visible to all players.
     *
     * @param material the item material
     * @param itemModel the custom model ID string
     * @param x scale on the X axis
     * @param y scale on the Y axis
     * @param z scale on the Z axis
     * @param location the location to spawn the hologram
     * @return the unique hologram ID
     */
    public static UUID item(Material material, String itemModel, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(material, itemModel);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString(), RenderMode.ALL)
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);

        return hologramID;
    }

    /**
     * Spawns an item hologram visible to all players.
     *
     * @param item the Bukkit ItemStack
     * @param x scale on the X axis
     * @param y scale on the Y axis
     * @param z scale on the Z axis
     * @param location the location to spawn the hologram
     * @return the unique hologram ID
     */
    public static UUID item(org.bukkit.inventory.ItemStack item, float x, float y, float z, Location location) {
        UUID hologramID = UUID.randomUUID();
        ItemStack holoItem = PacketEventsUtils.getItemStack(item);

        ItemHologram itemHologram = new ItemHologram(hologramID.toString(), RenderMode.ALL)
                .setItem(holoItem)
                .setGlowing(true)
                .setGlowColor(Color.white)
                .setDisplayType(ItemDisplayMeta.DisplayType.FIXED)
                .setScale(x, y, z)
                .setBillboard(Display.Billboard.VERTICAL);
        hologramManager.spawn(itemHologram, location);

        return hologramID;
    }

    /**
     * Spawns a text hologram visible to all players.
     *
     * @param text the text content (MiniMessage format supported)
     * @param x scale on the X axis
     * @param y scale on the Y axis
     * @param z scale on the Z axis
     * @param location the location to spawn the hologram
     * @return the unique hologram ID
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

        return hologramID;
    }

    /**
     * Updates the text of an existing text hologram.
     *
     * @param hologramID the hologram's unique ID
     * @param newText the new text content
     */
    public static void editText(UUID hologramID, String newText) {
        TextHologram textHologram = ((TextHologram) hologramManager.getHologram(hologramID.toString()).get());
        textHologram.setText(newText).update();
    }

    /**
     * Removes a hologram by its unique ID.
     *
     * @param hologramID the hologram's unique ID
     */
    public static void remove(UUID hologramID) {
        hologramManager.remove(hologramID.toString());
    }

    /**
     * Adds a player as a viewer of a hologram.
     *
     * @param player the player to add
     * @param hologramID the hologram's unique ID
     */
    public static void addViewer(Player player, UUID hologramID) {
        Optional<Hologram<?>> hologram = hologramManager.getHologram(hologramID.toString());
        hologram.ifPresent(value -> value.addViewer(player));
    }

    /**
     * Removes a player as a viewer of a hologram.
     *
     * @param player the player to remove
     * @param hologramID the hologram's unique ID
     */
    public static void removeViewer(Player player, UUID hologramID) {
        Optional<Hologram<?>> hologram = hologramManager.getHologram(hologramID.toString());
        hologram.ifPresent(value -> value.removeViewer(player));
    }

    /**
     * Removes all holograms currently managed.
     * <p><b>Warning:</b> This method is annotated with @Internal and is only intended for library use, not public use.</p>
     */
    @Internal
    public static void clearAll() {
        hologramManager.removeAll();
    }

    /**
     * Gets the current HologramManager instance.
     *
     * @return the hologram manager
     */
    public static HologramManager getHologramManager() { return hologramManager; }
}
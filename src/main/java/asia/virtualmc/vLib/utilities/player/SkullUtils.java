package asia.virtualmc.vLib.utilities.player;

import asia.virtualmc.vLib.integration.skinsrestorer.SkinsRestorerUtils;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class SkullUtils {
    private static final Cache<UUID, ItemStack> playerHeads = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    /**
     * Async-safe method: resolves a player's head and caches it.
     * Splits async I/O work from sync Bukkit API calls.
     */
    public static void savePlayerHeadAsync(Plugin plugin, Player player) {
        UUID uuid = player.getUniqueId();

        if (playerHeads.getIfPresent(uuid) != null) {
            return;
        }

        Bukkit.getAsyncScheduler().runNow(plugin, task -> {
            String skinUrl = null;

            Optional<SkinProperty> propOpt = SkinsRestorerUtils.getSkinProperty(uuid);
            if (propOpt.isEmpty()) {
                try {
                    propOpt = SkinsRestorerUtils.getSkinProperty(uuid, player.getName());
                } catch (DataRequestException ignored) {}
            }

            if (propOpt.isPresent()) {
                skinUrl = extractSkinUrl(propOpt.get());
            }

            final String finalSkinUrl = skinUrl;
            final String name = player.getName();

            Bukkit.getScheduler().runTask(plugin, () -> {
                ItemStack head = createHeadFromUrl(uuid, name, finalSkinUrl);
                playerHeads.put(uuid, head);
            });
        });
    }

    /**
     * Returns a cached player head, or a plain PLAYER_HEAD if not cached.
     */
    public static ItemStack get(UUID uuid) {
        ItemStack head = playerHeads.getIfPresent(uuid);
        if (head == null) {
            return new ItemStack(Material.PLAYER_HEAD);
        }
        return head.clone();
    }

    // ---------------- Private helpers ----------------

    private static String extractSkinUrl(@NotNull SkinProperty property) {
        try {
            String json = new String(Base64.getDecoder().decode(property.getValue()), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            return root.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        } catch (Throwable t) {
            return null;
        }
    }

    private static @NotNull ItemStack createHeadFromUrl(@NotNull UUID uuid, @NotNull String name, String skinUrl) {
        final ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        head.editMeta(SkullMeta.class, meta -> {
            PlayerProfile profile = Bukkit.createProfile(uuid, name);
            PlayerTextures textures = profile.getTextures();
            if (skinUrl != null) {
                try {
                    textures.setSkin(new URL(skinUrl));
                } catch (MalformedURLException ignored) {}
                profile.setTextures(textures);
            }
            meta.setPlayerProfile(profile);
        });
        return head;
    }
}
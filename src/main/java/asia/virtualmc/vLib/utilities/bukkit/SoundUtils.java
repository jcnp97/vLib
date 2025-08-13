package asia.virtualmc.vLib.utilities.bukkit;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class SoundUtils {
    private static final Map<String, Sound> soundCache = new HashMap<>();

    /**
     * Plays a sound to the specified player using the given sound name.
     * The sound is played with default volume and pitch (1.0f).
     * The sound is cached to avoid rebuilding it on future calls.
     *
     * @param player the player to play the sound to
     * @param name   the namespaced sound identifier (e.g., "minecraft:entity.villager.ambient")
     */
    public static void play(Player player, String name) {
        if (player == null || !player.isOnline()) return;

        if (soundCache.containsKey(name)) {
            player.playSound(soundCache.get(name));
        } else {
            String[] parts = name.split(":", 2);
            String namespace = parts.length > 1 ? parts[0] : "minecraft";
            String key = parts.length > 1 ? parts[1] : parts[0];

            Sound sound = Sound.sound()
                    .type(Key.key(namespace, key))
                    .source(Sound.Source.PLAYER)
                    .volume(1.0f)
                    .pitch(1.0f)
                    .build();

            player.playSound(sound);
            soundCache.put(name, sound);
        }
    }

    /**
     * Plays a sound to the specified player using the given sound name, volume, and pitch.
     * The sound is cached for reuse unless already stored.
     *
     * @param player the player to play the sound to
     * @param name   the namespaced sound identifier (e.g., "minecraft:block.note_block.pling")
     * @param volume the volume of the sound
     * @param pitch  the pitch of the sound
     */
    public static void play(Player player, String name, float volume, float pitch) {
        if (player == null || !player.isOnline()) return;

        if (soundCache.containsKey(name)) {
            player.playSound(soundCache.get(name));
        } else {
            String[] parts = name.split(":", 2);
            String namespace = parts.length > 1 ? parts[0] : "minecraft";
            String key = parts.length > 1 ? parts[1] : parts[0];

            Sound sound = Sound.sound()
                    .type(Key.key(namespace, key))
                    .source(Sound.Source.PLAYER)
                    .volume(volume)
                    .pitch(pitch)
                    .build();

            player.playSound(sound);
            soundCache.put(name, sound);
        }
    }

    /**
     * Stops the specified sound for the given player if it was previously played and cached.
     *
     * @param player the player to stop the sound for
     * @param name   the namespaced sound identifier used to play the sound
     */
    public static void stop(Player player, String name) {
        if (player == null || !player.isOnline()) return;

        if (soundCache.containsKey(name)) {
            player.stopSound(soundCache.get(name).asStop());
        }
    }

    /**
     * Gets a Sound object from the given name, volume, and pitch.
     * Returns null if the name is invalid or cannot be parsed into a valid sound key.
     *
     * @param name   the namespaced sound identifier (e.g., "minecraft:block.note_block.pling")
     * @param volume the volume of the sound
     * @param pitch  the pitch of the sound
     * @return the built Sound object, or null if invalid
     */
    public static Sound get(String name, float volume, float pitch) {
        if (name == null || name.isEmpty()) return null;

        try {
            String[] parts = name.split(":", 2);
            String namespace = parts.length > 1 ? parts[0] : "minecraft";
            String key = parts.length > 1 ? parts[1] : parts[0];

            return Sound.sound()
                    .type(Key.key(namespace, key))
                    .source(Sound.Source.PLAYER)
                    .volume(volume)
                    .pitch(pitch)
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Plays the given Sound object to the specified player.
     *
     * @param player the player to play the sound to
     * @param sound  the Sound object to play
     */
    public static void play(Player player, Sound sound) {
        if (player == null || !player.isOnline() || sound == null) return;
        player.playSound(sound);
    }
}

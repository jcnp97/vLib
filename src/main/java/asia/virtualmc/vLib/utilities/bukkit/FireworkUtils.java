package asia.virtualmc.vLib.utilities.bukkit;

import asia.virtualmc.vLib.Main;
import asia.virtualmc.vLib.utilities.paper.TaskUtils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FireworkUtils {
    private static Main plugin = null;
    private static final List<Color> colorList = new ArrayList<>(Arrays.asList(
            Color.AQUA, Color.LIME, Color.YELLOW, Color.BLUE, Color.BLACK, Color.FUCHSIA,
            Color.GRAY, Color.GREEN, Color.MAROON, Color.NAVY, Color.OLIVE, Color.ORANGE,
            Color.PURPLE, Color.RED, Color.SILVER, Color.TEAL, Color.WHITE));

    /**
     * Spawns a single firework at the specified location with randomized colors.
     * The firework has no damage and is set to type BALL with flicker and trail effects.
     *
     * @param location the location where the firework should be spawned
     */
    public static void spawn(Location location) {
        if (plugin == null) {
            plugin = Main.getInstance();
        }

        World world = location.getWorld();
        Firework firework = world.spawn(location, Firework.class);
        firework.setMetadata("nodamage", new FixedMetadataValue(plugin, true));
        FireworkMeta meta = firework.getFireworkMeta();
        Color[] colors = getColors();

        FireworkEffect effect = FireworkEffect.builder()
                .withColor(colors[0], colors[1])
                .withFade(colors[2])
                .with(FireworkEffect.Type.BALL)
                .trail(true)
                .flicker(true)
                .build();
        meta.setPower(0);
        meta.addEffect(effect);
        firework.setFireworkMeta(meta);
    }

    /**
     * Returns an array of 3 randomly selected colors from the predefined color list.
     * These colors are used for the firework effect's main and fade colors.
     *
     * @return an array of 3 {@link Color} objects
     */
    public static Color[] getColors() {
        Color[] colors = new Color[3];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorList.get(ThreadLocalRandom.current()
                    .nextInt(colorList.size()));
        }

        return colors;
    }

    /**
     * Spawns multiple fireworks at a player's location with a set interval.
     * The fireworks are spawned using a repeating task until the specified count is reached.
     *
     * @param player   the player whose location will be used as the spawn point
     * @param count    the number of fireworks to spawn
     * @param interval the delay between each firework spawn, in ticks
     */
    public static void spawn(@NotNull Player player, int count, long interval) {
        if (plugin == null) {
            plugin = Main.getInstance();
        }

        Location location = player.getLocation().clone();
        TaskUtils.repeating(plugin, () -> spawn(location), interval, count);
    }
}

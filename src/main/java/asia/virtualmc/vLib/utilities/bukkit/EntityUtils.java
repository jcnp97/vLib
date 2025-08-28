package asia.virtualmc.vLib.utilities.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class EntityUtils {

    /**
     * Retrieves the first nearest entity of a specified type within a given distance from the player.
     * Returns {@code null} if no matching entity is found.
     *
     * @param player   the player to scan from
     * @param distance the maximum distance to search
     * @param type     the type of entity to search for
     * @return the nearest {@link Entity} of the given type, or {@code null} if none found
     */
    public static Entity getNearest(Player player, double distance, EntityType type) {
        if (player == null || type == null) return null;

        Location origin = player.getLocation();
        Entity nearest = null;
        double nearestDistanceSq = distance * distance;

        for (Entity entity : player.getNearbyEntities(distance, distance, distance)) {
            if (entity.getType() != type) continue;

            double distSq = entity.getLocation().distanceSquared(origin);
            if (distSq < nearestDistanceSq) {
                nearest = entity;
                nearestDistanceSq = distSq;
            }
        }

        return nearest;
    }

    /**
     * Stores an integer value in the entity's PersistentDataContainer.
     *
     * @param entity the entity to attach data to
     * @param key    the key under which to store the integer
     * @param value  the integer value to store
     */
    public static void setInt(Entity entity, NamespacedKey key, int value) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.INTEGER, value);
    }

    /**
     * Reads an integer value from the entity's PersistentDataContainer.
     *
     * @param entity the entity to read data from
     * @param key    the key under which the integer was stored
     * @return the stored integer, or 0 if none was found
     */
    public static int getInt(Entity entity, NamespacedKey key) {
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        if (pdc.has(key, PersistentDataType.INTEGER)) {
            Integer stored = pdc.get(key, PersistentDataType.INTEGER);
            return (stored != null) ? stored : 0;
        }
        return 0;
    }

    /**
     * Retrieves the unique UUID of the given entity.
     *
     * @param entity the entity to extract the UUID from
     * @return the UUID representing the entity's unique identifier
     */
    public static UUID getUUID(@NotNull Entity entity) {
        return entity.getUniqueId();
    }

    /**
     * Retrieves the entity associated with the specified UUID.
     *
     * @param uuid the UUID of the entity to retrieve
     * @return the {@link Entity} with the given UUID, or {@code null} if not found or unloaded
     */
    public static Entity getEntity(UUID uuid) {
        if (uuid == null) return null;
        return Bukkit.getEntity(uuid);
    }

    public static String serialize(Entity entity) {
        if (entity == null) return null;
        return entity.getUniqueId().toString();
    }

    public static Entity deserialize(String entityId) {
        if (entityId == null) return null;
        return getEntity(UUID.fromString(entityId));
    }
}

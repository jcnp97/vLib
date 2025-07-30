//package asia.virtualmc.vLib.utilities.bukkit;
//
//import org.bukkit.Bukkit;
//import org.bukkit.Location;
//import org.bukkit.entity.Entity;
//import org.bukkit.entity.EntityType;
//import org.bukkit.entity.Player;
//import org.bukkit.util.Vector;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.UUID;
//
//public class EntityUtils {
//
//    public static Entity getTarget(Player player, double maxDistance, EntityType targetType) {
//        for (Entity entity : player.getNearbyEntities(maxDistance, maxDistance, maxDistance)) {
//            if (entity.getType() != targetType) continue;
//            if (!player.hasLineOfSight(entity)) continue;
//
//            Location eye = player.getEyeLocation();
//            Location to = entity.getLocation().add(0, entity.getHeight() / 2, 0);
//
//            Vector directionToEntity = to.toVector().subtract(eye.toVector()).normalize();
//            double dot = eye.getDirection().normalize().dot(directionToEntity);
//
//            if (dot > 0.98) {
//                return entity;
//            }
//        }
//
//        return null;
//    }
//
//    public static UUID getUUID(@NotNull Entity entity) {
//        return entity.getUniqueId();
//    }
//
//    public static Entity getEntity(@NotNull UUID uuid) {
//        return Bukkit.getEntity(uuid);
//    }
//}

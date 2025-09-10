package asia.virtualmc.vLib.utilities.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationUtils {

    /**
     * Serializes a block Location into a comma-delimited String: world,x,y,z
     *
     * @param location the Location to serialize (block coordinates only)
     * @return comma-delimited representation
     * @throws IllegalArgumentException if location or its world is null
     */
    public static String serializeBlock(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("Location world cannot be null");
        }

        return String.join(",",
                world.getName(),
                Integer.toString(location.getBlockX()),
                Integer.toString(location.getBlockY()),
                Integer.toString(location.getBlockZ())
        );
    }

    /**
     * Deserializes a comma-delimited block Location String back into a Location.
     * Expects format: world,x,y,z
     *
     * @param location the serialized Location
     * @return reconstructed Location with yaw=0, pitch=0
     * @throws IllegalArgumentException if data is malformed or world not found
     */
    public static Location deserializeBlock(String location) {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Location data cannot be null or empty");
        }
        String[] parts = location.split(",");
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid location format, expected 4 parts: world,x,y,z");
        }

        String worldName = parts[0];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World '" + worldName + "' not found");
        }

        int x, y, z;
        try {
            x = Integer.parseInt(parts[1]);
            y = Integer.parseInt(parts[2]);
            z = Integer.parseInt(parts[3]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be valid integers", e);
        }

        return new Location(world, x, y, z);
    }

    /**
     * Serializes a Location into a comma-delimited String: world,x,y,z,yaw,pitch
     *
     * @param location the Location to serialize
     * @return comma-delimited representation with yaw & pitch
     * @throws IllegalArgumentException if location or its world is null
     */
    public static String serializePos(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        World world = location.getWorld();
        if (world == null) {
            throw new IllegalArgumentException("Location world cannot be null");
        }

        return String.join(",",
                world.getName(),
                Double.toString(location.getX()),
                Double.toString(location.getY()),
                Double.toString(location.getZ()),
                Float.toString(location.getYaw()),
                Float.toString(location.getPitch())
        );
    }

    /**
     * Deserializes a comma-delimited String back into a Location.
     * Expects format: world,x,y,z,yaw,pitch
     *
     * @param location the serialized Location
     * @return reconstructed Location
     * @throws IllegalArgumentException if data is malformed or world not found
     */
    public static Location deserializePos(String location) {
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Location data cannot be null or empty");
        }
        String[] parts = location.split(",");
        if (parts.length != 6) {
            throw new IllegalArgumentException("Invalid location format, expected 6 parts: world,x,y,z,yaw,pitch");
        }

        String worldName = parts[0];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            throw new IllegalArgumentException("World '" + worldName + "' not found");
        }

        double x, y, z;
        float yaw, pitch;
        try {
            x = Double.parseDouble(parts[1]);
            y = Double.parseDouble(parts[2]);
            z = Double.parseDouble(parts[3]);
            yaw = Float.parseFloat(parts[4]);
            pitch = Float.parseFloat(parts[5]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be valid numbers", e);
        }

        return new Location(world, x, y, z, yaw, pitch);
    }
}

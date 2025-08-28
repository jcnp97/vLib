package asia.virtualmc.vLib.utilities.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.UUID;

public class ChunkUtils {

    public record ChunkData(
            UUID worldId,
            int chunkX,
            int chunkZ
    ) {}

    /**
     * Converts a {@link Chunk} into a {@link ChunkData} record containing world UUID, X, and Z.
     *
     * @param chunk the chunk to convert, must not be null
     * @return a {@link ChunkData} representing the chunk
     * @throws IllegalArgumentException if chunk is null
     */
    public static ChunkData get(Chunk chunk) {
        if (chunk == null) {
            throw new IllegalArgumentException("Chunk cannot be null");
        }
        return new ChunkData(chunk.getWorld().getUID(), chunk.getX(), chunk.getZ());
    }

    /**
     * Checks whether the given {@link ChunkData} is currently loaded in its world.
     *
     * @param data the chunk data to check
     * @return true if the world exists and the chunk is loaded, false otherwise
     */
    public static boolean isLoaded(ChunkData data) {
        if (data == null) return false;
        World world = Bukkit.getWorld(data.worldId);
        return world != null && world.isChunkLoaded(data.chunkX, data.chunkZ);
    }

    /**
     * Serializes a {@link ChunkData} into a comma-delimited string: worldUUID,x,z.
     *
     * @param data the chunk data to serialize, must not be null
     * @return serialized string representation of the chunk
     * @throws IllegalArgumentException if data is null
     */
    public static String serialize(ChunkData data) {
        if (data == null) {
            throw new IllegalArgumentException("ChunkData cannot be null");
        }
        return String.join(",",
                data.worldId.toString(),
                Integer.toString(data.chunkX),
                Integer.toString(data.chunkZ)
        );
    }

    /**
     * Deserializes a string back into a {@link ChunkData}.
     * Expected format: worldUUID,x,z.
     *
     * @param chunk the serialized string
     * @return a {@link ChunkData} instance parsed from the string
     * @throws IllegalArgumentException if the string is null, empty, malformed,
     *                                  or values cannot be parsed
     */
    public static ChunkData deserialize(String chunk) {
        if (chunk == null || chunk.isEmpty()) {
            throw new IllegalArgumentException("Chunk data cannot be null or empty");
        }

        String[] parts = chunk.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid chunk format, expected 3 parts: worldId,x,z");
        }

        UUID worldId;
        int x, z;

        try {
            worldId = UUID.fromString(parts[0]);
            x = Integer.parseInt(parts[1]);
            z = Integer.parseInt(parts[2]);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid chunk data format", e);
        }

        return new ChunkData(worldId, x, z);
    }
}
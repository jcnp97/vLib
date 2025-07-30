package asia.virtualmc.vLib.integration.better_model;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.tracker.DummyTracker;
import org.bukkit.Location;
import org.bukkit.World;

public class BMDummyUtils {

    /**
     * Spawns a dummy model at the specified location using the given model name.
     *
     * @param location  the location to spawn the dummy at
     * @param modelName the name of the model to spawn
     * @return the spawned DummyTracker instance, or null if spawning failed
     */
    public static DummyTracker spawn(Location location, String modelName) {
        World world = location.getWorld();
        if (world == null) {
            ConsoleUtils.severe("Unable to spawn dummy because world is NULL on " + location);
            return null;
        }

        if (BetterModel.model(modelName).isPresent()) {
            return BetterModel.model(modelName).get().create(location);
        }

        return null;
    }

    /**
     * Despawn and closes the given DummyTracker.
     *
     * @param tracker the DummyTracker instance to despawn
     * @return true if the tracker was despawn, false if the tracker was null
     */
    public static boolean despawn(DummyTracker tracker) {
        if (tracker != null) {
            tracker.despawn();
            tracker.close();
            return true;
        }

        return false;
    }

    /**
     * Replaces an existing DummyTracker with a new one of the specified model name at the same location.
     * The old tracker is despawn and closed before spawning the new one.
     *
     * @param tracker   the DummyTracker instance to replace
     * @param modelName the name of the new model to spawn
     * @return the new DummyTracker instance, or null if replacement failed
     */
    public static DummyTracker replace(DummyTracker tracker, String modelName) {
        if (tracker != null) {
            Location location = tracker.location();
            tracker.despawn();
            tracker.close();

            return spawn(location, modelName);
        }

        return null;
    }
}

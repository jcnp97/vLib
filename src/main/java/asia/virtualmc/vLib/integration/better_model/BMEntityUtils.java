package asia.virtualmc.vLib.integration.better_model;

import asia.virtualmc.vLib.utilities.messages.ConsoleUtils;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.tracker.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;

import java.util.Optional;

public class BMEntityUtils {

    /**
     * Spawns a new BetterModel entity at the specified location with the given model name.
     * The entity spawned is an ItemDisplay, configured with invulnerability, persistence, and no gravity.
     * If the world is null or the model name is invalid, logs an error and returns null.
     *
     * @param location the location to spawn the entity
     * @param modelName the BetterModel model name to apply
     * @return the spawned Entity, or null if failed
     */
    public static Entity spawn(Location location, String modelName) {
        World world = location.getWorld();
        if (world == null) {
            ConsoleUtils.severe("Unable to spawn BetterModel entity because world is NULL on " + location);
            return null;
        }

        Entity entity = world.spawn(location.add(0.5, 1.0, 0.5), org.bukkit.entity.ItemDisplay.class, e -> {
            e.setInvulnerable(true);
            e.setPersistent(true);
            e.setGravity(false);
        });

        EntityTracker tracker = BetterModel.model(modelName)
                .map(r -> r.getOrCreate(entity))
                .orElse(null);

        if (tracker == null) {
            entity.remove();
            ConsoleUtils.severe("Unable to render model on " + entity + " because modelName is invalid!");
            return null;
        }

        //tracker.asTrackerData().scaler().multiply(scale);
        //tracker.forceUpdate(true);
        return entity;
    }

    /**
     * Despawn the given entity if it contains BetterModel data.
     * Closes the associated EntityTrackerRegistry and removes the entity from the world.
     *
     * @param entity the entity to despawn
     * @return true if the entity was despawn, false otherwise
     */
    public static boolean despawn(Entity entity) {
        if (EntityTrackerRegistry.hasModelData(entity)) {
            EntityTrackerRegistry registry = EntityTrackerRegistry.registry(entity);
            registry.close();
            entity.remove();
            return true;
        }

        return false;
    }

    /**
     * Changes the BetterModel model of the given entity to a new model name.
     * If the entity already has the desired model or the model name is invalid, does nothing or removes the entity.
     * Closes the previous EntityTracker if present and assigns the new model.
     *
     * @param entity the entity whose model is to be changed
     * @param modelName the new BetterModel model name to apply
     * @return true if the model was changed, false otherwise
     */
    public static boolean change(Entity entity, String modelName) {
        if (entity == null || modelName == null) return false;

        String current = entity.getPersistentDataContainer()
                .get(EntityTrackerRegistry.TRACKING_ID, PersistentDataType.STRING);
        if (modelName.equals(current)) {
            return false;
        }

        Optional<ModelRenderer> rendererOpt = BetterModel.model(modelName);
        if (rendererOpt.isEmpty()) {
            entity.remove();
            ConsoleUtils.severe("Unable to render new model on: " + entity + " because the modelName is invalid!");
            return false;
        }

        if (EntityTrackerRegistry.hasModelData(entity)) {
            EntityTracker tracker = EntityTrackerRegistry.registry(entity).first();

            if (tracker != null) {
                tracker.close();
            }

            ModelRenderer renderer = rendererOpt.get();
            renderer.getOrCreate(entity);
        }

        return true;
    }
}
package asia.virtualmc.vLib.integration.better_model;

import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.tracker.EntityTracker;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class BMAnimUtils {

    /**
     * Plays the specified animation on the given entity using the provided {@link AnimationModifier}.
     *
     * @param entity   The target entity to animate.
     * @param animName The name of the animation to play.
     * @param modifier The animation modifier to apply (e.g., play once, loop, delay).
     * @return true if the animation was successfully triggered; false otherwise.
     */
    public static boolean play(@NotNull Entity entity, String animName, AnimationModifier modifier) {
        EntityTrackerRegistry registry = EntityTrackerRegistry.registry(entity);
        if (registry == null) {
            return false;
        }

        EntityTracker tracker = registry.first();
        if (tracker != null) {
            tracker.animate(animName, modifier);
            return true;
        }

        return false;
    }

    /**
     * Plays the specified animation once on the given entity using {@link AnimationModifier#DEFAULT_WITH_PLAY_ONCE}.
     *
     * @param entity   The target entity to animate.
     * @param animName The name of the animation to play once.
     * @return true if the animation was successfully triggered; false otherwise.
     */
    public static boolean playOnce(@NotNull Entity entity, String animName) {
        EntityTrackerRegistry registry = EntityTrackerRegistry.registry(entity);
        if (registry == null) {
            return false;
        }

        EntityTracker tracker = registry.first();
        if (tracker != null) {
            tracker.animate(animName, AnimationModifier.DEFAULT_WITH_PLAY_ONCE);
            return true;
        }

        return false;
    }

    /**
     * Plays the specified animation in a loop on the given entity using the default looping behavior.
     *
     * @param entity   The target entity to animate.
     * @param animName The name of the animation to play in a loop.
     * @return true if the animation was successfully triggered; false otherwise.
     */
    public static boolean playLoop(@NotNull Entity entity, String animName) {
        EntityTrackerRegistry registry = EntityTrackerRegistry.registry(entity);
        if (registry == null) {
            return false;
        }

        EntityTracker tracker = registry.first();
        if (tracker != null) {
            tracker.animate(animName);
            return true;
        }

        return false;
    }
}

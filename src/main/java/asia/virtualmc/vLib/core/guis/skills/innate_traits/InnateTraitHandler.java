package asia.virtualmc.vLib.core.guis.skills.innate_traits;

import asia.virtualmc.vLib.core.configs.InnateTraitConfig;

import java.util.Map;
import java.util.UUID;

public interface InnateTraitHandler {
    // Player Data
    int[] getAllTraits(UUID uuid);
    int getTraitPoints(UUID uuid);

    // Innate Trait Data
    Map<String, InnateTraitConfig.InnateTrait> getTraits();

    // Processing Methods
    void setTraitPoints(UUID uuid, int value);
    void addTraits(UUID uuid, int[] values);
}

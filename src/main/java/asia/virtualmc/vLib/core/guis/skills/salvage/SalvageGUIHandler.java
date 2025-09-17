package asia.virtualmc.vLib.core.guis.skills.salvage;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface SalvageGUIHandler {
    int getRarityId(ItemStack item);
    String getComponent(int rarityId);
    void addComponent(UUID uuid, String componentName, int amount);
}
package asia.virtualmc.vLib.core.guis.skills.sell;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface SellGUIHandler {
    int getRarityId(ItemStack item);
    double getValue(int rarityId);
    double getBonusValue(ItemStack item);
    double getSellMultiplier(UUID uuid);
}
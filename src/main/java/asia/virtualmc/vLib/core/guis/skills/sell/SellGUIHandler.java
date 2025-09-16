package asia.virtualmc.vLib.core.guis.skills.sell;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface SellGUIHandler {
    double getValue(ItemStack item);
    double getSellMultiplier(UUID uuid);
}
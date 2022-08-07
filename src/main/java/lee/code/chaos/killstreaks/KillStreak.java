package lee.code.chaos.killstreaks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class KillStreak {
    public abstract String name();
    public abstract String description();
    public abstract long cost();
    public abstract ItemStack lockedPreview();
    public abstract ItemStack unlockedPreview();
    public abstract ItemStack selectedPreview();
    public abstract int requiredKillStreak();
    public abstract void runLogic(Player player);
}

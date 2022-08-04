package lee.code.chaos.kits;

import org.bukkit.inventory.ItemStack;
import java.util.Map;

public abstract class Kit {
    public abstract String name();
    public abstract Map<Integer, ItemStack> inventory();
}

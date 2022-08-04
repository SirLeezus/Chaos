package lee.code.chaos.kits.kit;

import lee.code.chaos.kits.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Default extends Kit {

    @Override
    public String name() {
        return "default";
    }

    @Override
    public Map<Integer, ItemStack> inventory() {
        Map<Integer, ItemStack> kit = new HashMap<>();
        kit.put(1, new ItemStack(Material.SHIELD, 1));
        kit.put(3, new ItemStack(Material.IRON_AXE, 1));
        kit.put(4, new ItemStack(Material.BOW, 1));
        kit.put(5, new ItemStack(Material.COOKED_BEEF, 5));
        kit.put(6, new ItemStack(Material.ARROW, 15));
        kit.put(7, new ItemStack(Material.OAK_PLANKS, 30));
        return kit;
    }
}

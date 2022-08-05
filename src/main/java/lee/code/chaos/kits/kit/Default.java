package lee.code.chaos.kits.kit;

import lee.code.chaos.kits.Kit;
import lee.code.chaos.recipes.Tool;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class Default extends Kit {

    private final Map<Integer, ItemStack> kit = new HashMap<>();

    public Default() {
        kit.put(-1, new ItemStack(Material.SHIELD, 1));
        kit.put(0, Tool.IRON_SWORD.getItem());
        kit.put(1, new ItemStack(Material.BOW, 1));
        kit.put(2, new ItemStack(Material.ARROW, 30));
        kit.put(3, new ItemStack(Material.COOKED_BEEF, 5));
        kit.put(4, Tool.IRON_PICKAXE.getItem());
        kit.put(5, Tool.IRON_AXE.getItem());
        kit.put(6, new ItemStack(Material.OAK_PLANKS, 30));
    }

    @Override
    public String name() {
        return "default";
    }

    @Override
    public Map<Integer, ItemStack> inventory() {
        return kit;
    }
}

package lee.code.chaos.kits.kit;

import lee.code.chaos.Chaos;
import lee.code.chaos.kits.Kit;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.recipes.Tool;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class DeepDive extends Kit {

    private final Map<Integer, ItemStack> kit = new HashMap<>();
    private final ItemStack lockedPreview;
    private final ItemStack unlockedPreview;

    public DeepDive() {
        kit.put(-1, new ItemStack(Material.SHIELD, 1));
        kit.put(0, Tool.TRIDENT.getItem());
        kit.put(1, Tool.IRON_AXE.getItem());
        kit.put(2, new ItemStack(Material.COOKED_SALMON, 15));
        kit.put(3, new ItemStack(Material.OAK_PLANKS, 30));
        ItemStack locked = BukkitUtils.getItem(Material.STRUCTURE_VOID,
                Lang.MENU_KIT_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KIT_LORE_LOCKED.getString(new String[] { BukkitUtils.parseValue(cost()) }),
                null,
                true);
        ItemMeta lockedMeta = locked.getItemMeta();
        Chaos.getPlugin().getPU().setPreviewItemKitMeta(lockedMeta, name());
        locked.setItemMeta(lockedMeta);
        lockedPreview = locked;

        ItemStack unlocked = BukkitUtils.getItem(Material.TRIDENT,
                Lang.MENU_KIT_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KIT_LORE_UNLOCKED.getString(null),
                null,
                true);
        ItemMeta unlockedMeta = unlocked.getItemMeta();
        Chaos.getPlugin().getPU().setPreviewItemKitMeta(unlockedMeta, name());
        unlocked.setItemMeta(unlockedMeta);
        unlockedPreview = unlocked;
    }

    @Override
    public String name() {
        return "deep_dive";
    }

    @Override
    public Map<Integer, ItemStack> inventory() {
        return kit;
    }

    @Override
    public ItemStack lockedPreview() {
        return lockedPreview;
    }

    @Override
    public ItemStack unlockedPreview() {
        return unlockedPreview;
    }

    @Override
    public long cost() {
        return 1000;
    }
}

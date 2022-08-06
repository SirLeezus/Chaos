package lee.code.chaos.kits.kit;

import lee.code.chaos.Chaos;
import lee.code.chaos.PU;
import lee.code.chaos.kits.Kit;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.recipes.Tool;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class DeepDive extends Kit {

    private final Map<Integer, ItemStack> kit = new HashMap<>();
    private final ItemStack lockedPreview;
    private final ItemStack unlockedPreview;

    public DeepDive() {
        PU pu = Chaos.getPlugin().getPU();
        kit.put(-1, new ItemStack(Material.SHIELD, 1));
        ItemStack trident = Tool.TRIDENT.getItem();
        ItemMeta tridentMeta = trident.getItemMeta();
        tridentMeta.addEnchant(Enchantment.LOYALTY, 3, false);
        trident.setItemMeta(tridentMeta);
        kit.put(0, trident);
        kit.put(1, new ItemStack(Material.COOKED_SALMON, 15));
        kit.put(2, Tool.IRON_AXE.getItem());
        kit.put(3, new ItemStack(Material.OAK_PLANKS, 30));
        ItemStack locked = BukkitUtils.getItem(Material.STRUCTURE_VOID,
                Lang.MENU_KIT_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KIT_LORE_LOCKED.getString(new String[] { BukkitUtils.parseValue(cost()) }),
                null,
                true);
        ItemMeta lockedMeta = locked.getItemMeta();
        pu.setPreviewItemKitMeta(lockedMeta, name());
        locked.setItemMeta(lockedMeta);
        lockedPreview = locked;

        ItemStack unlocked = BukkitUtils.getItem(Material.TRIDENT,
                Lang.MENU_KIT_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KIT_LORE_UNLOCKED.getString(null),
                null,
                true);
        ItemMeta unlockedMeta = unlocked.getItemMeta();
        pu.setPreviewItemKitMeta(unlockedMeta, name());
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

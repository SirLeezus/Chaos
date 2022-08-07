package lee.code.chaos.kits.kit;

import lee.code.chaos.Chaos;
import lee.code.chaos.PU;
import lee.code.chaos.kits.Kit;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.recipes.Tool;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class Engineer extends Kit {

    private final Map<Integer, ItemStack> kit = new HashMap<>();
    private final ItemStack lockedPreview;
    private final ItemStack unlockedPreview;
    private final ItemStack selectedPreview;

    public Engineer() {
        PU pu = Chaos.getPlugin().getPU();
        kit.put(-1, new ItemStack(Material.SHIELD, 1));
        kit.put(0, new ItemStack(Material.REDSTONE, 30));
        kit.put(1, new ItemStack(Material.STICKY_PISTON, 3));
        kit.put(2, new ItemStack(Material.PISTON, 3));
        kit.put(3, new ItemStack(Material.REDSTONE_TORCH, 3));
        kit.put(4, Tool.IRON_SWORD.getItem());
        kit.put(5, new ItemStack(Material.COOKIE, 16));
        kit.put(6, Tool.IRON_AXE.getItem());
        kit.put(7, new ItemStack(Material.OAK_PLANKS, 30));

        ItemStack locked = BukkitUtils.getItem(Material.STRUCTURE_VOID,
                Lang.MENU_KIT_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KIT_LORE_LOCKED.getString(new String[] { String.valueOf(cost()) }),
                null,
                true);
        ItemMeta lockedMeta = locked.getItemMeta();
        pu.setPreviewItemKitMeta(lockedMeta, name());
        locked.setItemMeta(lockedMeta);
        lockedPreview = locked;

        ItemStack unlocked = BukkitUtils.getItem(Material.REDSTONE,
                Lang.MENU_KIT_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KIT_LORE_UNLOCKED.getString(null),
                null,
                true);
        ItemMeta unlockedMeta = unlocked.getItemMeta();
        pu.setPreviewItemKitMeta(unlockedMeta, name());
        unlocked.setItemMeta(unlockedMeta);
        unlockedPreview = unlocked;

        ItemStack selected = BukkitUtils.getItem(Material.REDSTONE,
                Lang.MENU_KIT_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KIT_LORE_SELECTED.getString(null),
                null,
                true);
        ItemMeta selectedMeta = selected.getItemMeta();
        pu.setPreviewItemKitMeta(selectedMeta, name());
        selectedMeta.addEnchant(Enchantment.ARROW_INFINITE, 0, true);
        selectedMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        selected.setItemMeta(selectedMeta);
        selectedPreview = selected;
    }

    @Override
    public String name() {
        return "engineer";
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
    public ItemStack selectedPreview() {
        return selectedPreview;
    }

    @Override
    public long cost() {
        return 1000;
    }
}

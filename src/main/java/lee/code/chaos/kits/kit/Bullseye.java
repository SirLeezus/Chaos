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
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;

public class Bullseye extends Kit {

    private final Map<Integer, ItemStack> kit = new HashMap<>();
    private final ItemStack lockedPreview;
    private final ItemStack unlockedPreview;
    private final ItemStack selectedPreview;

    public Bullseye() {
        PU pu = Chaos.getPlugin().getPU();
        kit.put(-1, new ItemStack(Material.SHIELD, 1));
        kit.put(0, new ItemStack(Material.GOLDEN_CARROT, 10));
        kit.put(1, new ItemStack(Material.BOW));
        kit.put(2, new ItemStack(Material.SPECTRAL_ARROW, 10));

        ItemStack poisonArrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta poisonArrowMeta = (PotionMeta) poisonArrow.getItemMeta();
        poisonArrowMeta.setBasePotionData(new PotionData(PotionType.POISON));
        poisonArrow.setItemMeta(poisonArrowMeta);
        poisonArrow.setAmount(10);
        kit.put(3, poisonArrow);

        ItemStack slownessArrow = new ItemStack(Material.TIPPED_ARROW);
        PotionMeta slownessArrowMeta = (PotionMeta) slownessArrow.getItemMeta();
        slownessArrowMeta.setBasePotionData(new PotionData(PotionType.SLOWNESS));
        slownessArrow.setItemMeta(slownessArrowMeta);
        slownessArrow.setAmount(10);
        kit.put(4, slownessArrow);

        kit.put(5, Tool.IRON_AXE.getItem());
        kit.put(6, new ItemStack(Material.OAK_PLANKS, 30));
        ItemStack locked = BukkitUtils.getItem(Material.STRUCTURE_VOID,
                Lang.MENU_KIT_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KIT_LORE_LOCKED.getString(new String[] { String.valueOf(cost()) }),
                null,
                true);
        ItemMeta lockedMeta = locked.getItemMeta();
        pu.setPreviewItemKitMeta(lockedMeta, name());
        locked.setItemMeta(lockedMeta);
        lockedPreview = locked;

        ItemStack unlocked = BukkitUtils.getItem(Material.BOW,
                Lang.MENU_KIT_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KIT_LORE_UNLOCKED.getString(null),
                null,
                true);
        ItemMeta unlockedMeta = unlocked.getItemMeta();
        pu.setPreviewItemKitMeta(unlockedMeta, name());
        unlocked.setItemMeta(unlockedMeta);
        unlockedPreview = unlocked;

        ItemStack selected = BukkitUtils.getItem(Material.BOW,
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
        return "bullseye";
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
        return 1500;
    }
}

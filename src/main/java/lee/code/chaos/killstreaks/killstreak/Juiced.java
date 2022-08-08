package lee.code.chaos.killstreaks.killstreak;

import lee.code.chaos.Chaos;
import lee.code.chaos.PU;
import lee.code.chaos.killstreaks.KillStreak;
import lee.code.chaos.lists.Lang;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class Juiced extends KillStreak {

    private final ItemStack lockedPreview;
    private final ItemStack unlockedPreview;
    private final ItemStack selectedPreview;

    public Juiced() {
        PU pu = Chaos.getPlugin().getPU();
        ItemStack locked = BukkitUtils.getItem(Material.STRUCTURE_VOID,
                Lang.MENU_KILL_STREAK_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KILL_STREAK_LORE_LOCKED.getString(new String[] { description(), String.valueOf(requiredKillStreak()), BukkitUtils.parseValue(cost()) }),
                null,
                true);
        ItemMeta lockedMeta = locked.getItemMeta();
        pu.setPreviewItemKitMeta(lockedMeta, name());
        locked.setItemMeta(lockedMeta);
        lockedPreview = locked;

        ItemStack unlocked = BukkitUtils.getItem(Material.GOLDEN_APPLE,
                Lang.MENU_KILL_STREAK_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KILL_STREAK_LORE_UNLOCKED.getString(new String[] { description(), String.valueOf(requiredKillStreak()) }),
                null,
                true);
        ItemMeta unlockedMeta = unlocked.getItemMeta();
        pu.setPreviewItemKitMeta(unlockedMeta, name());
        unlocked.setItemMeta(unlockedMeta);
        unlockedPreview = unlocked;

        ItemStack selected = BukkitUtils.getItem(Material.GOLDEN_APPLE,
                Lang.MENU_KILL_STREAK_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KILL_STREAK_LORE_SELECTED.getString(new String[] { description(), String.valueOf(requiredKillStreak()) }),
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
        return "juiced";
    }

    @Override
    public String description() {
        return "&7Gives you 20 extra hearts for 5mins\n&7or until you die.";
    }

    @Override
    public long cost() {
        return 1000;
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
    public int requiredKillStreak() {
        return 5;
    }

    @Override
    public void runLogic(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 300 * 20, 9, true, true, true));
        player.setHealth(60);
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.KILL_STREAK_ACTIVATED_PLAYER.getComponent(new String[] { BukkitUtils.parseCapitalization(name()) })).hoverEvent(Lang.KILL_STREAK_ACTIVATED_HOVER.getComponent(new String[] { description() })));
    }
}

package lee.code.chaos.killstreaks.killstreak;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.PU;
import lee.code.chaos.killstreaks.KillStreak;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.maps.MapData;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class CruelDragon extends KillStreak {

    private final ItemStack lockedPreview;
    private final ItemStack unlockedPreview;
    private final ItemStack selectedPreview;

    public CruelDragon() {
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

        ItemStack unlocked = BukkitUtils.getItem(Material.DRAGON_BREATH,
                Lang.MENU_KILL_STREAK_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KILL_STREAK_LORE_UNLOCKED.getString(new String[] { description(), String.valueOf(requiredKillStreak()) }),
                null,
                true);
        ItemMeta unlockedMeta = unlocked.getItemMeta();
        pu.setPreviewItemKitMeta(unlockedMeta, name());
        unlocked.setItemMeta(unlockedMeta);
        unlockedPreview = unlocked;

        ItemStack selected = BukkitUtils.getItem(Material.DRAGON_BREATH,
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
        return "cruel_dragon";
    }

    @Override
    public String description() {
        return "&7Summons a ender dragon that'll generally\n&7target your opponents spawn but it'll still\n&7be somewhat unpredictable.";
    }

    @Override
    public long cost() {
        return 5000;
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
        return 25;
    }

    @Override
    public void runLogic(Player player) {
        Chaos plugin = Chaos.getPlugin();
        UUID uuid = player.getUniqueId();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        GameTeam team = map.getTeam(uuid);
        Location spawn = team.equals(GameTeam.RED) ? map.getBlueSpawn() : map.getRedSpawn();
        String color = team.equals(GameTeam.RED) ? Lang.RED_COLOR.getString(null) : Lang.BLUE_COLOR.getString(null);

        EnderDragon dragon = (EnderDragon) player.getWorld().spawnEntity(new Location(spawn.getWorld(), spawn.getX(), spawn.getY() + 20, spawn.getZ()), EntityType.ENDER_DRAGON);
        data.setEntityOwner(dragon.getUniqueId(), uuid);
        dragon.setPhase(EnderDragon.Phase.CHARGE_PLAYER);
        dragon.setPodium(spawn);
        Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.KILL_STREAK_ACTIVATED_SERVER.getComponent(new String[] { color, player.getName(), BukkitUtils.parseCapitalization(name()) })).hoverEvent(Lang.KILL_STREAK_ACTIVATED_HOVER.getComponent(new String[] { description() })));
    }
}

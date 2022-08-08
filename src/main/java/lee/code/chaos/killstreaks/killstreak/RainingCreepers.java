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
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class RainingCreepers extends KillStreak {

    private final ItemStack lockedPreview;
    private final ItemStack unlockedPreview;
    private final ItemStack selectedPreview;

    public RainingCreepers() {
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

        ItemStack unlocked = BukkitUtils.getItem(Material.GUNPOWDER,
                Lang.MENU_KILL_STREAK_NAME.getString(new String[] { BukkitUtils.parseCapitalization(name()) }),
                Lang.MENU_KILL_STREAK_LORE_UNLOCKED.getString(new String[] { description(), String.valueOf(requiredKillStreak()) }),
                null,
                true);
        ItemMeta unlockedMeta = unlocked.getItemMeta();
        pu.setPreviewItemKitMeta(unlockedMeta, name());
        unlocked.setItemMeta(unlockedMeta);
        unlockedPreview = unlocked;

        ItemStack selected = BukkitUtils.getItem(Material.GUNPOWDER,
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
        return "raining_creepers";
    }

    @Override
    public String description() {
        return "&7Summon charged creepers on\n&7all your opponents.";
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
        return 30;
    }

    @Override
    public void runLogic(Player player) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        UUID uuid = player.getUniqueId();
        GameTeam team = map.getTeam(uuid);
        List<UUID> players = team.equals(GameTeam.RED) ? map.getBlueTeam() : map.getRedTeam();
        String color = team.equals(GameTeam.RED) ? Lang.RED_COLOR.getString(null) : Lang.BLUE_COLOR.getString(null);
        for (UUID sUUID : players) {
            Player sPlayer = Bukkit.getPlayer(sUUID);
            if (sPlayer != null) {
                Location pLocation = sPlayer.getLocation();
                Creeper creeper = (Creeper) pLocation.getWorld().spawnEntity(new Location(pLocation.getWorld(), pLocation.getX(), pLocation.getY() + 10, pLocation.getZ()), EntityType.CREEPER);
                data.setEntityOwner(creeper.getUniqueId(), uuid);
                creeper.setPowered(true);
            }
        }
        Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.KILL_STREAK_ACTIVATED_SERVER.getComponent(new String[] { color, player.getName(), BukkitUtils.parseCapitalization(name()) })).hoverEvent(Lang.KILL_STREAK_ACTIVATED_HOVER.getComponent(new String[] { description() })));
    }
}

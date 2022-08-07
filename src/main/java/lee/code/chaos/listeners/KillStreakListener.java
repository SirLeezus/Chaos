package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.maps.MapData;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class KillStreakListener implements Listener {

    @EventHandler
    public void onHorseSpawn(PlayerInteractEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        if (data.getGameState().equals(GameState.ACTIVE)) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            GameTeam team = map.getTeam(uuid);
            String color = team.equals(GameTeam.RED) ? Lang.RED_COLOR.getString(null) : Lang.BLUE_COLOR.getString(null);
            if (e.getAction().isRightClick() && player.getInventory().getItemInMainHand().getType().equals(Material.HORSE_SPAWN_EGG)) {
                e.setCancelled(true);
                if (BukkitUtils.hasClickDelay(player)) return;
                BukkitUtils.removePlayerItems(player, new ItemStack(Material.HORSE_SPAWN_EGG), 1, true);
                Horse horse = (Horse) player.getWorld().spawnEntity(player.getLocation(), EntityType.HORSE);
                horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                horse.getInventory().setArmor(new ItemStack(Material.GOLDEN_HORSE_ARMOR));
                horse.customName(Lang.KILL_STREAK_HORSE_NAME.getComponent(new String[] { color, BukkitUtils.parseCapitalization(team.name()) }));
                horse.setTamed(true);
                data.setEntityOwner(horse.getUniqueId(), uuid);
            }
        }
    }

    @EventHandler
    public void onHorseInteract(PlayerInteractEntityEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        if (data.getGameState().equals(GameState.ACTIVE)) {
            Player player = e.getPlayer();
            if (e.getRightClicked() instanceof Horse horse) {
                if (data.hasEntityOwner(horse.getUniqueId())) {
                    UUID owner = data.getEntityOwner(horse.getUniqueId());
                    if (!map.getTeam(player.getUniqueId()).equals(map.getTeam(owner))) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFireChargeThrow(PlayerInteractEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        if (data.getGameState().equals(GameState.ACTIVE)) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            if (e.getAction().isLeftClick() && player.getInventory().getItemInMainHand().getType().equals(Material.FIRE_CHARGE)) {
                e.setCancelled(true);
                if (BukkitUtils.hasClickDelay(player)) return;
                BukkitUtils.removePlayerItems(player, new ItemStack(Material.FIRE_CHARGE), 1, true);
                Location eye = player.getEyeLocation();
                Location loc = eye.add(eye.getDirection().multiply(1.2));
                Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
                fireball.setVelocity(loc.getDirection().normalize().multiply(2));
                fireball.setShooter(player);
                data.setEntityOwner(fireball.getUniqueId(), uuid);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_GHAST_SHOOT, (float) 0.5, (float) 0.5);
            }
        }
    }
}
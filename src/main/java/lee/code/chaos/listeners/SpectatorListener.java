package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.lists.SpectatorItem;
import lee.code.chaos.maps.MapData;
import lee.code.chaos.menusystem.menus.KillStreakMenu;
import lee.code.chaos.menusystem.menus.KitMenu;
import lee.code.chaos.menusystem.menus.TeamMenu;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import java.util.UUID;

public class SpectatorListener implements Listener {

    @EventHandler
    public void onVoidDeath(PlayerMoveEvent e) {
        if (e.getTo().getBlockY() <= 0) {
            Chaos plugin = Chaos.getPlugin();
            Data data = plugin.getData();
            MapData map = data.getActiveMap().getData();
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            if (map.getSpectators().contains(uuid)) {
                player.teleportAsync(map.getSpawn());
            }
        }
    }

    @EventHandler
    public void onSpectatorEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player player) {
            Data data = Chaos.getPlugin().getData();
            MapData map = data.getActiveMap().getData();
            if (map.getSpectators().contains(player.getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpectatorEntityInteract(PlayerInteractEntityEvent e) {
        Data data = Chaos.getPlugin().getData();
        MapData map = data.getActiveMap().getData();
        if (map.getSpectators().contains(e.getPlayer().getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectatorBlockBreak(BlockBreakEvent e) {
        Data data = Chaos.getPlugin().getData();
        MapData map = data.getActiveMap().getData();
        if (map.getSpectators().contains(e.getPlayer().getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectatorHangingEntityBreak(HangingBreakByEntityEvent e) {
        if (e.getRemover() instanceof Player player) {
            Data data = Chaos.getPlugin().getData();
            MapData map = data.getActiveMap().getData();
            if (map.getSpectators().contains(player.getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpectatorVehicleBreak(VehicleDestroyEvent e) {
        if (e.getAttacker() instanceof Player player) {
            Data data = Chaos.getPlugin().getData();
            MapData map = data.getActiveMap().getData();
            if (map.getSpectators().contains(player.getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpectatorBucketEmpty(PlayerBucketEmptyEvent e) {
        Data data = Chaos.getPlugin().getData();
        MapData map = data.getActiveMap().getData();
        if (map.getSpectators().contains(e.getPlayer().getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectatorBlockPlace(BlockPlaceEvent e) {
        Data data = Chaos.getPlugin().getData();
        MapData map = data.getActiveMap().getData();
        if (map.getSpectators().contains(e.getPlayer().getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectatorEntityBlockPlace(EntityPlaceEvent e) {
        if (e.getEntity() instanceof Player player) {
            Data data = Chaos.getPlugin().getData();
            MapData map = data.getActiveMap().getData();
            if (map.getSpectators().contains(player.getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpectatorArmorStandInteract(PlayerInteractAtEntityEvent e) {
        Data data = Chaos.getPlugin().getData();
        MapData map = data.getActiveMap().getData();
        if (map.getSpectators().contains(e.getPlayer().getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectatorDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            Data data = Chaos.getPlugin().getData();
            MapData map = data.getActiveMap().getData();
            if (map.getSpectators().contains(player.getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpectatorItemDrop(PlayerDropItemEvent e) {
        Data data = Chaos.getPlugin().getData();
        MapData map = data.getActiveMap().getData();
        if (map.getSpectators().contains(e.getPlayer().getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpectatorMoveInventoryItem(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player player) {
            Data data = Chaos.getPlugin().getData();
            MapData map = data.getActiveMap().getData();
            if (map.getSpectators().contains(player.getUniqueId()) || data.getGameState().equals(GameState.TRANSITIONING)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSpectatorInteractInventory(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Data data = Chaos.getPlugin().getData();
        if (e.getAction().isRightClick()) {
            if (data.getActiveMap().getData().getSpectators().contains(uuid)) {
                e.setCancelled(true);
                if (BukkitUtils.hasClickDelay(player)) return;
                if (!data.getGameState().equals(GameState.TRANSITIONING)) {
                    if (player.getInventory().getItemInMainHand().equals(SpectatorItem.TEAM_SELECTOR.getItem())) {
                        new TeamMenu(data.getPlayerMU(uuid)).open();
                        player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1);
                    } else if (player.getInventory().getItemInMainHand().equals(SpectatorItem.KIT_SELECTOR.getItem())) {
                        new KitMenu(data.getPlayerMU(uuid)).open();
                        player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1);
                    } else if (player.getInventory().getItemInMainHand().equals(SpectatorItem.KILL_STREAK_SELECTOR.getItem())) {
                        new KillStreakMenu(data.getPlayerMU(uuid)).open();
                        player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1);
                    }
                }
            }
        }
    }
}

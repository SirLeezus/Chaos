package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.lists.SpectatorItem;
import lee.code.chaos.maps.MapData;
import lee.code.chaos.menusystem.menus.TeamMenu;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class SpectatorListener implements Listener {

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
                if (player.getInventory().getItemInMainHand().equals(SpectatorItem.TEAM_SELECTOR.getItem()) && !data.getGameState().equals(GameState.TRANSITIONING)) {
                    new TeamMenu(data.getPlayerMU(uuid)).open();
                    player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1);
                }
            }
        }
    }
}

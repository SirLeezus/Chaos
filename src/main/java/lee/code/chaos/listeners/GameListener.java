package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.lists.Setting;
import lee.code.chaos.managers.GameManager;
import lee.code.chaos.maps.MapData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class GameListener implements Listener {

    @EventHandler
    public void onArmorRemove(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof PlayerInventory) {
            if (e.getWhoClicked() instanceof Player player) {
                if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                    int slot = e.getSlot();
                    switch (slot) {
                        case 36, 37, 38, 39 -> e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        GameManager gameManager = plugin.getGameManager();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        GameTeam team = map.getTeam(uuid);
        if (data.getGameState().equals(GameState.ACTIVE)) {
            if (e.getBlock().getType().equals(Material.BLUE_WOOL)) {
                e.setCancelled(true);
                if (team == GameTeam.BLUE) return;
                data.addRedPoint(uuid);
                e.getBlock().setType(Material.AIR);
                Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.BROKE_WOOL.getComponent(new String[] {
                        Lang.RED_COLOR.getString(null),
                        player.getName(),
                        Lang.BLUE_TEAM.getString(null),
                        Lang.BLUE_COLOR.getString(null),
                        String.valueOf(Setting.WIN_SCORE.getValue() - map.getRedScore()),
                        String.valueOf(Setting.WIN_SCORE.getValue()),
                })));

                if (map.getRedScore() >= Setting.WIN_SCORE.getValue()) {
                    if (data.getGameState().equals(GameState.ACTIVE)) {
                        gameManager.endGame();
                    }
                }
            } else if (e.getBlock().getType().equals(Material.RED_WOOL)) {
                e.setCancelled(true);
                if (team == GameTeam.RED) return;
                data.addBluePoint(uuid);
                e.getBlock().setType(Material.AIR);
                Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.BROKE_WOOL.getComponent(new String[] {
                        Lang.BLUE_COLOR.getString(null),
                        player.getName(),
                        Lang.RED_TEAM.getString(null),
                        Lang.RED_COLOR.getString(null),
                        String.valueOf(Setting.WIN_SCORE.getValue() - map.getBlueScore()),
                        String.valueOf(Setting.WIN_SCORE.getValue()),
                })));
                if (map.getBlueScore() >= Setting.WIN_SCORE.getValue()) {
                    if (data.getGameState().equals(GameState.ACTIVE)) {
                        gameManager.endGame();
                    }
                }
            } else e.setCancelled(isSafeLocation(e.getBlock().getLocation()));
        } else e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (Chaos.getPlugin().getData().getGameState().equals(GameState.ACTIVE)) {
            e.setCancelled(isSafeLocation(e.getBlock().getLocation()));
        } else e.setCancelled(true);
    }

    @EventHandler
    public void onBucketUse(PlayerBucketEmptyEvent e) {
        if (Chaos.getPlugin().getData().getGameState().equals(GameState.ACTIVE)) {
            e.setCancelled(isSafeLocation(e.getBlock().getLocation()));
        } else e.setCancelled(true);
    }

    @EventHandler
    public void onDamageSpawn(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (Chaos.getPlugin().getData().getGameState().equals(GameState.ACTIVE)) {
                e.setCancelled(isSafeLocation(player.getLocation()));
            } else e.setCancelled(true);
        }
    }

    private boolean isSafeLocation(Location location) {
        MapData map = Chaos.getPlugin().getData().getActiveMap().getData();
        Location redSpawn = map.getRedSpawn();
        Location blueSpawn = map.getBlueSpawn();
        Location gameSpawn = map.getSpawn();
        int radius = 5;
        boolean red = location.distanceSquared(redSpawn) <= radius * radius;
        boolean blue = location.distanceSquared(blueSpawn) <= radius * radius;
        boolean spawn = location.distanceSquared(gameSpawn) <= radius * radius;
        return red || blue || spawn;
    }
}

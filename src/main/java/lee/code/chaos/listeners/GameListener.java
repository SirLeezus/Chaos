package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.lists.Setting;
import lee.code.chaos.managers.GameManager;
import lee.code.chaos.maps.MapData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
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
            } else e.setCancelled(Chaos.getPlugin().getPU().isSafeLocation(e.getBlock().getLocation()));
        } else e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (Chaos.getPlugin().getData().getGameState().equals(GameState.ACTIVE)) {
            e.setCancelled(Chaos.getPlugin().getPU().isSafeLocation(e.getBlock().getLocation()));
        } else e.setCancelled(true);
    }

    @EventHandler
    public void onBucketUse(PlayerBucketEmptyEvent e) {
        if (Chaos.getPlugin().getData().getGameState().equals(GameState.ACTIVE)) {
            e.setCancelled(Chaos.getPlugin().getPU().isSafeLocation(e.getBlock().getLocation()));
        } else e.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamageSpawn(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (Chaos.getPlugin().getData().getGameState().equals(GameState.ACTIVE)) {
                e.setCancelled(Chaos.getPlugin().getPU().isSafeLocation(player.getLocation()));
            } else e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {
        Chaos plugin = Chaos.getPlugin();
        if (plugin.getData().getGameState().equals(GameState.ACTIVE)) {
            List<Block> blocks = new ArrayList<>(e.getBlocks());
            if (blocks.size() > 0) {
                Block lastBlock = blocks.get(blocks.size() - 1);
                lastBlock = lastBlock.getRelative(e.getDirection());
                blocks.add(lastBlock);
            }
            for (Block block : blocks) {
                if (block.getType().equals(Material.RED_WOOL)) e.setCancelled(true);
                else if (block.getType().equals(Material.BLUE_WOOL)) e.setCancelled(true);
                else if (plugin.getPU().isSafeLocation(block.getLocation())) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        Chaos plugin = Chaos.getPlugin();
        if (plugin.getData().getGameState().equals(GameState.ACTIVE)) {
            List<Block> blocks = new ArrayList<>(e.getBlocks());
            if (blocks.size() > 0) {
                Block lastBlock = blocks.get(blocks.size() - 1);
                lastBlock = lastBlock.getRelative(e.getDirection());
                blocks.add(lastBlock);
            }
            for (Block block : blocks) {
                if (block.getType().equals(Material.RED_WOOL)) e.setCancelled(true);
                else if (block.getType().equals(Material.BLUE_WOOL)) e.setCancelled(true);
                else if (plugin.getPU().isSafeLocation(block.getLocation())) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFlow(BlockFromToEvent e) {
        Chaos plugin = Chaos.getPlugin();
        if (plugin.getData().getGameState().equals(GameState.ACTIVE)) {
            Block block = e.getBlock();
            if (block.getType().equals(Material.WATER) || block.getType().equals(Material.LAVA)) {
                if (plugin.getPU().isSafeLocation(e.getToBlock().getLocation())) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        Chaos plugin = Chaos.getPlugin();
        if (plugin.getData().getGameState().equals(GameState.ACTIVE)) {
            Block block = e.getBlock();
            if (plugin.getPU().isSafeLocation(block.getLocation()) || block.getType().equals(Material.RED_WOOL) || block.getType().equals(Material.BLUE_WOOL)) e.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onBlockChange(EntityChangeBlockEvent e) {
        Chaos plugin = Chaos.getPlugin();
        if (plugin.getData().getGameState().equals(GameState.ACTIVE)) {
            Block block = e.getBlock();
            if (plugin.getPU().isSafeLocation(block.getLocation()) || block.getType().equals(Material.RED_WOOL) || block.getType().equals(Material.BLUE_WOOL)) e.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onExplodeEvent(EntityExplodeEvent e) {
        Chaos plugin = Chaos.getPlugin();
        if (plugin.getData().getGameState().equals(GameState.ACTIVE)) {
            e.blockList().removeIf(block -> plugin.getPU().isSafeLocation(block.getLocation()) || block.getType().equals(Material.RED_WOOL) || block.getType().equals(Material.BLUE_WOOL));
        }
    }
}

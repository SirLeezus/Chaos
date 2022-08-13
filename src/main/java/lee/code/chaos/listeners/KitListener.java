package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

public class KitListener implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTNTExplode(EntityExplodeEvent e) {
        Chaos plugin = Chaos.getPlugin();
        if (plugin.getData().getGameState().equals(GameState.ACTIVE)) {
            for (Block b : e.blockList()) {
                if (!b.getType().equals(Material.RED_WOOL) && !b.getType().equals(Material.BLUE_WOOL) && !b.getType().equals(Material.TNT) && !b.getType().equals(Material.BEDROCK) && !plugin.getPU().isSafeLocation(b.getLocation())) {
                    float x = (float) -2 + (float) (Math.random() * ((2 - -2) + 1));
                    float y = (float) -1.5 + (float) (Math.random() * ((1.5 - -1.5) + 1));
                    float z = (float) -2 + (float) (Math.random() * ((2 - -2) + 1));

                    FallingBlock fallingBlock = b.getWorld().spawnFallingBlock(b.getLocation(), b.getBlockData());
                    fallingBlock.setDropItem(false);
                    fallingBlock.setVelocity(new Vector(x, y, z));
                    b.setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void onKitItemDrop(PlayerDropItemEvent e) {
        if (e.getItemDrop().getType().equals(EntityType.DROPPED_ITEM)) {
            if (e.getItemDrop().getItemStack().getType().equals(Material.TNT)) {
                Chaos.getPlugin().getData().addDroppingTNT(e.getPlayer().getUniqueId());
            } else if (e.getItemDrop().getItemStack().getType().equals(Material.FIRE_CHARGE)) {
                Chaos.getPlugin().getData().addDroppingFireCharge(e.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTNTThrow(PlayerInteractEvent e) {
        Data data = Chaos.getPlugin().getData();
        if (data.getGameState().equals(GameState.ACTIVE)) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            ItemStack handItem = player.getInventory().getItemInMainHand();
            if (data.isDroppingTNT(uuid)) {
                data.removeDroppingTNT(uuid);
                return;
            }
            if (handItem.getType().equals(Material.TNT) && e.getAction().isLeftClick()) {
                if (BukkitUtils.hasClickDelay(player)) return;
                e.setCancelled(true);
                FallingBlock tnt = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.TNT.createBlockData());
                data.setEntityOwner(tnt.getUniqueId(),uuid);
                tnt.setVelocity(player.getLocation().getDirection().multiply(1.1));
                BukkitUtils.removePlayerItems(player, handItem, 1, true);
            }
        }
    }
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTNTChange(EntityChangeBlockEvent e) {
        Data data = Chaos.getPlugin().getData();
        if (data.getGameState().equals(GameState.ACTIVE)) {
            if (e.getEntity() instanceof FallingBlock fallingBlock) {
                UUID fallingUUID = fallingBlock.getUniqueId();
                if (fallingBlock.getBlockData().getMaterial().equals(Material.TNT)) {
                    if (data.hasEntityOwner(fallingUUID)) {
                        Entity tnt = fallingBlock.getWorld().spawnEntity(fallingBlock.getLocation(), EntityType.PRIMED_TNT);
                        data.setEntityOwner(tnt.getUniqueId(), data.getEntityOwner(fallingUUID));
                        data.removeEntityOwner(fallingUUID);
                        e.setCancelled(true);
                        fallingBlock.remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTNTDropItem(EntityDropItemEvent e) {
        Data data = Chaos.getPlugin().getData();
        if (data.getGameState().equals(GameState.ACTIVE)) {
            if (e.getEntity() instanceof FallingBlock fallingBlock) {
                UUID fallingUUID = fallingBlock.getUniqueId();
                if (data.hasEntityOwner(fallingUUID)) {
                    Entity tnt = fallingBlock.getWorld().spawnEntity(fallingBlock.getLocation(), EntityType.PRIMED_TNT);
                    data.setEntityOwner(tnt.getUniqueId(), data.getEntityOwner(fallingUUID));
                    data.removeEntityOwner(fallingUUID);
                    e.setCancelled(true);
                    fallingBlock.remove();
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onTNTPlaceEvent(BlockPlaceEvent e) {
        if (!e.isCancelled()) {
            Block block = e.getBlock();
            if (block.getType().equals(Material.TNT)) {
                Data data = Chaos.getPlugin().getData();
                data.setBlockOwner(block.getLocation(), e.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onTNTNormalChange(EntitySpawnEvent e) {
        if (e.getEntity() instanceof TNTPrimed tnt) {
            Data data = Chaos.getPlugin().getData();
            if (data.hasBlockOwner(tnt.getLocation())) {
                data.setEntityOwner(tnt.getUniqueId(), data.getBlockOwner(tnt.getLocation()));
                data.removeBlockOwner(tnt.getLocation());
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
            if (data.isDroppingFireCharge(uuid)) {
                data.removeDroppingFireCharge(uuid);
                return;
            }
            if (player.getInventory().getItemInMainHand().getType().equals(Material.FIRE_CHARGE) && e.getAction().isLeftClick()) {
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
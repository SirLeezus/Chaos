package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
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

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onTNTThrow(PlayerInteractEvent e) {
        Data data = Chaos.getPlugin().getData();
        if (data.getGameState().equals(GameState.ACTIVE)) {
            Player player = e.getPlayer();
            ItemStack handItem = player.getInventory().getItemInMainHand();
            if (handItem.getType().equals(Material.TNT) && e.getAction().isLeftClick()) {
                if (BukkitUtils.hasClickDelay(player)) return;
                e.setCancelled(true);
                FallingBlock tnt = player.getWorld().spawnFallingBlock(player.getEyeLocation(), Material.TNT.createBlockData());
                data.setEntityOwner(tnt.getUniqueId(), player.getUniqueId());
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
}
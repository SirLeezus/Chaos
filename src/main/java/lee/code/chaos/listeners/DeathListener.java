package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.managers.GameManager;
import lee.code.chaos.maps.MapData;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.UUID;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeathByEntity(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player player) {
            Data data = Chaos.getPlugin().getData();
            UUID uuid = player.getUniqueId();
            if (!data.getActiveMap().getData().getSpectators().contains(uuid)) {
                if (e.getDamage() >= player.getHealth()) {
                    e.setCancelled(true);
                    Player attacker = Bukkit.getPlayer(data.getLastPlayerDamage(uuid));
                    respawnPlayer(player, attacker);
                    data.removeLastPlayerDamage(uuid);
                } else {
                    Player attacker = getLastAttacker(e.getDamager());
                    if (attacker != null) {
                        data.setLastPlayerDamage(player.getUniqueId(), attacker.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            Data data = Chaos.getPlugin().getData();
            UUID uuid = player.getUniqueId();
            if (!data.getActiveMap().getData().getSpectators().contains(uuid)) {
                if (e.getDamage() >= player.getHealth() || e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                    e.setCancelled(true);
                    if (BukkitUtils.hasClickDelay(player)) return;
                    Player attacker = Bukkit.getPlayer(data.getLastPlayerDamage(uuid));
                    respawnPlayer(player, attacker);
                    data.removeLastPlayerDamage(uuid);
                }
            }
        }
    }

    private void respawnPlayer(Player player, Player attacker) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        GameManager gameManager = plugin.getGameManager();
        GameTeam team = map.getTeam(player.getUniqueId());
        switch (team) {
            case BLUE -> {
                if (attacker != null) {
                    data.addPlayerKill(attacker.getUniqueId());
                    Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.PLAYER_KILLED_TARGET.getComponent(new String[] {
                            Lang.RED_COLOR.getString(null),
                            attacker.getName(),
                            Lang.BLUE_COLOR.getString(null),
                            player.getName()
                    })));
                } else Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.PLAYER_DIED.getComponent(new String[] { Lang.BLUE_COLOR.getString(null), player.getName() })));
                data.addPlayerDeath(player.getUniqueId());
                gameManager.respawnPlayer(player);
            }
            case RED -> {
                if (attacker != null) {
                    data.addPlayerKill(attacker.getUniqueId());
                    Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.PLAYER_KILLED_TARGET.getComponent(new String[] {
                            Lang.BLUE_COLOR.getString(null),
                            attacker.getName(),
                            Lang.RED_COLOR.getString(null),
                            player.getName()
                    })));
                } else Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.PLAYER_DIED.getComponent(new String[] { Lang.RED_COLOR.getString(null), player.getName() })));
                data.addPlayerDeath(player.getUniqueId());
                gameManager.respawnPlayer(player);
            }
            case SPECTATOR -> {
                Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.PLAYER_DIED.getComponent(new String[] { Lang.SPECTATOR_COLOR.getString(null), player.getName() })));
                gameManager.respawnPlayer(player);
            }
        }
    }

    private Player getLastAttacker(Entity entity) {
        if (entity instanceof Player player) {
            return player;
        } else if (entity instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player player) return player;
        } else if (entity instanceof TNTPrimed tnt) {
            if (tnt.getSource() instanceof Player player) return player;
        }
        return null;
    }
}

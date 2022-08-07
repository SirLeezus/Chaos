package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.killstreaks.KillStreak;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.managers.GameManager;
import lee.code.chaos.maps.MapData;
import lee.code.chaos.maps.ScoreData;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.LinkedList;
import java.util.UUID;

public class DeathListener implements Listener {

    @EventHandler
    public void onVoidDeath(PlayerMoveEvent e) {
        if (e.getTo().getBlockY() <= 60) {
            Player player = e.getPlayer();
            respawnPlayer(player, null);
        }
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        Data data = Chaos.getPlugin().getData();
        MapData map = data.getActiveMap().getData();
        if (e.getEntity() instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (!data.getActiveMap().getData().getSpectators().contains(uuid)) {
                Player attacker = getLastAttacker(e.getDamager());
                if (attacker != null) {
                    data.setLastPlayerDamage(player.getUniqueId(), attacker.getUniqueId());
                }
            }
            if (e.getDamager() instanceof Player damager) {
                UUID damUUID = damager.getUniqueId();
                if (map.getTeam(damUUID).equals(map.getTeam(uuid))) e.setCancelled(true);
            } else {
                UUID damUUID = e.getDamager().getUniqueId();
                if (data.hasEntityOwner(damUUID)) {
                    UUID ownerUUID = data.getEntityOwner(damUUID);
                    if (map.getTeam(ownerUUID).equals(map.getTeam(uuid))) e.setCancelled(true);
                }
            }
        } else if (e.getDamager() instanceof Player player) {
            UUID uuid = player.getUniqueId();
            Entity entity = e.getEntity();
            UUID eUUID = entity.getUniqueId();
            if (data.hasEntityOwner(eUUID)) {
                UUID owner = data.getEntityOwner(eUUID);
                if (map.getTeam(owner).equals(map.getTeam(uuid))) e.setCancelled(true);
            }
        } else if (e.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                UUID uuid = shooter.getUniqueId();
                if (e.getEntity() instanceof Player player) {
                    UUID pUUID = player.getUniqueId();
                    if (map.getTeam(pUUID).equals(map.getTeam(uuid))) e.setCancelled(true);
                } else {
                    UUID eUUID = e.getEntity().getUniqueId();
                    if (data.hasEntityOwner(eUUID)) {
                        UUID ownerUUID = data.getEntityOwner(eUUID);
                        if (map.getTeam(ownerUUID).equals(map.getTeam(uuid))) e.setCancelled(true);
                    }
                }
            } else if (projectile.getShooter() instanceof Entity entity) {
                UUID eUUID = entity.getUniqueId();
                if (data.hasEntityOwner(eUUID)) {
                    UUID ownerUUID = data.getEntityOwner(eUUID);
                    if (map.getTeam(ownerUUID).equals(map.getTeam(e.getEntity().getUniqueId()))) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onDeath(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            if (!e.isCancelled()) {
                Data data = Chaos.getPlugin().getData();
                MapData map = data.getActiveMap().getData();
                UUID uuid = player.getUniqueId();
                ScoreData scoreData = data.getPlayerScoreData(uuid);
                if (!map.getSpectators().contains(uuid)) {
                    if (map.getTeam(data.getLastPlayerDamage(uuid)).equals(map.getTeam(uuid))) {
                        e.setCancelled(true);
                        return;
                    }
                    if (e.getDamage() >= player.getHealth() && !e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                        e.setCancelled(true);
                        Player attacker = Bukkit.getPlayer(data.getLastPlayerDamage(uuid));
                        respawnPlayer(player, attacker);
                        data.removeLastPlayerDamage(uuid);
                        scoreData.setKillSteakOneUsed(false);
                        scoreData.setKillSteakTwoUsed(false);
                        scoreData.setKillSteakThreeUsed(false);
                    }
                }
            }
        }
    }

    private void killStreakChecker(Player attacker) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();
        UUID uuid = attacker.getUniqueId();
        if (cacheManager.hasSelectedKillStreak(uuid)) {
            ScoreData scoreData = data.getPlayerScoreData(uuid);
            int killStreak = scoreData.getKillStreak();
            LinkedList<KillStreak> killStreaks = cacheManager.getSelectedKillStreaks(uuid);
            for (int i = 0; i < killStreaks.size(); i++) {
                KillStreak streak = killStreaks.get(i);
                switch (i) {
                    case 0 -> {
                        if (!scoreData.isKillSteakOneUsed() && streak.requiredKillStreak() <= killStreak) {
                            streak.runLogic(attacker);
                            scoreData.setKillSteakOneUsed(true);
                        }
                    }
                    case 1 -> {
                        if (!scoreData.isKillSteakTwoUsed() && streak.requiredKillStreak() <= killStreak) {
                            streak.runLogic(attacker);
                            scoreData.setKillSteakTwoUsed(true);
                        }
                    }
                    case 2 -> {
                        if (!scoreData.isKillSteakThreeUsed() && streak.requiredKillStreak() <= killStreak) {
                            streak.runLogic(attacker);
                            scoreData.setKillSteakThreeUsed(true);
                        }
                    }
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
                    killStreakChecker(attacker);
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
                    killStreakChecker(attacker);
                } else Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.PLAYER_DIED.getComponent(new String[] { Lang.RED_COLOR.getString(null), player.getName() })));
                data.addPlayerDeath(player.getUniqueId());
                gameManager.respawnPlayer(player);
            }
            case SPECTATOR -> player.teleportAsync(map.getSpawn());
        }
    }

    private Player getLastAttacker(Entity entity) {
        Data data = Chaos.getPlugin().getData();
        if (entity instanceof Player player) {
            return player;
        } else if (entity instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player player) return player;
        } else if (entity instanceof TNTPrimed tnt) {
            if (data.hasEntityOwner(tnt.getUniqueId())) return Bukkit.getPlayer(data.getEntityOwner(tnt.getUniqueId()));
        } else if (entity instanceof EnderDragon enderDragon) {
            if (data.hasEntityOwner(enderDragon.getUniqueId())) return Bukkit.getPlayer(data.getEntityOwner(enderDragon.getUniqueId()));
        } else if (entity instanceof Creeper creeper) {
            if (data.hasEntityOwner(creeper.getUniqueId())) return Bukkit.getPlayer(data.getEntityOwner(creeper.getUniqueId()));
        }
        return null;
    }
}

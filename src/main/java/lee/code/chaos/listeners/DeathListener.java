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
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.UUID;

public class DeathListener implements Listener {

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
        } else if (e.getDamager() instanceof Player player) {
            UUID eUUID = e.getEntity().getUniqueId();
            if (data.hasEntityOwner(eUUID)) {
                UUID owner = data.getEntityOwner(eUUID);
                if (map.getTeam(player.getUniqueId()).equals(map.getTeam(owner))) e.setCancelled(true);
            }
        } else if (e.getDamager() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player player) {
                UUID eUUID = e.getEntity().getUniqueId();
                if (data.hasEntityOwner(eUUID)) {
                    UUID owner = data.getEntityOwner(eUUID);
                    if (map.getTeam(player.getUniqueId()).equals(map.getTeam(owner))) e.setCancelled(true);
                }
            }
        } else if (data.hasEntityOwner(e.getEntity().getUniqueId()) && data.hasEntityOwner(e.getDamager().getUniqueId())) {
            if (map.getTeam(data.getEntityOwner(e.getEntity().getUniqueId())).equals(map.getTeam(data.getEntityOwner(e.getDamager().getUniqueId())))) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onDeath(EntityDamageEvent e) {
        if (!e.isCancelled()) {
            Data data = Chaos.getPlugin().getData();
            MapData map = data.getActiveMap().getData();

            if (e.getEntity() instanceof Player player) {
                UUID uuid = player.getUniqueId();
                if (!map.getSpectators().contains(uuid)) {
                    GameTeam lastHitTeam = map.getTeam(data.getLastPlayerDamage(uuid));
                    if (lastHitTeam.equals(map.getTeam(uuid)) || lastHitTeam.equals(GameTeam.SPECTATOR)) {
                        data.removeLastPlayerDamage(uuid);
                        e.setCancelled(true);
                        return;
                    }
                    if (e.getDamage() >= player.getHealth() && !e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                        if (!player.getInventory().contains(new ItemStack(Material.TOTEM_OF_UNDYING))) {
                            e.setCancelled(true);
                            if (!map.isRespawningPlayer(uuid)) {
                                map.addRespawningPlayer(uuid);
                                respawnPlayer(player, Bukkit.getPlayer(data.getLastPlayerDamage(uuid)));
                                data.removeLastPlayerDamage(uuid);
                            }
                        }
                    } else if (e.getDamage() >= player.getHealth() && e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                        e.setCancelled(true);
                        if (!map.isRespawningPlayer(uuid)) {
                            map.addRespawningPlayer(uuid);
                            respawnPlayer(player, Bukkit.getPlayer(data.getLastPlayerDamage(uuid)));
                            data.removeLastPlayerDamage(uuid);
                        }
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

        MapData map = data.getActiveMap().getData();
        ScoreData scoreData = data.getPlayerScoreData(uuid);
        int killStreak = scoreData.getKillStreak();

        if (killStreak % 5 == 0) {
            Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.KILL_STREAK_MESSAGE.getComponent(new String[] { map.getColorChar(uuid), attacker.getName(), String.valueOf(killStreak) })));
        }

        if (cacheManager.hasSelectedKillStreak(uuid)) {
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
        UUID uuid = player.getUniqueId();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        GameManager gameManager = plugin.getGameManager();
        GameTeam team = map.getTeam(uuid);
        ScoreData scoreData = data.getPlayerScoreData(uuid);
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
                scoreData.setKillSteakOneUsed(false);
                scoreData.setKillSteakTwoUsed(false);
                scoreData.setKillSteakThreeUsed(false);
                data.addPlayerDeath(uuid);
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
                scoreData.setKillSteakOneUsed(false);
                scoreData.setKillSteakTwoUsed(false);
                scoreData.setKillSteakThreeUsed(false);
                data.addPlayerDeath(uuid);
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
        } else if (entity instanceof Horse horse) {
            if (data.hasEntityOwner(horse.getUniqueId())) return Bukkit.getPlayer(data.getEntityOwner(horse.getUniqueId()));
        } else if (entity instanceof LightningStrike lightningStrike) {
            if (data.hasEntityOwner(lightningStrike.getUniqueId())) return Bukkit.getPlayer(data.getEntityOwner(lightningStrike.getUniqueId()));
        }
        return null;
    }
}

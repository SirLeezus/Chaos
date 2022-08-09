package lee.code.chaos.managers;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.kits.Kit;
import lee.code.chaos.lists.*;
import lee.code.chaos.managers.board.BoardManager;
import lee.code.chaos.maps.MapData;
import lee.code.chaos.util.CountdownTimer;
import lee.code.core.util.bukkit.BukkitUtils;
import lee.code.permissions.PermissionsAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

import java.time.Duration;
import java.util.*;

public class GameManager {

    private int waitingTask;

    public void scheduleWaitingTask() {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        data.setGameState(GameState.WAITING);
        waitingTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            GameState gameState = data.getGameState();
            MapData map = data.getActiveMap().getData();

            if (gameState.equals(GameState.WAITING)) {
                Bukkit.getServer().sendActionBar(Lang.WAITING_WARNING.getComponent(null));
                if (map.getBlueTeam().size() > 0 && map.getRedTeam().size() > 0) {
                    startGame();
                }
            }
        }), 0L, 20L);
    }

    private void startGame() {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        if (data.getGameState().equals(GameState.WAITING)) {
            Bukkit.getScheduler().cancelTask(waitingTask);
            Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(1000), Duration.ofMillis(100));
            data.setGameState(GameState.STARTING);
            MapData map = data.getActiveMap().getData();
            CountdownTimer timer = new CountdownTimer(plugin,
                    10,
                    () -> {
                Bukkit.getServer().showTitle(Title.title(Lang.STARTING_TIMER_TITLE_PREPARE.getComponent(null), Component.text(""), times));
                    },
                    () -> {
                data.setGameState(GameState.ACTIVE);
                teleportTeamsSpawn();
                Bukkit.getServer().showTitle(Title.title(Lang.STARTING_TIMER_TITLE_FLIGHT.getComponent(null), Component.text(""), times));
                playPingSound(2);
                    },
                    (t) ->  {
                if (!map.getBlueTeam().isEmpty() && !map.getRedTeam().isEmpty()) {
                    Bukkit.getServer().showTitle(Title.title(Lang.STARTING_TIMER_TITLE.getComponent(new String[] { String.valueOf(t.getSecondsLeft()) }), Component.text(""), times));
                    playPingSound(1);
                } else {
                    scheduleWaitingTask();
                    t.stop();
                }
                    });
            timer.scheduleTimer();
        }
    }

    public void endGame() {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        data.setGameState(GameState.TRANSITIONING);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
        }

        List<UUID> winners = new ArrayList<>();
        //winner
        if (map.getBlueScore() > map.getRedScore() && !map.getBlueTeam().isEmpty()) {
            winners.addAll(map.getBlueTeam());
            Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.GAME_WINNER.getComponent(new String[] { Lang.BLUE_TEAM.getString(null) })));
        } else if (map.getRedScore() > map.getBlueScore() && !map.getRedTeam().isEmpty()) {
            winners.addAll(map.getRedTeam());
            Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.GAME_WINNER.getComponent(new String[] { Lang.RED_TEAM.getString(null) })));
        } else if (!map.getBlueTeam().isEmpty()) {
            winners.addAll(map.getBlueTeam());
            Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.GAME_WINNER.getComponent(new String[] { Lang.BLUE_TEAM.getString(null) })));
        } else if (!map.getRedTeam().isEmpty()) {
            winners.addAll(map.getRedTeam());
            Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.GAME_WINNER.getComponent(new String[] { Lang.RED_TEAM.getString(null) })));
        } else {
            Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.GAME_END_DRAW.getComponent(null)));
        }

        CountdownTimer timer = new CountdownTimer(plugin,
                20,
                () -> Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.CYCLING_MAP_STARTED.getComponent(null))),
                () -> data.loadNextMap(false),
                (t) ->  {
                    Bukkit.getServer().sendActionBar(Lang.CYCLING_MAP.getComponent(new String[] { String.valueOf(t.getSecondsLeft()) }));
                    if (!winners.isEmpty()) {
                        for (UUID winner : winners) {
                            Player player = Bukkit.getPlayer(winner);
                            if (player != null && player.isOnline()) {
                                spawnFirework(player.getLocation());
                            }
                        }
                    }
                });
        timer.scheduleTimer();
    }

    private void playPingSound(int pitch) {
        Sound sound = Sound.sound(Key.key("block.note_block.pling"), Sound.Source.PLAYER, 1f, (float) pitch);
        Bukkit.getServer().playSound(sound, Sound.Emitter.self());
    }

    public void loadPlayerDefaults(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) player.removePotionEffect(potionEffect.getType());
        loadKit(player);
        loadArmor(player);
    }

    public void loadSpectatorDefaults(Player player) {
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setFireTicks(0);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) player.removePotionEffect(potionEffect.getType());
        loadSpectatorItems(player);
    }

    private void loadSpectatorItems(Player player) {
        player.getInventory().setItem(2, SpectatorItem.KILL_STREAK_SELECTOR.getItem());
        player.getInventory().setItem(4, SpectatorItem.TEAM_SELECTOR.getItem());
        player.getInventory().setItem(6, SpectatorItem.KIT_SELECTOR.getItem());
        player.updateInventory();
    }

    public void respawnPlayer(Player player) {
        MapData map = Chaos.getPlugin().getData().getActiveMap().getData();
        UUID uuid = player.getUniqueId();
        for (ItemStack drop : player.getInventory()) if (drop != null) {
            if (drop.getType().equals(Material.LEATHER_HELMET) || drop.getType().equals(Material.LEATHER_CHESTPLATE) || drop.getType().equals(Material.LEATHER_LEGGINGS) || drop.getType().equals(Material.LEATHER_BOOTS)) continue;
            player.getInventory().remove(drop);
            player.getWorld().dropItemNaturally(player.getLocation(), drop);
        }
        player.getWorld().dropItemNaturally(player.getLocation(), player.getInventory().getItemInOffHand());
        player.getInventory().getItemInOffHand().setType(Material.AIR);
        switch (map.getTeam(uuid)) {
            case BLUE -> player.teleportAsync(map.getBlueSpawn()).thenAccept(result -> {
                loadArmor(player);
                loadKit(player);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setFireTicks(0);
                for (PotionEffect potionEffect : player.getActivePotionEffects()) player.removePotionEffect(potionEffect.getType());
                map.removeRespawningPlayer(uuid);
            });
            case RED -> player.teleportAsync(map.getRedSpawn()).thenAccept(result -> {
                loadArmor(player);
                loadKit(player);
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setFireTicks(0);
                for (PotionEffect potionEffect : player.getActivePotionEffects()) player.removePotionEffect(potionEffect.getType());
                map.removeRespawningPlayer(uuid);
            });
        }
    }

    public void teleportPlayerSpawn(Player player) {
        MapData map = Chaos.getPlugin().getData().getActiveMap().getData();
        UUID uuid = player.getUniqueId();
        map.removeSpectator(uuid);
        switch (map.getTeam(uuid)) {
            case BLUE -> player.teleportAsync(map.getBlueSpawn()).thenAccept(result -> {
                loadPlayerDefaults(player);
                showSpectator(player);
                hideSpectatorFromPlayer(player);
            });
            case RED -> player.teleportAsync(map.getRedSpawn()).thenAccept(result -> {
                loadPlayerDefaults(player);
                showSpectator(player);
                hideSpectatorFromPlayer(player);
            });
        }
    }

    public void teleportTeamsSpawn() {
        MapData map = Chaos.getPlugin().getData().getActiveMap().getData();
        //red
        for (UUID uuid : map.getRedTeam()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                map.removeSpectator(uuid);
                player.teleportAsync(map.getRedSpawn()).thenAccept(result -> {
                    loadPlayerDefaults(player);
                    showSpectator(player);
                    hideSpectatorFromPlayer(player);
                });
            }
        }
        //blue
        for (UUID uuid : map.getBlueTeam()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                map.removeSpectator(uuid);
                player.teleportAsync(map.getBlueSpawn()).thenAccept(result -> {
                    loadPlayerDefaults(player);
                    showSpectator(player);
                    hideSpectatorFromPlayer(player);
                });
            }
        }
    }

    public void teleportServerSpawn() {
        Chaos plugin = Chaos.getPlugin();
        MapData map = plugin.getData().getActiveMap().getData();
        CacheManager cacheManager = plugin.getCacheManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = player.getUniqueId();
            map.addSpectator(player.getUniqueId());
            player.teleportAsync(map.getSpawn()).thenAccept(result -> {
                loadSpectatorDefaults(player);
                showSpectators(player);
                cacheManager.setGamesPlayed(uuid, cacheManager.getGamesPlayed(uuid) + 1);
                plugin.getGameManager().updateDisplayName(player, GameTeam.SPECTATOR, false);
            });
        }
    }

    private void spawnFirework(Location location) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder()
                .withColor(Color.RED)
                .withColor(Color.ORANGE)
                .withColor(Color.YELLOW)
                .withColor(Color.LIME)
                .withColor(Color.NAVY)
                .withColor(Color.PURPLE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .flicker(true)
                .build());
        fw.setFireworkMeta(fwm);
    }

    private void loadKit(Player player) {
        Kit kit = Chaos.getPlugin().getCacheManager().getKit(player.getUniqueId());
        player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        for (Map.Entry<Integer, ItemStack> kItem : kit.inventory().entrySet()) {
            int slot = kItem.getKey();
            ItemStack item = kItem.getValue();
            if (item.getType().equals(Material.SHIELD)) player.getInventory().setItemInOffHand(item);
            else player.getInventory().setItem(slot, item);
        }
        player.updateInventory();
    }

    private void loadArmor(Player player) {
        MapData map = Chaos.getPlugin().getData().getActiveMap().getData();
        Color color = map.getTeam(player.getUniqueId()).equals(GameTeam.RED) ? Color.RED : Color.BLUE;
        //helmet
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(color);
        helmetMeta.setUnbreakable(true);
        helmet.setItemMeta(helmetMeta);
        player.getInventory().setHelmet(helmet);

        //chest
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        chestMeta.setColor(color);
        chestMeta.setUnbreakable(true);
        chest.setItemMeta(chestMeta);
        player.getInventory().setChestplate(chest);

        //leggings
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(color);
        leggingsMeta.setUnbreakable(true);
        leggings.setItemMeta(leggingsMeta);
        player.getInventory().setLeggings(leggings);

        //boots
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(color);
        bootsMeta.setUnbreakable(true);
        boots.setItemMeta(bootsMeta);
        player.getInventory().setBoots(boots);
    }

    public void hideSpectatorFromPlayer(Player player) {
        Chaos plugin = Chaos.getPlugin();
        MapData map = plugin.getData().getActiveMap().getData();
        for (UUID uuid : map.getSpectators()) {
            Player oPlayer = Bukkit.getPlayer(uuid);
            if (oPlayer != null) player.hidePlayer(plugin, oPlayer);
        }
    }

    public void showSpectators(Player player) {
        Chaos plugin = Chaos.getPlugin();
        MapData map = plugin.getData().getActiveMap().getData();
        for (UUID uuid : map.getSpectators()) {
            Player oPlayer = Bukkit.getPlayer(uuid);
            if (oPlayer != null) oPlayer.showPlayer(plugin, player);
        }
    }

    public void hideSpectator(Player player) {
        Chaos plugin = Chaos.getPlugin();
        MapData map = plugin.getData().getActiveMap().getData();
        for (UUID uuid : map.getBlueTeam()) {
            Player oPlayer = Bukkit.getPlayer(uuid);
            if (oPlayer != null) oPlayer.hidePlayer(plugin, player);
        }
        for (UUID uuid : map.getRedTeam()) {
            Player oPlayer = Bukkit.getPlayer(uuid);
            if (oPlayer != null) oPlayer.hidePlayer(plugin, player);
        }
    }

    public void showSpectator(Player player) {
        Chaos plugin = Chaos.getPlugin();
        MapData map = plugin.getData().getActiveMap().getData();
        for (UUID uuid : map.getBlueTeam()) {
            Player oPlayer = Bukkit.getPlayer(uuid);
            if (oPlayer != null) oPlayer.showPlayer(plugin, player);
        }
        for (UUID uuid : map.getRedTeam()) {
            Player oPlayer = Bukkit.getPlayer(uuid);
            if (oPlayer != null) oPlayer.showPlayer(plugin, player);
        }
    }

    public void updateDisplayName(Player player) {
        GameTeam team = Chaos.getPlugin().getData().getActiveMap().getData().getTeam(player.getUniqueId());
        updateDisplayName(player, team, false);
    }

    public void updateDisplayName(Player player, GameTeam team, boolean delayed) {
        UUID uuid = player.getUniqueId();
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        BoardManager boardManager = data.hasBoard(uuid) ? data.getBoardPacket(uuid) : new BoardManager(uuid);
        String level = plugin.getCacheManager().getDisplayLevel(uuid);
        switch (team) {
            case RED -> {
                boardManager.setTeamName("R" + data.getTeamNumber());
                boardManager.setColor(ChatColor.RED);
                String prefix = "&6[&a" + level + "&6] &c";
                if (PermissionsAPI.hasRank(uuid)) prefix = prefix + Rank.valueOf(PermissionsAPI.getRank(uuid)).getPrefix() + " &c";
                player.displayName(BukkitUtils.parseColorComponent( prefix + player.getName()));
                player.playerListName(BukkitUtils.parseColorComponent( prefix + player.getName()));
            }
            case BLUE -> {
                boardManager.setTeamName("B" + data.getTeamNumber());
                boardManager.setColor(ChatColor.BLUE);
                String prefix = "&6[&a" + level + "&6] &9";
                if (PermissionsAPI.hasRank(uuid)) prefix = prefix + Rank.valueOf(PermissionsAPI.getRank(uuid)).getPrefix() + " &9";
                player.displayName(BukkitUtils.parseColorComponent( prefix + player.getName()));
                player.playerListName(BukkitUtils.parseColorComponent( prefix + player.getName()));
            }
            case SPECTATOR -> {
                boardManager.setTeamName("S" + data.getTeamNumber());
                boardManager.setColor(ChatColor.YELLOW);
                String prefix = "&6[&a" + level + "&6] &e";
                if (PermissionsAPI.hasRank(uuid)) prefix = prefix + Rank.valueOf(PermissionsAPI.getRank(uuid)).getPrefix() + " &e";
                player.displayName(BukkitUtils.parseColorComponent(prefix + player.getName()));
                player.playerListName(BukkitUtils.parseColorComponent( prefix + player.getName()));
            }
        }
        boardManager.setCollisionRule(CollisionRule.NEVER);
        data.setTeamNumber(data.getTeamNumber() + 1);
        String prefix = "&6[&a" + level + "&6] ";
        if (PermissionsAPI.hasRank(uuid)) prefix = prefix + Rank.valueOf(PermissionsAPI.getRank(uuid)).getPrefix() + " ";
        boardManager.setPrefix(WrappedChatComponent.fromJson(BukkitUtils.serializeColorComponentJson(prefix)));
        boardManager.setPlayers(Collections.singletonList(player.getName()));
        data.setBoardPacket(uuid, boardManager);
        if (delayed) {
            Bukkit.getServer().getScheduler().runTaskLater(plugin, () -> {
                boardManager.broadcastPacket();
                boardManager.sendSidebarPacket();
            }, 20L);
        } else {
            boardManager.broadcastPacket();
            boardManager.sendSidebarPacket();
        }
    }
}

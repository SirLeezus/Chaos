package lee.code.chaos;

import lee.code.chaos.database.CacheManager;
import lee.code.chaos.kits.Kit;
import lee.code.chaos.kits.kit.Default;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.managers.GameManager;
import lee.code.chaos.managers.WorldManager;
import lee.code.chaos.managers.board.BoardManager;
import lee.code.chaos.maps.Map;
import lee.code.chaos.maps.MapData;
import lee.code.chaos.maps.ScoreData;
import lee.code.chaos.maps.map.EgyptianIslands;
import lee.code.chaos.maps.map.ForgottenTowers;
import lee.code.chaos.maps.map.IceAge;
import lee.code.chaos.maps.map.LastDestination;
import lee.code.chaos.menusystem.PlayerMU;
import lee.code.core.util.bukkit.BukkitUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Data {

    private int deleteMapTask;
    @Getter private int mapID;
    @Getter private final List<Map> maps = new ArrayList<>();
    @Getter private Map activeMap;
    @Setter @Getter private GameState gameState;
    @Getter @Setter private int teamNumber = 0;

    private final ConcurrentHashMap<UUID, PlayerMU> playerMUList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, BoardManager> activeBoardPackets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Double> playerHeathTracker = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, UUID> lastPlayerDamage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Kit> gameKits = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Kit> selectedKit = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ScoreData> playerScore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, UUID> lastReplier = new ConcurrentHashMap<>();

    public void setBoardPacket(UUID player, BoardManager boardManager) { activeBoardPackets.put(player, boardManager); }
    public BoardManager getBoardPacket(UUID player) {
        return activeBoardPackets.get(player);
    }
    public List<BoardManager> getBoardPackets() { return activeBoardPackets.values().stream().toList();}
    public void removeBoard(UUID uuid) { activeBoardPackets.get(uuid); }
    public boolean hasBoard(UUID uuid) { return activeBoardPackets.containsKey(uuid); }

    public void setLastPlayerDamage(UUID uuid, UUID attacker) { lastPlayerDamage.put(uuid, attacker);}
    public UUID getLastPlayerDamage(UUID uuid) { return lastPlayerDamage.getOrDefault(uuid, null); }
    public void removeLastPlayerDamage(UUID uuid) { lastPlayerDamage.remove(uuid); }

    public void setSelectedKit(UUID uuid, Kit kit) { selectedKit.put(uuid, kit);}
    public Kit getSelectedKit(UUID uuid) { return selectedKit.getOrDefault(uuid, gameKits.get("default")); }
    public void removeSelectedKit(UUID uuid) { selectedKit.remove(uuid); }

    public UUID getLastReplier(UUID uuid) { return lastReplier.get(uuid); }
    public void setLastReplier(UUID player, UUID target) { lastReplier.put(player, target); }
    public boolean hasReplier(UUID uuid) { return lastReplier.containsKey(uuid); }

    public ScoreData getPlayerScoreData(UUID uuid) {
        if (playerScore.containsKey(uuid)) return playerScore.get(uuid);
        playerScore.put(uuid, new ScoreData(uuid));
        return playerScore.get(uuid);
    }
    public void addPlayerKill(UUID uuid) {
        CacheManager cacheManager = Chaos.getPlugin().getCacheManager();
        ScoreData scoreData = getPlayerScoreData(uuid);
        scoreData.setKills(scoreData.getKills() + 1);
        scoreData.setKillStreak(scoreData.getKillStreak() + 1);
        cacheManager.setKills(uuid, cacheManager.getKills(uuid) + 1);
        cacheManager.setLevel(uuid, cacheManager.getLevel(uuid) + 200);
        cacheManager.setCoins(uuid, cacheManager.getCoins(uuid) + 5);
        if (scoreData.getKillStreak() > cacheManager.getLongestKillStreak(uuid)) cacheManager.setLongestKillStreak(uuid, scoreData.getKillStreak());
        activeBoardPackets.get(uuid).sendSidebarPacket();
    }
    public void addPlayerDeath(UUID uuid) {
        CacheManager cacheManager = Chaos.getPlugin().getCacheManager();
        ScoreData scoreData = getPlayerScoreData(uuid);
        scoreData.setDeaths(scoreData.getDeaths() + 1);
        scoreData.setKillStreak(0);
        cacheManager.setDeaths(uuid, cacheManager.getDeaths(uuid) + 1);
        activeBoardPackets.get(uuid).sendSidebarPacket();
    }
    public void resetPlayerScores() { playerScore.clear(); }
    public void addBluePoint(UUID uuid) {
        CacheManager cacheManager = Chaos.getPlugin().getCacheManager();
        activeMap.getData().setBlueScore(activeMap.getData().getBlueScore() + 1);
        cacheManager.setWoolBroken(uuid, cacheManager.getWoolBroken(uuid) + 1);
        cacheManager.setLevel(uuid, cacheManager.getLevel(uuid) + 500);
        cacheManager.setCoins(uuid, cacheManager.getCoins(uuid) + 10);
        activeBoardPackets.get(uuid).broadcastSidebarPacket();
    }
    public void addRedPoint(UUID uuid) {
        CacheManager cacheManager = Chaos.getPlugin().getCacheManager();
        activeMap.getData().setRedScore(activeMap.getData().getRedScore() + 1);
        cacheManager.setWoolBroken(uuid, cacheManager.getWoolBroken(uuid) + 1);
        cacheManager.setLevel(uuid, cacheManager.getLevel(uuid) + 500);
        cacheManager.setCoins(uuid, cacheManager.getCoins(uuid) + 10);
        activeBoardPackets.get(uuid).broadcastSidebarPacket();
    }

    public void setHeathTracker(UUID uuid, double amount) { playerHeathTracker.put(uuid, amount); }
    public double getHeathTracker(UUID uuid) {
        if (!playerHeathTracker.containsKey(uuid)) playerHeathTracker.put(uuid, 0.0);
        return playerHeathTracker.get(uuid);
    }
    public void removeHeathTracker(UUID uuid) { playerHeathTracker.remove(uuid); }

    public PlayerMU getPlayerMU(UUID uuid) {
        if (playerMUList.containsKey(uuid)) {
            return playerMUList.get(uuid);
        } else {
            PlayerMU pmu = new PlayerMU(uuid);
            playerMUList.put(uuid, pmu);
            return pmu;
        }
    }

    public void load() {
        loadData();
        loadNextMap(true);
        scheduleTabListUpdater();
        scheduleHeathChecker();
    }

    private void loadData() {
        maps.add(new ForgottenTowers());
        maps.add(new IceAge());
        maps.add(new EgyptianIslands());
        maps.add(new LastDestination());

        Kit defaultKit = new Default();
        gameKits.put(defaultKit.name(), defaultKit);
    }

    public int getNextMap() {
        int amount = mapID;
        mapID++;
        if (mapID > maps.size() - 1) mapID = 0;
        return amount;
    }

    public void loadNextMap(boolean firstMap) {
        Chaos plugin = Chaos.getPlugin();
        GameManager gameManager = plugin.getGameManager();
        WorldManager worldManager = plugin.getWorldManager();
        if (firstMap) {
            activeMap = maps.get(getNextMap());
            worldManager.createMap(activeMap);
            MapData mapData = activeMap.getData();
            mapData.setSpawn(BukkitUtils.parseLocation(activeMap.spawn()));
            mapData.setBlueSpawn(BukkitUtils.parseLocation(activeMap.blueTeamSpawn()));
            mapData.setRedSpawn(BukkitUtils.parseLocation(activeMap.redTeamSpawn()));
            gameState = GameState.WAITING;
            gameManager.scheduleWaitingTask();
        } else {
            scheduleDeleteMap(activeMap.name());
            activeMap.resetData();
            resetPlayerScores();
            activeMap = maps.get(getNextMap());
            worldManager.createMap(activeMap);
            MapData mapData = activeMap.getData();
            mapData.setSpawn(BukkitUtils.parseLocation(activeMap.spawn()));
            mapData.setBlueSpawn(BukkitUtils.parseLocation(activeMap.blueTeamSpawn()));
            mapData.setRedSpawn(BukkitUtils.parseLocation(activeMap.redTeamSpawn()));
            gameState = GameState.WAITING;
            gameManager.teleportServerSpawn();
            gameManager.scheduleWaitingTask();
            lastPlayerDamage.clear();
        }
    }

    private void scheduleDeleteMap(String name) {
        Chaos plugin = Chaos.getPlugin();
        deleteMapTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            World targetWord = Bukkit.getWorld(name);
            if (targetWord != null) {
                if (targetWord.getPlayers().isEmpty()) {
                    deleteMap(name);
                }
            }
        }), 0L, 20L);
    }

    private void deleteMap(String name) {
        Chaos plugin = Chaos.getPlugin();
        Bukkit.getScheduler().cancelTask(deleteMapTask);
        World world = Bukkit.getWorld(name);
        if (world != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getServer().unloadWorld(world, false);
                plugin.getWorldManager().deleteWorldFolder(new File("./" + name));
            },100L);
        }
    }

    private void scheduleTabListUpdater() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Chaos.getPlugin(), () -> {
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                Bukkit.getServer().sendPlayerListHeaderAndFooter(Lang.TABLIST_HEADER.getComponent(null), Lang.TABLIST_FOOTER.getComponent(new String[] { String.valueOf(BukkitUtils.getOnlinePlayers().size()) }));
            }
        }, 10, 40);
    }

    private void scheduleHeathChecker() {
        Chaos plugin = Chaos.getPlugin();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    double health = player.getAbsorptionAmount() + player.getHealth();
                    if (getHeathTracker(uuid) != health && hasBoard(uuid)) {
                        setHeathTracker(uuid, health);
                        getBoardPacket(uuid).updateHeath();
                    }
                }
            }
        }), 1L, 1L);
    }
}

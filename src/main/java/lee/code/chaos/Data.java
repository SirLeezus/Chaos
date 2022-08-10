package lee.code.chaos;

import lee.code.chaos.database.CacheManager;
import lee.code.chaos.killstreaks.KillStreak;
import lee.code.chaos.killstreaks.killstreak.*;
import lee.code.chaos.kits.Kit;
import lee.code.chaos.kits.kit.*;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.lists.Rank;
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
import lee.code.chaos.recipes.Tool;
import lee.code.chaos.recipes.CraftingRecipe;
import lee.code.core.util.bukkit.BukkitUtils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.bukkit.Bukkit.getServer;

public class Data {

    private int deleteMapTask;
    @Getter private int mapID;
    @Getter private final List<Map> maps = new ArrayList<>();
    @Getter private Map activeMap;
    @Setter @Getter private GameState gameState;
    @Getter @Setter private int teamNumber = 0;

    @Getter private final List<NamespacedKey> recipeKeys = new ArrayList<>();
    @Getter private final List<String> rankKeys = new ArrayList<>();
    @Getter private final List<String> premiumRankKeys = new ArrayList<>();
    @Getter private final List<Component> motd = new ArrayList<>();

    private final ConcurrentHashMap<Location, UUID> blockOwner = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, UUID> entityOwner = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, PlayerMU> playerMUList = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, BoardManager> activeBoardPackets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, Double> playerHeathTracker = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, UUID> lastPlayerDamage = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ScoreData> playerScore = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, UUID> lastReplier = new ConcurrentHashMap<>();
    private final LinkedHashMap<String, Kit> gameKits = new LinkedHashMap<>();
    private final LinkedHashMap<String, KillStreak> gameKillStreaks = new LinkedHashMap<>();
    private final ConcurrentHashMap<UUID, Integer> teamMenuTask = new ConcurrentHashMap<>();

    public void setBlockOwner(Location location, UUID uuid) {
        blockOwner.put(location, uuid);
    }
    public UUID getBlockOwner(Location location) {
        return blockOwner.get(location);
    }
    public void removeBlockOwner(Location location) {
        blockOwner.remove(location);
    }
    public boolean hasBlockOwner(Location location) {
        return blockOwner.containsKey(location);
    }

    public int getTeamMenuTask(UUID uuid) { return teamMenuTask.get(uuid); }
    public void setTeamMenuTask(UUID uuid, int task) { teamMenuTask.put(uuid, task); }
    public void removeTeamMenuTask(UUID uuid) { teamMenuTask.remove(uuid); }
    public boolean hasTeamMenuTask(UUID uuid) { return teamMenuTask.containsKey(uuid); }

    public void setEntityOwner(UUID entity, UUID owner) {
        entityOwner.put(entity, owner);
    }
    public UUID getEntityOwner(UUID entity) {
        return entityOwner.get(entity);
    }
    public void removeEntityOwner(UUID entity) {
        entityOwner.remove(entity);
    }
    public boolean hasEntityOwner(UUID entity) {
        return entityOwner.containsKey(entity);
    }

    public void setBoardPacket(UUID player, BoardManager boardManager) { activeBoardPackets.put(player, boardManager); }
    public BoardManager getBoardPacket(UUID player) {
        return activeBoardPackets.get(player);
    }
    public List<BoardManager> getBoardPackets() { return activeBoardPackets.values().stream().toList();}
    public void removeBoard(UUID uuid) { activeBoardPackets.get(uuid); }
    public boolean hasBoard(UUID uuid) { return activeBoardPackets.containsKey(uuid); }

    public void setLastPlayerDamage(UUID uuid, UUID attacker) { lastPlayerDamage.put(uuid, attacker);}
    public UUID getLastPlayerDamage(UUID uuid) { return lastPlayerDamage.getOrDefault(uuid, UUID.fromString(Lang.SERVER_UUID.getString(null))); }
    public void removeLastPlayerDamage(UUID uuid) { lastPlayerDamage.remove(uuid); }

    public LinkedList<Kit> getKits() { return new LinkedList<>(gameKits.values()); }
    public Kit getKit(String kit) { return gameKits.get(kit); }

    public LinkedList<KillStreak> getKillStreaks() { return new LinkedList<>(gameKillStreaks.values()); }
    public KillStreak getKillStreak(String killStreak) { return gameKillStreaks.get(killStreak); }

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
        long coins = cacheManager.getCoins(uuid) + 5;
        if (cacheManager.isBoosterActive()) {
            int id = cacheManager.getActiveBoosterID();
            int multiplier = cacheManager.getBoosterMultiplier(id);
            coins = coins - 5 + (5L * multiplier);
        }
        ScoreData scoreData = getPlayerScoreData(uuid);
        scoreData.setKills(scoreData.getKills() + 1);
        scoreData.setKillStreak(scoreData.getKillStreak() + 1);
        cacheManager.setKills(uuid, cacheManager.getKills(uuid) + 1);
        cacheManager.setLevel(uuid, cacheManager.getLevel(uuid) + 200);
        cacheManager.setCoins(uuid, coins);
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
        long coins = cacheManager.getCoins(uuid) + 10;
        if (cacheManager.isBoosterActive()) {
            int id = cacheManager.getActiveBoosterID();
            int multiplier = cacheManager.getBoosterMultiplier(id);
            coins = coins - 10 + (10L * multiplier);
        }
        activeMap.getData().setBlueScore(activeMap.getData().getBlueScore() + 1);
        cacheManager.setWoolBroken(uuid, cacheManager.getWoolBroken(uuid) + 1);
        cacheManager.setLevel(uuid, cacheManager.getLevel(uuid) + 500);
        cacheManager.setCoins(uuid, coins);
        activeBoardPackets.get(uuid).broadcastSidebarPacket();
    }
    public void addRedPoint(UUID uuid) {
        CacheManager cacheManager = Chaos.getPlugin().getCacheManager();
        long coins = cacheManager.getCoins(uuid) + 10;
        if (cacheManager.isBoosterActive()) {
            int id = cacheManager.getActiveBoosterID();
            int multiplier = cacheManager.getBoosterMultiplier(id);
            coins = coins - 10 + (10L * multiplier);
        }
        activeMap.getData().setRedScore(activeMap.getData().getRedScore() + 1);
        cacheManager.setWoolBroken(uuid, cacheManager.getWoolBroken(uuid) + 1);
        cacheManager.setLevel(uuid, cacheManager.getLevel(uuid) + 500);
        cacheManager.setCoins(uuid, coins);
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

        //rank keys
        rankKeys.addAll(EnumSet.allOf(Rank.class).stream().map(Rank::name).toList());
        for (Rank rank : Rank.values()) if (rank.getPriority() > 0) premiumRankKeys.add(rank.name());

        //motd
        motd.add(Lang.MOTD_SPLITTER.getComponent(null));
        motd.add(Component.text(""));
        motd.add(Lang.MOTD_TITLE.getComponent(null));
        motd.add(Component.text(""));
        motd.add(Lang.DISCORD.getComponent(null).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, Lang.DISCORD_LINK.getString(null))));
        motd.add(Lang.STORE.getComponent(null).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, Lang.STORE_LINK.getString(null))));
        motd.add(Component.text(""));
        motd.add(Lang.MOTD_SPLITTER.getComponent(null));

        //remove recipes
        List<Material> removedRecipes = new ArrayList<>(EnumSet.allOf(Tool.class).stream().map(Tool::getMaterial).toList());
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        Recipe recipe;
        while (it.hasNext()) {
            recipe = it.next();
            if (recipe != null) {
                Material material = recipe.getResult().getType();
                if (removedRecipes.contains(material) || material.equals(Material.RED_WOOL) || material.equals(Material.BLUE_WOOL) || material.equals(Material.ENDER_CHEST)) {
                    it.remove();
                }
            }
        }

        //custom recipes
        for (String sRecipe : EnumSet.allOf(CraftingRecipe.class).stream().map(CraftingRecipe::name).toList()) {
            CraftingRecipe.valueOf(sRecipe).registerRecipe();
        }

        Iterator<Recipe> ita = Bukkit.getServer().recipeIterator();
        ita.forEachRemaining(dRecipe -> {
            if (dRecipe instanceof ShapelessRecipe shapelessRecipe) recipeKeys.add(shapelessRecipe.getKey());
            else if (dRecipe instanceof ShapedRecipe shapedRecipe) recipeKeys.add(shapedRecipe.getKey());
            else if (dRecipe instanceof BlastingRecipe shapedRecipe) recipeKeys.add(shapedRecipe.getKey());
            else if (dRecipe instanceof CampfireRecipe shapedRecipe) recipeKeys.add(shapedRecipe.getKey());
            else if (dRecipe instanceof FurnaceRecipe shapedRecipe) recipeKeys.add(shapedRecipe.getKey());
            else if (dRecipe instanceof SmithingRecipe shapedRecipe) recipeKeys.add(shapedRecipe.getKey());
            else if (dRecipe instanceof SmokingRecipe shapedRecipe) recipeKeys.add(shapedRecipe.getKey());
            else if (dRecipe instanceof StonecuttingRecipe shapedRecipe) recipeKeys.add(shapedRecipe.getKey());
        });
    }

    private void loadData() {
        maps.add(new ForgottenTowers());
        maps.add(new IceAge());
        maps.add(new EgyptianIslands());
        maps.add(new LastDestination());

        //kits
        Kit defaultKit = new Default();
        gameKits.put(defaultKit.name(), defaultKit);
        Kit constructionWorker = new ConstructionWorker();
        gameKits.put(constructionWorker.name(), constructionWorker);
        Kit heavyHitter = new HeavyHitter();
        gameKits.put(heavyHitter.name(), heavyHitter);
        Kit noMercy = new NoMercy();
        gameKits.put(noMercy.name(), noMercy);
        Kit wonderwall = new Wonderwall();
        gameKits.put(wonderwall.name(), wonderwall);
        Kit deepDive = new DeepDive();
        gameKits.put(deepDive.name(), deepDive);
        Kit engineer = new Engineer();
        gameKits.put(engineer.name(), engineer);
        Kit hadouken = new Hadouken();
        gameKits.put(hadouken.name(), hadouken);
        Kit tripleShot = new TripleShot();
        gameKits.put(tripleShot.name(), tripleShot);
        Kit bullseye = new Bullseye();
        gameKits.put(bullseye.name(), bullseye);
        Kit enchanter = new Enchanter();
        gameKits.put(enchanter.name(), enchanter);
        Kit demolitionist = new Demolitionist();
        gameKits.put(demolitionist.name(), demolitionist);
        //kill streaks
        KillStreak secondChance = new SecondChance();
        gameKillStreaks.put(secondChance.name(), secondChance);
        KillStreak homeRun = new HomeRun();
        gameKillStreaks.put(homeRun.name(), homeRun);
        KillStreak juiced = new Juiced();
        gameKillStreaks.put(juiced.name(), juiced);
        KillStreak fireChargeRestock = new FireChargeRestock();
        gameKillStreaks.put(fireChargeRestock.name(), fireChargeRestock);
        KillStreak explosiveRestock = new ExplosiveRestock();
        gameKillStreaks.put(explosiveRestock.name(), explosiveRestock);
        KillStreak eyesInTheSky = new EyesInTheSky();
        gameKillStreaks.put(eyesInTheSky.name(), eyesInTheSky);
        KillStreak trustySteed = new TrustySteed();
        gameKillStreaks.put(trustySteed.name(), trustySteed);
        KillStreak flashStorm = new FlashStorm();
        gameKillStreaks.put(flashStorm.name(), flashStorm);
        KillStreak cruelDragon = new CruelDragon();
        gameKillStreaks.put(cruelDragon.name(), cruelDragon);
        KillStreak rainingCreepers = new RainingCreepers();
        gameKillStreaks.put(rainingCreepers.name(), rainingCreepers);
        KillStreak nuke = new Nuke();
        gameKillStreaks.put(nuke.name(), nuke);
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
            entityOwner.clear();
            blockOwner.clear();
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
                getServer().unloadWorld(world, false);
                plugin.getWorldManager().deleteWorldFolder(new File("./" + name));
            },100L);
        }
    }

    private void scheduleTabListUpdater() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Chaos.getPlugin(), () -> {
            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                MapData map = Chaos.getPlugin().getData().getActiveMap().getData();
                getServer().sendPlayerListHeaderAndFooter(Lang.TABLIST_HEADER.getComponent(null), Lang.TABLIST_FOOTER.getComponent(new String[] { String.valueOf(map.getRedTeam().size()), String.valueOf(map.getBlueTeam().size()), String.valueOf(map.getSpectators().size()) }));
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

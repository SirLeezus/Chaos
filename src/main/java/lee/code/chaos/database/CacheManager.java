package lee.code.chaos.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lee.code.chaos.Chaos;
import lee.code.chaos.PU;
import lee.code.chaos.database.tables.BoosterTable;
import lee.code.chaos.database.tables.PlayerTable;
import lee.code.chaos.killstreaks.KillStreak;
import lee.code.chaos.kits.Kit;
import lee.code.core.util.bukkit.BukkitUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CacheManager {

    @Getter
    private final Cache<UUID, PlayerTable> playerCache = CacheBuilder
            .newBuilder()
            .initialCapacity(5000)
            .recordStats()
            .build();

    @Getter
    private final Cache<Integer, BoosterTable> boosterCache = CacheBuilder
            .newBuilder()
            .initialCapacity(5000)
            .recordStats()
            .build();

    //boosters

    public void setBoosterData(BoosterTable boosterTable) {
        getBoosterCache().put(boosterTable.getId(), boosterTable);
    }

    private BoosterTable getBoosterTable(int id) {
        return getBoosterCache().getIfPresent(id);
    }

    private void updateBoosterTable(BoosterTable boosterTable) {
        getBoosterCache().put(boosterTable.getId(), boosterTable);
        Chaos.getPlugin().getDatabaseManager().updateBoosterTable(boosterTable);
    }

    public void createBoosterData(int id, UUID uuid, int multiplier, long time, boolean active, long duration) {
        BoosterTable boosterTable = new BoosterTable(id, uuid, multiplier, time, active, duration);
        getBoosterCache().put(boosterTable.getId(), boosterTable);
        Chaos.getPlugin().getDatabaseManager().createBoosterTable(boosterTable);
    }

    public void queueBooster(UUID uuid, int multiplier, long duration) {
        createBoosterData(getNextBoosterID(), uuid, multiplier, 0, false, duration);
    }

    private int getNextBoosterID() {
        if (getBoosterCache().asMap().isEmpty()) return 1;
        else return Collections.max(getBoosterCache().asMap().keySet()) + 1;
    }

    public boolean areBoosters() {
        return !getBoosterCache().asMap().isEmpty();
    }

    public boolean isBoosterActive() {
        return getBoosterCache().asMap().values().stream().anyMatch(BoosterTable::isActive);
    }
    public int getNextBoosterQueueID() {
        return Collections.min(getBoosterCache().asMap().keySet());
    }

    public List<Integer> getBoosterIDList() {
        return new ArrayList<>(getBoosterCache().asMap().keySet());
    }

    public List<String> getBoosterIDStringList() {
        return getBoosterCache().asMap().keySet().stream().map(String::valueOf).sorted().collect(Collectors.toList());
    }

    public long getBoosterTime(int id) {
        return getBoosterTable(id).getTime() - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public int getBoosterMultiplier(int id) {
        return getBoosterTable(id).getMultiplier();
    }

    public long getBoosterDuration(int id) {
        return getBoosterTable(id).getDuration();
    }

    public String getBoosterPlayerName(int id) {
        return Bukkit.getOfflinePlayer(getBoosterTable(id).getPlayer()).getName();
    }

    public void setBoosterActive(int id, boolean isActive) {
        BoosterTable boosterTable = getBoosterTable(id);
        boosterTable.setActive(isActive);
        boosterTable.setTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + boosterTable.getDuration());
        updateBoosterTable(boosterTable);
    }

    public void removeBooster(int id) {
        BoosterTable boosterTable = getBoosterTable(id);
        Chaos.getPlugin().getDatabaseManager().deleteBoosterTable(boosterTable);
        getBoosterCache().invalidate(id);
    }

    public int getActiveBoosterID() {
        List<BoosterTable> booster = getBoosterCache().asMap().values().stream().filter(BoosterTable::isActive).toList();
        if (booster.isEmpty()) return 0;
        else return booster.get(0).getId();
    }


    //player data
    public boolean hasPlayerData(UUID uuid) {
        return getPlayerCache().getIfPresent(uuid) != null;
    }

    public void setPlayerData(PlayerTable playerTable) {
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
    }

    public void createPlayerData(PlayerTable playerTable) {
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        Chaos.getPlugin().getDatabaseManager().createPlayerTable(playerTable);
    }

    private PlayerTable getPlayerTable(UUID player) {
        return getPlayerCache().getIfPresent(player);
    }

    private void updatePlayerTable(PlayerTable playerTable) {
        getPlayerCache().put(playerTable.getPlayer(), playerTable);
        Chaos.getPlugin().getDatabaseManager().updatePlayerTable(playerTable);
    }

    public String getDisplayLevel(UUID uuid) {
        return BukkitUtils.parseValue(Chaos.getPlugin().getPU().calculateLevel(getPlayerTable(uuid).getLevel()));
    }

    public double getLevel(UUID uuid) {
        return getPlayerTable(uuid).getLevel();
    }

    public void setLevel(UUID uuid, double level) {
        PU pu = Chaos.getPlugin().getPU();
        PlayerTable playerTable = getPlayerTable(uuid);
        int lastLevel = pu.calculateLevel(playerTable.getLevel());
        playerTable.setLevel(level);
        updatePlayerTable(playerTable);
        int currentLevel = pu.calculateLevel(playerTable.getLevel());
        if (lastLevel < currentLevel) {
            pu.sendLevelUp(uuid, currentLevel);
        }
    }

    public long getCoins(UUID uuid) {
        return getPlayerTable(uuid).getCoins();
    }

    public void setCoins(UUID uuid, long amount) {
        PlayerTable playerTable = getPlayerTable(uuid);
        Chaos.getPlugin().getPU().sendCoinReward(uuid,  Math.max(playerTable.getCoins(), amount) - Math.min(playerTable.getCoins(), amount));
        playerTable.setCoins(amount);
        updatePlayerTable(playerTable);
    }

    public int getKills(UUID uuid) {
        return getPlayerTable(uuid).getKills();
    }

    public void setKills(UUID uuid, int amount) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setKills(amount);
        updatePlayerTable(playerTable);
    }

    public int getDeaths(UUID uuid) {
        return getPlayerTable(uuid).getDeaths();
    }

    public void setDeaths(UUID uuid, int amount) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setDeaths(amount);
        updatePlayerTable(playerTable);
    }

    public int getLongestKillStreak(UUID uuid) {
        return getPlayerTable(uuid).getLongestKillStreak();
    }

    public void setLongestKillStreak(UUID uuid, int amount) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setLongestKillStreak(amount);
        updatePlayerTable(playerTable);
    }

    public int getWoolBroken(UUID uuid) {
        return getPlayerTable(uuid).getWoolBroken();
    }

    public void setWoolBroken(UUID uuid, int amount) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setWoolBroken(amount);
        updatePlayerTable(playerTable);
    }

    public int getGamesPlayed(UUID uuid) {
        return getPlayerTable(uuid).getGames();
    }

    public void setGamesPlayed(UUID uuid, int amount) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setGames(amount);
        updatePlayerTable(playerTable);
    }

    public void addPerm(UUID uuid, String perm) {
        PlayerTable playerTable = getPlayerTable(uuid);
        StringBuilder sPerms = new StringBuilder(playerTable.getPerms());
        if (!sPerms.toString().contains(perm)) {
            sPerms = sPerms.toString().equals("0") ? new StringBuilder(perm) : sPerms.append(",").append(perm);
            playerTable.setPerms(sPerms.toString());
            updatePlayerTable(playerTable);
        }
    }

    public void addPermList(UUID uuid, List<String> newPerms) {
        PlayerTable playerTable = getPlayerTable(uuid);
        StringBuilder sPerms = new StringBuilder(playerTable.getPerms());
        if (sPerms.toString().equals("0")) sPerms = new StringBuilder();
        for (String sPerm : newPerms) {
            if (sPerms.isEmpty()) {
                new StringBuilder(sPerm);
            } else {
                if (!sPerms.toString().contains(sPerm)) sPerms.append(",").append(sPerm);
            }
        }
        playerTable.setPerms(sPerms.toString());
        updatePlayerTable(playerTable);
    }

    public void removePerm(UUID uuid, String perm) {
        PlayerTable playerTable = getPlayerTable(uuid);
        String sPerms = playerTable.getPerms();
        List<String> permList = new ArrayList<>(Arrays.asList(StringUtils.split(sPerms, ',')));
        permList.remove(perm);
        String newPerms = StringUtils.join(permList, ",");
        if (newPerms.isBlank()) newPerms = "0";
        playerTable.setPerms(newPerms);
        updatePlayerTable(playerTable);
    }

    public List<String> getPerms(UUID uuid) {
        return new ArrayList<>(Arrays.asList(StringUtils.split(getPlayerTable(uuid).getPerms(), ',')));
    }

    public boolean hasPerm(UUID uuid, String perm) {
        return getPlayerTable(uuid).getPerms().contains(perm);
    }

    public boolean hasRank(UUID uuid) {
        return !getPlayerTable(uuid).getRank().equals("0");
    }

    public void setRank(UUID uuid, String rank) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setRank(rank);
        updatePlayerTable(playerTable);
    }

    public String getRank(UUID uuid) {
        return getPlayerTable(uuid).getRank();
    }

    public void setKit(UUID uuid, String kit) {
        PlayerTable playerTable = getPlayerTable(uuid);
        playerTable.setKit(kit);
        updatePlayerTable(playerTable);
    }

    public Kit getKit(UUID uuid) {
        return Chaos.getPlugin().getData().getKit(getPlayerTable(uuid).getKit());
    }

    public boolean hasSelectedKillStreak(UUID uuid) {
        return !getPlayerTable(uuid).getSelectedKillStreaks().equals("0");
    }

    public LinkedList<KillStreak> getSelectedKillStreaks(UUID uuid) {
        PlayerTable playerTable = getPlayerTable(uuid);
        LinkedList<KillStreak> killStreaks = new LinkedList<>();
        for (String streak : StringUtils.split(playerTable.getSelectedKillStreaks(), ',')) {
            killStreaks.add(Chaos.getPlugin().getData().getKillStreak(streak));
        }
        return killStreaks;
    }

    public void addSelectedKillStreak(UUID uuid, String name) {
        PlayerTable playerTable = getPlayerTable(uuid);
        if (playerTable.getSelectedKillStreaks().equals("0")) playerTable.setSelectedKillStreaks(name);
        else {
            LinkedList<String> streaks =  new LinkedList<>(Arrays.asList(StringUtils.split(playerTable.getSelectedKillStreaks(), ',')));
            if (streaks.size() >= 3) streaks.remove(0);
            streaks.add(name);
            playerTable.setSelectedKillStreaks(StringUtils.join(streaks, ","));
        }
        updatePlayerTable(playerTable);
    }

    public void removeSelectedKillStreak(UUID uuid, String name) {
        PlayerTable playerTable = getPlayerTable(uuid);
        LinkedList<String> streaks =  new LinkedList<>(Arrays.asList(StringUtils.split(playerTable.getSelectedKillStreaks(), ',')));
        streaks.remove(name);
        if (streaks.isEmpty()) playerTable.setSelectedKillStreaks("0");
        else playerTable.setSelectedKillStreaks(StringUtils.join(streaks, ","));
        updatePlayerTable(playerTable);
    }
}

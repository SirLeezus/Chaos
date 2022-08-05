package lee.code.chaos.database;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lee.code.chaos.Chaos;
import lee.code.chaos.PU;
import lee.code.chaos.database.tables.PlayerTable;
import lee.code.core.util.bukkit.BukkitUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CacheManager {

    @Getter
    private final Cache<UUID, PlayerTable> playerCache = CacheBuilder
            .newBuilder()
            .initialCapacity(5000)
            .recordStats()
            .build();

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

}

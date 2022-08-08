package lee.code.chaos.database;

import lee.code.chaos.Chaos;
import lee.code.chaos.database.tables.BoosterTable;
import lee.code.chaos.database.tables.PlayerTable;
import lee.code.core.ormlite.dao.Dao;
import lee.code.core.ormlite.dao.DaoManager;
import lee.code.core.ormlite.jdbc.JdbcConnectionSource;
import lee.code.core.ormlite.jdbc.db.DatabaseTypeUtils;
import lee.code.core.ormlite.logger.LogBackendType;
import lee.code.core.ormlite.logger.LoggerFactory;
import lee.code.core.ormlite.support.ConnectionSource;
import lee.code.core.ormlite.table.TableUtils;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {

    private Dao<PlayerTable, UUID> playerDao;
    private Dao<BoosterTable, Integer> boosterDao;

    @Getter(AccessLevel.NONE)
    private ConnectionSource connectionSource;

    private String getDatabaseURL() {
        Chaos plugin = Chaos.getPlugin();
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        return "jdbc:sqlite:" + new File(plugin.getDataFolder(), "database.db");
    }

    public void initialize() {
        LoggerFactory.setLogBackendFactory(LogBackendType.NULL);

        try {
            String databaseURL = getDatabaseURL();
            connectionSource = new JdbcConnectionSource(
                    databaseURL,
                    "test",
                    "test",
                    DatabaseTypeUtils.createDatabaseType(databaseURL));
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        CacheManager cacheManager = Chaos.getPlugin().getCacheManager();

        //player data
        TableUtils.createTableIfNotExists(connectionSource, PlayerTable.class);
        playerDao = DaoManager.createDao(connectionSource, PlayerTable.class);
        //load player data into cache
        for (PlayerTable playerTable : playerDao.queryForAll()) cacheManager.setPlayerData(playerTable);

        //booster data
        TableUtils.createTableIfNotExists(connectionSource, BoosterTable.class);
        boosterDao = DaoManager.createDao(connectionSource, BoosterTable.class);
        //booster punishment data into cache
        for (BoosterTable boosterTable : boosterDao.queryForAll()) cacheManager.setBoosterData(boosterTable);

    }

    public void closeConnection() {
        try {
            connectionSource.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void createPlayerTable(PlayerTable playerTable) {
        Bukkit.getScheduler().runTaskAsynchronously(Chaos.getPlugin(), () -> {
            try {
                playerDao.create(playerTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updatePlayerTable(PlayerTable playerTable) {
        Bukkit.getScheduler().runTaskAsynchronously(Chaos.getPlugin(), () -> {
            try {
                playerDao.update(playerTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void createBoosterTable(BoosterTable boosterTable) {
        Bukkit.getScheduler().runTaskAsynchronously(Chaos.getPlugin(), () -> {
            try {
                boosterDao.create(boosterTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void updateBoosterTable(BoosterTable boosterTable) {
        Bukkit.getScheduler().runTaskAsynchronously(Chaos.getPlugin(), () -> {
            try {
                boosterDao.update(boosterTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public synchronized void deleteBoosterTable(BoosterTable boosterTable) {
        Bukkit.getScheduler().runTaskAsynchronously(Chaos.getPlugin(), () -> {
            try {
                boosterDao.delete(boosterTable);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}

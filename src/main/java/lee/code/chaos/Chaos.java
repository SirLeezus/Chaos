package lee.code.chaos;

import lee.code.chaos.commands.cmds.*;
import lee.code.chaos.commands.tabs.*;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.database.DatabaseManager;
import lee.code.chaos.listeners.*;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.managers.GameManager;
import lee.code.chaos.managers.WorldManager;
import lee.code.core.util.bukkit.BukkitUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Chaos extends JavaPlugin {

    @Getter private WorldManager worldManager;
    @Getter private DatabaseManager databaseManager;
    @Getter private CacheManager cacheManager;
    @Getter private GameManager gameManager;
    @Getter private Data data;
    @Getter private PU pU;

    @Override
    public void onEnable() {
        this.pU = new PU();
        this.worldManager = new WorldManager();
        this.data = new Data();
        this.gameManager = new GameManager();
        this.databaseManager = new DatabaseManager();
        this.cacheManager = new CacheManager();

        databaseManager.initialize();
        data.load();
        pU.scheduleBoosterChecker();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        databaseManager.closeConnection();
        BukkitUtils.kickOnlinePlayers(Lang.RESTART_MESSAGE.getComponent(null));
        Bukkit.unloadWorld(data.getActiveMap().name(), false);
        worldManager.deleteWorldFolder(new File("./" + data.getActiveMap().name()));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new KillStreakListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new QuitListener(), this);
        getServer().getPluginManager().registerEvents(new SpectatorListener(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new SignListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new KitListener(), this);
    }

    private void registerCommands() {
        getCommand("message").setExecutor(new MessageCMD());
        getCommand("message").setTabCompleter(new MessageTab());
        getCommand("reply").setExecutor(new ReplyCMD());
        getCommand("reply").setTabCompleter(new ReplyTab());
        getCommand("stats").setExecutor(new StatsCMD());
        getCommand("stats").setTabCompleter(new StatsTab());
        getCommand("kits").setExecutor(new KitsCMD());
        getCommand("kits").setTabCompleter(new KitsTab());
        getCommand("killstreaks").setExecutor(new KillStreaksCMD());
        getCommand("killstreaks").setTabCompleter(new KillStreakTab());
        getCommand("addbooster").setExecutor(new AddBoosterCMD());
        getCommand("addbooster").setTabCompleter(new AddBoosterTab());
        getCommand("booster").setExecutor(new BoosterCMD());
        getCommand("booster").setTabCompleter(new BoosterTab());
        getCommand("removebooster").setExecutor(new RemoveBoosterCMD());
        getCommand("removebooster").setTabCompleter(new RemoveBoosterTab());
        getCommand("setpremiumrank").setExecutor(new SetPremiumRankCMD());
        getCommand("setpremiumrank").setTabCompleter(new SetPremiumRankTab());
        getCommand("coins").setExecutor(new CoinsCMD());
        getCommand("coins").setTabCompleter(new CoinsTab());
        getCommand("teamchat").setExecutor(new TeamChatCMD());
        getCommand("teamchat").setTabCompleter(new TeamChatTab());
    }

    public static Chaos getPlugin() {
        return Chaos.getPlugin(Chaos.class);
    }
}

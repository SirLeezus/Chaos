package lee.code.chaos;

import lee.code.chaos.commands.cmds.*;
import lee.code.chaos.commands.tabs.*;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.database.DatabaseManager;
import lee.code.chaos.listeners.*;
import lee.code.chaos.managers.GameManager;
import lee.code.chaos.managers.PermissionManager;
import lee.code.chaos.managers.WorldManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Chaos extends JavaPlugin {

    @Getter private WorldManager worldManager;
    @Getter private DatabaseManager databaseManager;
    @Getter private PermissionManager permissionManager;
    @Getter private CacheManager cacheManager;
    @Getter private GameManager gameManager;
    @Getter private Data data;
    @Getter private PU pU;

    @Override
    public void onEnable() {
        this.pU = new PU();
        this.worldManager = new WorldManager();
        this.permissionManager = new PermissionManager();
        this.data = new Data();
        this.gameManager = new GameManager();
        this.databaseManager = new DatabaseManager();
        this.cacheManager = new CacheManager();

        databaseManager.initialize();
        permissionManager.loadPerms();
        data.load();
        pU.scheduleBoosterChecker();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        databaseManager.closeConnection();
        for (Player player : Bukkit.getOnlinePlayers()) player.kick();
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
        getServer().getPluginManager().registerEvents(new CommandTabListener(), this);
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
        getCommand("setrank").setExecutor(new SetRankCMD());
        getCommand("setrank").setTabCompleter(new SetRankTab());
        getCommand("removerank").setExecutor(new RemoveRankCMD());
        getCommand("removerank").setTabCompleter(new RemoveRankTab());
    }

    public static Chaos getPlugin() {
        return Chaos.getPlugin(Chaos.class);
    }
}

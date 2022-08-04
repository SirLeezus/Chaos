package lee.code.chaos.commands.cmds;

import lee.code.chaos.Chaos;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.lists.Lang;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StatsCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Chaos plugin = Chaos.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        if (sender instanceof Player player) {
            Player target = player;
            if (args.length > 0) {
                OfflinePlayer oTarget = Bukkit.getOfflinePlayerIfCached(args[0]);
                if (oTarget != null) {
                    target = oTarget.getPlayer();
                } else {
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { args[0] })));
                    return true;
                }
            }
            if (target != null) {
                UUID uuid = target.getUniqueId();
                List<Component> lines = new ArrayList<>();
                lines.add(Lang.COMMAND_STATS_TITLE.getComponent(null));
                lines.add(Component.text(""));
                lines.add(Lang.COMMAND_STATS_PLAYER_LINE.getComponent(new String[] { target.getName() }));
                lines.add(Lang.COMMAND_STATS_LEVEL_LINE.getComponent(new String[] { cacheManager.getDisplayLevel(uuid) }));
                lines.add(Lang.COMMAND_STATS_COINS_LINE.getComponent(new String[] { BukkitUtils.parseValue(cacheManager.getCoins(uuid)) }));
                lines.add(Lang.COMMAND_STATS_KILLS_LINE.getComponent(new String[] { BukkitUtils.parseValue(cacheManager.getKills(uuid)) }));
                lines.add(Lang.COMMAND_STATS_LONGEST_KILL_STREAK_LINE.getComponent(new String[] { BukkitUtils.parseValue(cacheManager.getLongestKillStreak(uuid)) }));
                lines.add(Lang.COMMAND_STATS_DEATHS_LINE.getComponent(new String[] { BukkitUtils.parseValue(cacheManager.getDeaths(uuid)) }));
                lines.add(Lang.COMMAND_STATS_WOOL_BROKEN_LINE.getComponent(new String[] { BukkitUtils.parseValue(cacheManager.getWoolBroken(uuid)) }));
                lines.add(Lang.COMMAND_STATS_GAMES_LINE.getComponent(new String[] { BukkitUtils.parseValue(cacheManager.getGamesPlayed(uuid)) }));
                lines.add(Component.text(""));
                lines.add(Lang.COMMAND_STATS_SPLITTER.getComponent(null));
                for (Component line : lines) player.sendMessage(line);
            }
        } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
        return true;
    }
}

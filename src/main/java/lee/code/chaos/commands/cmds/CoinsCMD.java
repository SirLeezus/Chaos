package lee.code.chaos.commands.cmds;

import lee.code.chaos.Chaos;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CoinsCMD implements CommandExecutor {

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
                long balance = cacheManager.getCoins(target.getUniqueId());
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_COINS_SUCCESSFUL.getComponent(new String[] { target.getName(), String.valueOf(balance) })));
            }
        } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
        return true;
    }
}

package lee.code.chaos.commands.cmds;

import lee.code.chaos.Chaos;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.lists.Lang;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class AddBoosterCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Chaos plugin = Chaos.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        if (args.length > 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (target != null) {
                UUID uuid = target.getUniqueId();
                long durationBooster = BukkitUtils.serializeSeconds(args[1]);
                if (durationBooster != 0) {
                    if (BukkitUtils.containOnlyNumbers(args[2])) {
                        int multiplier = Integer.parseInt(args[2]);
                        cacheManager.queueBooster(uuid, multiplier, durationBooster);
                        sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BOOSTER_ADD_SUCCESSFUL.getComponent(new String[] { target.getName(), String.valueOf(multiplier) })));
                    }
                }
            } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[] { args[0] })));
        } else sender.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));

        return true;
    }
}

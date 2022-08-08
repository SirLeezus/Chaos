package lee.code.chaos.commands.cmds;

import lee.code.chaos.Chaos;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.lists.Lang;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RemoveBoosterCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Chaos plugin = Chaos.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        if (args.length > 0) {
            if (cacheManager.areBoosters()) {
                if (BukkitUtils.containOnlyNumbers(args[0])) {
                    int id = Integer.parseInt(args[0]);
                    if (cacheManager.getBoosterIDList().contains(id)) {
                        cacheManager.removeBooster(id);
                        sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BOOSTER_REMOVE_SUCCESSFUL.getComponent(new String[] { String.valueOf(id) })));
                    }
                }
            }
        } else sender.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));

        return true;
    }
}

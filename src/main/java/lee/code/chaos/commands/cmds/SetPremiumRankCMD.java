package lee.code.chaos.commands.cmds;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.lists.Rank;
import lee.code.permissions.PermissionsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class SetPremiumRankCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();

        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[0]);
            if (target != null) {
                UUID tUUID = target.getUniqueId();
                String rank = args[1].toUpperCase();
                if (data.getPremiumRankKeys().contains(rank)) {
                    if (PermissionsAPI.hasRank(tUUID)) {
                        Rank pRank = Rank.valueOf(PermissionsAPI.getRank(tUUID));
                        if (pRank.getPriority() > 0) {
                            Rank tRank = Rank.valueOf(rank);
                            if (tRank.getPriority() <= pRank.getPriority()) return true;
                        }
                    }
                    PermissionsAPI.setRank(tUUID, rank);
                    if (target.isOnline()) {
                        Player tPlayer = target.getPlayer();
                        if (tPlayer != null) {
                            plugin.getGameManager().updateDisplayName(tPlayer);
                            PermissionsAPI.register(tPlayer);
                        }
                    }
                    sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_RANKSET_SUCCESSFUL.getComponent(new String[] { rank, target.getName() })));
                }
            }
        } else sender.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));
        return true;
    }
}

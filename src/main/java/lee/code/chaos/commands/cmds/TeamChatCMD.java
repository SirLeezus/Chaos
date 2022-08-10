package lee.code.chaos.commands.cmds;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.maps.MapData;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamChatCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();

        if (sender instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (args.length > 0) {
                MapData map = data.getActiveMap().getData();
                GameTeam team = map.getTeam(uuid);
                String message = BukkitUtils.buildStringFromArgs(args, 0);
                switch (team) {
                    case BLUE -> {
                        for (UUID bUUID : map.getBlueTeam()) {
                            Player bPlayer = Bukkit.getPlayer(bUUID);
                            if (bPlayer != null) {
                                bPlayer.sendMessage(Lang.TEAM_MESSAGE.getComponent(new String[] { Lang.BLUE_TEAM.getString(null), Lang.BLUE_COLOR.getString(null), player.getName(), "&#00D1FF", message }));
                            }
                        }
                    }
                    case RED -> {
                        for (UUID rUUID : map.getRedTeam()) {
                            Player rPlayer = Bukkit.getPlayer(rUUID);
                            if (rPlayer != null) {
                                rPlayer.sendMessage(Lang.TEAM_MESSAGE.getComponent(new String[] { Lang.RED_TEAM.getString(null), Lang.RED_COLOR.getString(null), player.getName(), "&#CD2234", message }));
                            }
                        }
                    }
                    case SPECTATOR -> {
                        for (UUID sUUID : map.getSpectators()) {
                            Player sPlayer = Bukkit.getPlayer(sUUID);
                            if (sPlayer != null) {
                                sPlayer.sendMessage(Lang.TEAM_MESSAGE.getComponent(new String[] { Lang.SPECTATOR_TEAM.getString(null), Lang.SPECTATOR_COLOR.getString(null), player.getName(), "&#FFC900", message }));
                            }
                        }
                    }
                }
            } else player.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));
        } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
        return true;
    }
}

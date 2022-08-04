package lee.code.chaos.commands.cmds;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.Lang;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ReplyCMD implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();

        if (sender instanceof Player player) {
            if (args.length > 0) {
                UUID uuid = player.getUniqueId();
                if (data.hasReplier(uuid)) {
                    UUID tUUID = data.getLastReplier(uuid);
                    OfflinePlayer oTarget = Bukkit.getPlayer(tUUID);
                    if (oTarget != null) {
                        if (oTarget.isOnline()) {
                            Player target = oTarget.getPlayer();
                            if (target != null) {
                                String message = BukkitUtils.buildStringFromArgs(args, 0);
                                data.setLastReplier(uuid, target.getUniqueId());

                                player.sendMessage(Lang.MESSAGE_SENT.getComponent(new String[] { target.getName() })
                                        .append(Component.text(message).color(TextColor.color(0, 220, 234))));

                                target.sendMessage(Lang.MESSAGE_RECEIVED.getComponent(new String[] { player.getName() })
                                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + player.getName() + " "))
                                        .append(Component.text(message).color(TextColor.color(0, 220, 234))));

                            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_REPLY_NO_PLAYER.getComponent(null)));
                        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_ONLINE.getComponent(new String[] { oTarget.getName() })));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_REPLY_NO_PLAYER.getComponent(null)));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_REPLY_NO_PLAYER.getComponent(null)));
            } else sender.sendMessage(Lang.USAGE.getComponent(new String[] { command.getUsage() }));
        } else sender.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_CONSOLE_COMMAND.getComponent(null)));
        return true;
    }
}

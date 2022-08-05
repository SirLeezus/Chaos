package lee.code.chaos.managers;

import lee.code.chaos.Chaos;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.lists.Rank;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.*;

public class PermissionManager {

    @Getter private final List<String> defaultPerms = new ArrayList<>();
    private final List<String> staffPerms = new ArrayList<>();

    public void register(Player player) {
        Chaos plugin = Chaos.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        UUID uuid = player.getUniqueId();

        PermissionAttachment attachment = player.addAttachment(plugin);
        if (!player.isOp()) {
            attachment.getPermissions().clear();
            for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) attachment.setPermission(perm.getPermission(), false);
            if (cacheManager.hasRank(uuid)) {
                Rank rank = Rank.valueOf(cacheManager.getRank(uuid));
                switch (rank) {
                    case MOD, ADMIN -> {
                        for (String perm : staffPerms) attachment.setPermission(perm, true);
                    }
                }
            }
            for (String perm : defaultPerms) attachment.setPermission(perm, true);
            for (String perm : cacheManager.getPerms(uuid)) attachment.setPermission(perm, true);
        }

        player.recalculatePermissions();
        player.updateCommands();
    }

    public Collection<String> getCommands(Player player) {
        Chaos plugin = Chaos.getPlugin();
        Collection<String> commands = new HashSet<>();
        CommandMap commandMap = plugin.getServer().getCommandMap();
         for (Map.Entry<String, Command> sCommand : commandMap.getKnownCommands().entrySet()) {
             String perm = sCommand.getValue().getPermission();
             if (perm != null && player.hasPermission(perm)) {
                 Command command = sCommand.getValue();
                 List<String> aliases = command.getAliases();
                 commands.addAll(aliases);
                 commands.add(command.getName());
             }
         }
        return commands;
    }

    public void loadPerms() {
        //bukkit
        defaultPerms.add("bukkit.command.tps");
        defaultPerms.add("bukkit.command.ping");
        defaultPerms.add("allow.ride.llama");
        defaultPerms.add("allow.ride.snow_golem");
        defaultPerms.add("allow.special.snow_golem");

        // chaos
        defaultPerms.add("chaos.command.stats");
        defaultPerms.add("chaos.command.message");
        defaultPerms.add("chaos.command.reply");
        defaultPerms.add("chaos.kit.default");

        // staff
        staffPerms.add("chaos.command.admin");
    }
}

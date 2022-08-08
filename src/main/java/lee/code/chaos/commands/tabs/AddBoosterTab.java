package lee.code.chaos.commands.tabs;

import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddBoosterTab implements TabCompleter {

    private final List<String> blank = new ArrayList<>();
    
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 1) {
                return StringUtil.copyPartialMatches(args[0], BukkitUtils.getOnlinePlayers(), new ArrayList<>());
            } else if (args.length == 2) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("1h", "1m", "1s"), new ArrayList<>());
            } else if (args.length == 3) {
                return StringUtil.copyPartialMatches(args[2], Arrays.asList("2", "3", "4", "5"), new ArrayList<>());
            }else return blank;
        } else return blank;
    }
}
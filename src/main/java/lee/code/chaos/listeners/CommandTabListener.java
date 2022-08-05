package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.managers.PermissionManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

public class CommandTabListener implements Listener {

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onCommandTabShow(PlayerCommandSendEvent e) {
        Chaos plugin = Chaos.getPlugin();
        PermissionManager pm = plugin.getPermissionManager();

        Player player = e.getPlayer();
        if (!player.isOp()) {
            e.getCommands().clear();
            e.getCommands().addAll(pm.getCommands(player));
        }
    }
}

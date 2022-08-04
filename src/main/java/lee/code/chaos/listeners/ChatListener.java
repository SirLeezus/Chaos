package lee.code.chaos.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import lee.code.chaos.Chaos;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(AsyncChatEvent e) {
        Chaos plugin = Chaos.getPlugin();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!e.isCancelled()) {
            e.setCancelled(true);
            plugin.getServer().sendMessage(player.displayName().clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + player.getName() + " ")).append(BukkitUtils.parseColorComponent("&8: &f")).append(e.message()));
        }
    }
}
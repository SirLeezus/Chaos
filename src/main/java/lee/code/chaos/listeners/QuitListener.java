package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.maps.MapData;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class QuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();

        if (!map.getSpectators().contains(uuid)) {
            GameTeam team = map.getTeam(uuid);
            Component teamComponent = team.equals(GameTeam.RED) ? Lang.PREFIX.getComponent(null).append(Lang.PLAYER_LEFT_TEAM.getComponent(new String[] { Lang.RED_COLOR.getString(null), player.getName(), Lang.RED_TEAM.getString(null) })) : Lang.PREFIX.getComponent(null).append(Lang.PLAYER_LEFT_TEAM.getComponent(new String[] { Lang.BLUE_COLOR.getString(null), player.getName(), Lang.BLUE_TEAM.getString(null) }));
            Bukkit.getServer().sendMessage(teamComponent);
        }

        //remove from teams
        map.removeSpectator(uuid);
        map.removeBlueTeam(uuid);
        map.removeSpectator(uuid);

        //disable quit message
        e.quitMessage(null);

        if (map.getBlueTeam().isEmpty() || map.getRedTeam().isEmpty()) {
            if (data.getGameState().equals(GameState.ACTIVE)) {
                plugin.getGameManager().endGame();
            }
        }
    }
}

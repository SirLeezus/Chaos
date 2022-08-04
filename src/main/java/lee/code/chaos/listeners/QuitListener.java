package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.maps.MapData;
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

        //remove from teams
        map.getSpectators().remove(uuid);
        map.getRedTeam().remove(uuid);
        map.getBlueTeam().remove(uuid);

        //remove board packet & heath tracker
        data.removeBoard(uuid);
        data.removeHeathTracker(uuid);

        //disable quit message
        e.quitMessage(null);

        if (map.getBlueTeam().isEmpty() || map.getRedTeam().isEmpty()) {
            if (data.getGameState().equals(GameState.ACTIVE)) {
                plugin.getGameManager().endGame();
            }
        }
    }
}

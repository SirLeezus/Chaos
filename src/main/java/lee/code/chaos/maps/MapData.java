package lee.code.chaos.maps;

import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class MapData {

    private String name;
    private long seed;
    private Location spawn;
    private Location redSpawn;
    private Location blueSpawn;
    private World.Environment environment;
    private int blueScore;
    private int redScore;
    private List<UUID> redTeam = new ArrayList<>();
    private List<UUID> blueTeam = new ArrayList<>();
    private List<UUID> spectators = new ArrayList<>();
    private final List<UUID> respawningPlayers = new ArrayList<>();

    public void addRedTeam(UUID uuid) { redTeam.add(uuid); }
    public void removeRedTeam(UUID uuid) { redTeam.remove(uuid); }

    public void addBlueTeam(UUID uuid) { blueTeam.add(uuid); }
    public void removeBlueTeam(UUID uuid) { blueTeam.remove(uuid); }

    public void addSpectator(UUID uuid) { spectators.add(uuid); }
    public void removeSpectator(UUID uuid) { spectators.remove(uuid); }

    public void addRespawningPlayer(UUID uuid) { respawningPlayers.add(uuid); }
    public void removeRespawningPlayer(UUID uuid) { respawningPlayers.remove(uuid); }
    public boolean isRespawningPlayer(UUID uuid) { return respawningPlayers.contains(uuid); }

    public GameTeam getTeam(UUID uuid) {
        if (spectators.contains(uuid)) return GameTeam.SPECTATOR;
        else if (blueTeam.contains(uuid)) return GameTeam.BLUE;
        else if (redTeam.contains(uuid)) return GameTeam.RED;
        else return GameTeam.SPECTATOR;
    }

    public String getColorChar(UUID uuid) {
        if (spectators.contains(uuid)) return Lang.SPECTATOR_COLOR.getString(null);
        else if (blueTeam.contains(uuid)) return Lang.BLUE_COLOR.getString(null);
        else if (redTeam.contains(uuid)) return Lang.RED_COLOR.getString(null);
        else return Lang.SPECTATOR_COLOR.getString(null);
    }
}

package lee.code.chaos.maps.map;

import lee.code.chaos.maps.Map;
import lee.code.chaos.maps.MapData;
import org.bukkit.World;

public class LastDestination extends Map {

    private MapData mapData = new MapData();

    @Override
    public MapData getData() { return mapData; }

    @Override
    public void resetData() { mapData = new MapData(); }

    @Override
    public String name() { return "last_destination"; }

    @Override
    public long seed() { return 2704391880255509001L; }

    @Override
    public World.Environment environment() {
        return World.Environment.NORMAL;
    }

    @Override
    public String spawn() {
        return name() + ",-183.545,102,-31.517,0,1.2";
    }

    @Override
    public String redTeamSpawn() {
        return name() + ",-0.528,67,-123.511,0.9,1.2";
    }

    @Override
    public String blueTeamSpawn() {
        return name() + ",3.503,67,87.481,-178.9,1.4";
    }
}

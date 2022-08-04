package lee.code.chaos.maps.map;

import lee.code.chaos.maps.Map;
import lee.code.chaos.maps.MapData;
import org.bukkit.World;

public class EgyptianIslands extends Map {

    private MapData mapData = new MapData();

    @Override
    public MapData getData() { return mapData; }

    @Override
    public void resetData() { mapData = new MapData(); }

    @Override
    public String name() { return "egyptian_islands"; }

    @Override
    public long seed() { return 3970978866593988791L; }

    @Override
    public World.Environment environment() {
        return World.Environment.NORMAL;
    }

    @Override
    public String spawn() {
        return name() + ",-110.551,141,-5.493,-88.8,2.1";
    }

    @Override
    public String redTeamSpawn() {
        return name() + ",-23.523,101,-93.527,-0.2,-0.8";
    }

    @Override
    public String blueTeamSpawn() {
        return name() + ",-24.537,101,82.47,179.9,0.2";
    }
}

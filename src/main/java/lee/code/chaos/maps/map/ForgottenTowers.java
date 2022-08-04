package lee.code.chaos.maps.map;

import lee.code.chaos.maps.Map;
import lee.code.chaos.maps.MapData;
import org.bukkit.World;

public class ForgottenTowers extends Map {

    private MapData mapData = new MapData();

    @Override
    public MapData getData() { return mapData; }

    @Override
    public void resetData() { mapData = new MapData(); }

    @Override
    public String name() { return "forgotten_towers"; }

    @Override
    public long seed() { return 8152420655030201955L; }

    @Override
    public World.Environment environment() {
        return World.Environment.NORMAL;
    }

    @Override
    public String spawn() {
        return name() + ",75.47,179,56.5,89.7,2.3";
    }

    @Override
    public String redTeamSpawn() {
        return name() + ",-1,142.5,125.465,-179.9,0.5";
    }

    @Override
    public String blueTeamSpawn() {
        return name() + ",4,142.5,-17.525,0.5,1.0";
    }
}

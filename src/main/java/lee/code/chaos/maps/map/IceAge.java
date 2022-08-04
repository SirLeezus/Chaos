package lee.code.chaos.maps.map;

import lee.code.chaos.maps.Map;
import lee.code.chaos.maps.MapData;
import org.bukkit.World;

public class IceAge extends Map {

    private MapData mapData = new MapData();

    @Override
    public MapData getData() {
        return mapData;
    }

    @Override
    public void resetData() {
        mapData = new MapData();
    }

    @Override
    public String name() {
        return "ice_age";
    }

    @Override
    public long seed() {
        return -5215549426016449062L;
    }

    @Override
    public World.Environment environment() {
        return World.Environment.NORMAL;
    }

    @Override
    public String spawn() {
        return name() + ",269.75,114,-59.53,89.9,3.4";
    }

    @Override
    public String redTeamSpawn() {
        return name() + ",6.456,98,-162,-0.2,0.8";
    }

    @Override
    public String blueTeamSpawn() {
        return name() + ",10.479,98,67.472,179.6,-1.2";
    }
}

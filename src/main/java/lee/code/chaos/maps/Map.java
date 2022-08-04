package lee.code.chaos.maps;

import org.bukkit.World;

public abstract class Map {
    public abstract MapData getData();
    public abstract void resetData();
    public abstract String name();
    public abstract long seed();
    public abstract World.Environment environment();
    public abstract String spawn();
    public abstract String redTeamSpawn();
    public abstract String blueTeamSpawn();
}

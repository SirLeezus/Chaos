package lee.code.chaos.managers;

import lee.code.chaos.generators.VoidGenerator;
import lee.code.chaos.maps.Map;
import lee.code.core.util.bukkit.BukkitUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class WorldManager {

    public void createMap(Map map) {
        copyWorld("./maps/" + map.name(), map.environment(), map.seed(), map.name());
    }

    private void copyWorld(String folder, World.Environment environment, long seed, String newWorldName) {
        copyWorldFolder(new File(folder), new File(Bukkit.getWorldContainer(), newWorldName));
        WorldCreator wcWorld = new WorldCreator(newWorldName);
        wcWorld.environment(environment);
        wcWorld.seed(seed);
        wcWorld.generator(new VoidGenerator());
        wcWorld.createWorld();
        Bukkit.getLogger().log(Level.INFO, BukkitUtils.parseColorString("&2World Created: &6" + newWorldName));
    }

    private void copyWorldFolder(File source, File target) {
        try {
            FileUtils.deleteDirectory(target);
            FileUtils.copyDirectory(source, target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteWorldFolder(File target) {
        try {
            FileUtils.deleteDirectory(target);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

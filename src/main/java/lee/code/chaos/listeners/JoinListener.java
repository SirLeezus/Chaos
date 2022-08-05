package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.database.tables.PlayerTable;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.managers.GameManager;
import lee.code.chaos.managers.board.BoardManager;
import lee.code.chaos.maps.MapData;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerPreLogin(PlayerLoginEvent e) {
        CacheManager cacheManager = Chaos.getPlugin().getCacheManager();
        UUID uuid = e.getPlayer().getUniqueId();
        if (!cacheManager.hasPlayerData(uuid)) cacheManager.createPlayerData(new PlayerTable(uuid));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Data data = Chaos.getPlugin().getData();
        Player player = e.getPlayer();
        MapData map = data.getActiveMap().getData();
        GameManager gameManager = plugin.getGameManager();

        //add spectator
        map.addSpectator(player.getUniqueId());

        ///set custom attack speed
        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if (attribute != null) attribute.setBaseValue(23.4);


        //disable join message
        e.joinMessage(null);

        //register perms
        plugin.getPermissionManager().register(player);

        //give all recipes
        for (NamespacedKey key : data.getRecipeKeys()) e.getPlayer().discoverRecipe(key);

        //update displayname
        gameManager.updateDisplayName(player, GameTeam.SPECTATOR, true);
        for (BoardManager board : data.getBoardPackets()) board.sendPacket(player);

        //teleport
        player.teleportAsync(map.getSpawn()).thenAccept(result -> {
            if (result) {
                gameManager.hideSpectator(player);
                gameManager.loadSpectatorDefaults(player);
            }
        });
    }
}

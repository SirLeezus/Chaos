package lee.code.chaos.listeners;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.menusystem.Menu;
import lee.code.chaos.menusystem.menus.TeamMenu;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Menu menu) {
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            if (BukkitUtils.hasClickDelay(player)) return;
            menu.handleMenu(e);
        }
    }

    @EventHandler
    public void onMenuClose(InventoryCloseEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        InventoryHolder holder = e.getInventory().getHolder();
        UUID uuid = e.getPlayer().getUniqueId();
        if (holder instanceof TeamMenu) {
            if (data.hasTeamMenuTask(uuid)) {
                Bukkit.getScheduler().cancelTask(data.getTeamMenuTask(uuid));
                data.removeTeamMenuTask(uuid);
            }
        }
    }
}
package lee.code.chaos.menusystem.menus;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.lists.GameState;
import lee.code.chaos.lists.GameTeam;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.managers.GameManager;
import lee.code.chaos.maps.MapData;
import lee.code.chaos.menusystem.Menu;
import lee.code.chaos.menusystem.PlayerMU;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TeamMenu extends Menu {

    public TeamMenu(PlayerMU pmu) { super(pmu); }

    @Override
    public Component getMenuName() {
        return Lang.MENU_TEAM_TITLE.getComponent(null);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        GameManager gameManager = plugin.getGameManager();
        Player player = pmu.getOwner();
        UUID uuid = player.getUniqueId();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null) return;
        if (e.getClickedInventory() == player.getInventory()) return;
        if (clickedItem.getType().equals(Material.AIR)) return;
        if (clickedItem.equals(fillerGlass)) return;

        if (map.getBlueTeam().contains(uuid) || map.getRedTeam().contains(uuid)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MENU_ALREADY_ON_TEAM.getComponent(new String[] { Lang.BLUE_TEAM.getString(null) })));
            return;
        }

        switch (e.getSlot()) {
            case 2 -> {
                playClickSound(player);
                if (map.getBlueTeam().size() > map.getRedTeam().size()) {
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MENU_JOIN_FILLED.getComponent(new String[] { Lang.BLUE_TEAM.getString(null) })));
                    return;
                }
                map.addBlueTeam(uuid);
                gameManager.updateDisplayName(player, GameTeam.BLUE, false);
                if (data.getGameState().equals(GameState.ACTIVE)) gameManager.teleportPlayerSpawn(player);
                Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MENU_JOINED_TEAM.getComponent(new String[] { Lang.BLUE_COLOR.getString(null), player.getName(), Lang.BLUE_TEAM.getString(null) })));
                inventory.close();
            }

            case 6 -> {
                playClickSound(player);
                if (map.getRedTeam().size() > map.getBlueTeam().size()) {
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MENU_JOIN_FILLED.getComponent(new String[] { Lang.RED_TEAM.getString(null) })));
                    return;
                }
                map.addRedTeam(uuid);
                gameManager.updateDisplayName(player, GameTeam.RED, false);
                if (data.getGameState().equals(GameState.ACTIVE)) gameManager.teleportPlayerSpawn(player);
                Bukkit.getServer().sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MENU_JOINED_TEAM.getComponent(new String[] { Lang.RED_COLOR.getString(null), player.getName(), Lang.RED_TEAM.getString(null) })));
                inventory.close();
            }
        }
    }

    @Override
    public void setMenuItems() {
        setFillerGlass();

        inventory.setItem(2, blueTeam);
        inventory.setItem(6, redTeam);
        scheduleUpdateTeamPlayers(pmu.getOwner());
    }

    private void scheduleUpdateTeamPlayers(Player player) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        MapData map = data.getActiveMap().getData();
        UUID uuid = player.getUniqueId();

        if (!data.hasTeamMenuTask(uuid)) {
            data.setTeamMenuTask(uuid, new BukkitRunnable() {
                @Override
                public void run() {
                    int blue = map.getBlueTeam().size();
                    int red = map.getRedTeam().size();

                    ItemStack blueItem = new ItemStack(blueTeam);
                    ItemMeta blueItemMeta = blueItem.getItemMeta();
                    BukkitUtils.setItemLore(blueItemMeta, Lang.MENU_TEAMS_ITEM_LORE.getString(new String[] { String.valueOf(blue), String.valueOf(50) }));
                    blueItem.setItemMeta(blueItemMeta);

                    ItemStack redItem = new ItemStack(redTeam);
                    ItemMeta redItemMeta = redItem.getItemMeta();
                    BukkitUtils.setItemLore(redItemMeta, Lang.MENU_TEAMS_ITEM_LORE.getString(new String[] { String.valueOf(red), String.valueOf(50) }));
                    redItem.setItemMeta(redItemMeta);

                    inventory.setItem(2, blueItem);
                    inventory.setItem(6, redItem);
                }
            }.runTaskTimer(plugin, 1L, 20L).getTaskId());
        }
    }
}

package lee.code.chaos.menusystem.menus;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.PU;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.killstreaks.KillStreak;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.menusystem.PaginatedMenu;
import lee.code.chaos.menusystem.PlayerMU;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.UUID;

public class KillStreakMenu extends PaginatedMenu {

    public KillStreakMenu(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_KILL_STREAK_TITLE.getComponent(new String[] { String.valueOf(pmu.getKitPage()  +  1) });
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
        PU pu = plugin.getPU();
        CacheManager cacheManager = plugin.getCacheManager();
        Player player = pmu.getOwner();
        UUID uuid = player.getUniqueId();
        ItemStack clickedItem = e.getCurrentItem();

        if (e.getClickedInventory() == player.getInventory()) return;
        if (clickedItem == null) return;
        if (clickedItem.getType().equals(Material.AIR)) return;
        if (clickedItem.equals(fillerGlass)) return;

        if (clickedItem.equals(previousPage)) {
            if (page == 0) {
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_PREVIOUS_PAGE.getString(null));
            } else {
                page = page - 1;
                pmu.setKillStreakPage(page);
                super.open();
                playClickSound(player);
            }
        } else if (clickedItem.equals(nextPage)) {
            if (!((index + 1) >= data.getKillStreaks().size())) {
                page = page + 1;
                pmu.setKillStreakPage(page);
                super.open();
                playClickSound(player);
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NEXT_PAGE.getString(null));

        } else if (!clickedItem.getType().equals(Material.STRUCTURE_VOID)) {
            KillStreak killStreak = pu.getPreviewItemKillStreak(clickedItem);
            if (e.isLeftClick()) {
                playClickSound(player);
                if (cacheManager.getSelectedKillStreaks(uuid).contains(killStreak)) return;
                cacheManager.addSelectedKillStreak(uuid, killStreak.name());
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MENU_KILL_STREAK_SELECTED.getComponent(new String[] { BukkitUtils.parseCapitalization(killStreak.name()) })));
                this.open();
            }
        } else if (clickedItem.getType().equals(Material.STRUCTURE_VOID)) {
            KillStreak killStreak = pu.getPreviewItemKillStreak(clickedItem);
            if (e.isLeftClick()) {
                playClickSound(player);
                new KillStreakBuyPreview(pmu, killStreak).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        Chaos plugin = Chaos.getPlugin();
        Player player = pmu.getOwner();
        UUID uuid = player.getUniqueId();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();
        LinkedList<KillStreak> killStreaks = data.getKillStreaks();
        LinkedList<KillStreak> selectedKillStreak = cacheManager.getSelectedKillStreaks(uuid);
        if (!killStreaks.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= killStreaks.size()) break;
                if (killStreaks.get(index) != null) {
                    KillStreak killStreak = killStreaks.get(index);
                    if (selectedKillStreak.contains(killStreak)) inventory.addItem(killStreak.selectedPreview());
                    else if (player.hasPermission("chaos.streak." + killStreak.name())) inventory.addItem(killStreak.unlockedPreview());
                    else inventory.addItem(killStreak.lockedPreview());
                }
            }
        }
    }
}

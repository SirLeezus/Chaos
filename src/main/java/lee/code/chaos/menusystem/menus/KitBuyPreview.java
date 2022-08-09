package lee.code.chaos.menusystem.menus;

import lee.code.chaos.Chaos;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.kits.Kit;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.menusystem.Menu;
import lee.code.chaos.menusystem.PlayerMU;
import lee.code.core.util.bukkit.BukkitUtils;
import lee.code.permissions.PermissionsAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class KitBuyPreview extends Menu {
    private final Kit kit;

    public KitBuyPreview(PlayerMU pmu, Kit kit) {
        super(pmu);
        this.kit = kit;
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_KIT_PREVIEW_TITLE.getComponent(new String[] { BukkitUtils.parseCapitalization(kit.name()) });
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Player player = pmu.getOwner();
        UUID uuid = player.getUniqueId();
        ItemStack clickedItem = e.getCurrentItem();
        CacheManager cacheManager = plugin.getCacheManager();

        if (clickedItem == null) return;
        if (e.getClickedInventory() == player.getInventory()) return;
        if (clickedItem.getType().equals(Material.AIR)) return;
        if (clickedItem.equals(fillerGlass)) return;

        if (clickedItem.equals(cancel)) {
            playClickSound(player);
            new KitMenu(pmu).open();
        } else if (clickedItem.equals(buy)) {
            playClickSound(player);
            long balance = cacheManager.getCoins(uuid);
            if (balance >= kit.cost()) {
                cacheManager.setCoins(uuid, balance - kit.cost());
                cacheManager.setKit(uuid, kit.name());
                PermissionsAPI.addPerm(player, "chaos.kit." + kit.name());
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MENU_BUY_KIT.getComponent(new String[] { BukkitUtils.parseCapitalization(kit.name()) })));
                inventory.close();
            } else {
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_BUY_KIT_BALANCE.getComponent(new String[] { BukkitUtils.parseValue(balance), BukkitUtils.parseValue(kit.cost()), BukkitUtils.parseCapitalization(kit.name()) })));
                inventory.close();
            }
        }
    }

    @Override
    public void setMenuItems() {
        setFillerGlass();
        ItemStack item = new ItemStack(kit.unlockedPreview());
        ItemMeta meta = item.getItemMeta();
        BukkitUtils.setItemLore(meta, Lang.MENU_BUY_PREVIEW_LORE.getString(new String[] { BukkitUtils.parseValue(kit.cost()) }));
        item.setItemMeta(meta);

        inventory.setItem(11, cancel);
        inventory.setItem(13, item);
        inventory.setItem(15, buy);
    }
}

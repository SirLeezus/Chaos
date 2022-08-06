package lee.code.chaos.menusystem.menus;

import lee.code.chaos.Chaos;
import lee.code.chaos.Data;
import lee.code.chaos.database.CacheManager;
import lee.code.chaos.kits.Kit;
import lee.code.chaos.kits.kit.Default;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.menusystem.PaginatedMenu;
import lee.code.chaos.menusystem.PlayerMU;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class KitMenu extends PaginatedMenu {

    public KitMenu(PlayerMU pmu) {
        super(pmu);
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_KIT_TITLE.getComponent(new String[] { String.valueOf(pmu.getKitPage()  +  1) });
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Chaos plugin = Chaos.getPlugin();
        Data data = plugin.getData();
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
                pmu.setKitPage(page);
                super.open();
                playClickSound(player);
            }
        } else if (clickedItem.equals(nextPage)) {
            if (!((index + 1) >= data.getKits().size())) {
                page = page + 1;
                pmu.setKitPage(page);
                super.open();
                playClickSound(player);
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NEXT_PAGE.getString(null));

        } else if (!clickedItem.getType().equals(Material.STRUCTURE_VOID)) {
            Kit kit = plugin.getPU().getPreviewItemKit(clickedItem);
            if (e.isLeftClick()) {
                playClickSound(player);
                cacheManager.setKit(uuid, kit.name());
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.MENU_KIT_SELECTED.getComponent(new String[] { BukkitUtils.parseCapitalization(kit.name()) })));
                inventory.close();
            } else if (e.isRightClick()) {
                playClickSound(player);
                new KitPreview(pmu, kit).open();
            }
        } else if (clickedItem.getType().equals(Material.STRUCTURE_VOID)) {
            Kit kit = plugin.getPU().getPreviewItemKit(clickedItem);
            if (e.isLeftClick()) {
                playClickSound(player);
                new KitBuyPreview(pmu, kit).open();
            } else if (e.isRightClick()) {
                playClickSound(player);
                new KitPreview(pmu, kit).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        Chaos plugin = Chaos.getPlugin();
        Player player = pmu.getOwner();
        Data data = plugin.getData();
        LinkedList<Kit> kits = new LinkedList<>(data.getKits());

        //set default first always
        for (int i = 0; i < kits.size(); i++) {
            if (kits.get(i).name().equals("default")) {
                Collections.swap(kits, 0, i);
                break;
            }
        }

        if (!kits.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= kits.size()) break;
                if (kits.get(index) != null) {
                    Kit kit = kits.get(index);
                    if (plugin.getCacheManager().getKit(player.getUniqueId()).equals(kit)) {
                        ItemStack previewItem = new ItemStack(kit.unlockedPreview());
                        ItemMeta itemMeta = previewItem.getItemMeta();
                        itemMeta.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        previewItem.setItemMeta(itemMeta);
                        inventory.addItem(previewItem);
                        continue;
                    }
                    if (player.hasPermission("chaos.kit." + kit.name())) inventory.addItem(kit.unlockedPreview());
                    else inventory.addItem(kit.lockedPreview());
                }
            }
        }
    }
}

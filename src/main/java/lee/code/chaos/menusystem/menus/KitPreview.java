package lee.code.chaos.menusystem.menus;

import lee.code.chaos.kits.Kit;
import lee.code.chaos.lists.Lang;
import lee.code.chaos.menusystem.Menu;
import lee.code.chaos.menusystem.PlayerMU;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class KitPreview extends Menu {
    private final Kit kit;

    public KitPreview(PlayerMU pmu, Kit kit) {
        super(pmu);
        this.kit = kit;
    }

    @Override
    public Component getMenuName() {
        return Lang.MENU_KIT_PREVIEW_TITLE.getComponent(new String[] { BukkitUtils.parseCapitalization(kit.name()) });
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = pmu.getOwner();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null) return;
        if (e.getClickedInventory() == player.getInventory()) return;
        if (clickedItem.getType().equals(Material.AIR)) return;
        if (clickedItem.equals(fillerGlass)) return;

        if (clickedItem.equals(back)) {
            playClickSound(player);
            new KitMenu(pmu).open();
        }
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(0, fillerGlass);
        inventory.setItem(1, fillerGlass);
        inventory.setItem(2, fillerGlass);
        inventory.setItem(3, fillerGlass);
        inventory.setItem(4, fillerGlass);
        inventory.setItem(5, fillerGlass);
        inventory.setItem(6, fillerGlass);
        inventory.setItem(7, fillerGlass);
        inventory.setItem(8, fillerGlass);

        for (ItemStack item : new ArrayList<>(kit.inventory().values())) inventory.addItem(item);

        inventory.setItem(45, fillerGlass);
        inventory.setItem(46, fillerGlass);
        inventory.setItem(47, fillerGlass);
        inventory.setItem(48, fillerGlass);
        inventory.setItem(49, back);
        inventory.setItem(50, fillerGlass);
        inventory.setItem(51, fillerGlass);
        inventory.setItem(52, fillerGlass);
        inventory.setItem(53, fillerGlass);
    }
}

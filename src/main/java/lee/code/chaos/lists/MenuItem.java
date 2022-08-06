package lee.code.chaos.lists;

import lee.code.core.util.bukkit.BukkitUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public enum MenuItem {

    FILLER_GLASS(Material.BLACK_STAINED_GLASS_PANE, "&r", null),
    CLOSE_MENU(Material.BARRIER, "&c&lClose", null),
    NEXT_PAGE(Material.PAPER, "&eNext Page >", null),
    BUY(Material.LIME_STAINED_GLASS_PANE, "&a&lBuy", null),
    CANCEL(Material.RED_STAINED_GLASS_PANE, "&c&lCancel", null),
    PREVIOUS_PAGE(Material.PAPER, "&e< Previous Page", null),
    BACK(Material.BARRIER, "&c&l< Back", null),
    BLUE_TEAM(Material.BLUE_WOOL, "&9&lBlue Team", null),
    RED_TEAM(Material.RED_WOOL, "&c&lRed Team", null),
    ;

    @Getter private final Material type;
    @Getter private final String name;
    @Getter private final String lore;

    public ItemStack getItem() {
        return BukkitUtils.getItem(type, name, lore, null, true);
    }
}

package lee.code.chaos.lists;

import lee.code.core.util.bukkit.BukkitUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public enum SpectatorItem {
    TEAM_SELECTOR(Material.COMPASS, "&e&lSelect Team", "&e> &aTo pick a team right-click this while holding it."),
    KIT_SELECTOR(Material.CHEST, "&6&lSelect Kit", "&e> &aTo pick a kit right-click this while holding it."),

    ;

    @Getter private final Material type;
    @Getter private final String name;
    @Getter private final String lore;

    public ItemStack getItem() {
        return BukkitUtils.getItem(type, name, lore, null, true);
    }
}

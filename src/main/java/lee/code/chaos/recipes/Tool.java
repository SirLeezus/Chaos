package lee.code.chaos.recipes;

import lee.code.chaos.Chaos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@AllArgsConstructor
public enum Tool {

    WOODEN_AXE(Material.WOODEN_AXE, 1),
    STONE_AXE(Material.STONE_AXE, 2),
    IRON_AXE(Material.IRON_AXE, 3),
    GOLDEN_AXE(Material.GOLDEN_AXE, 4),
    DIAMOND_AXE(Material.DIAMOND_AXE, 5),
    NETHERITE_AXE(Material.NETHERITE_AXE, 6),

    WOODEN_PICKAXE(Material.WOODEN_PICKAXE, 1),
    STONE_PICKAXE(Material.STONE_PICKAXE, 1),
    IRON_PICKAXE(Material.IRON_PICKAXE, 1),
    GOLDEN_PICKAXE(Material.GOLDEN_PICKAXE, 1),
    DIAMOND_PICKAXE(Material.DIAMOND_PICKAXE, 1),
    NETHERITE_PICKAXE(Material.NETHERITE_PICKAXE, 1),

    WOODEN_SWORD(Material.WOODEN_SWORD, 2),
    STONE_SWORD(Material.STONE_SWORD, 3),
    IRON_SWORD(Material.IRON_SWORD, 4),
    GOLDEN_SWORD(Material.GOLDEN_SWORD, 5),
    DIAMOND_SWORD(Material.DIAMOND_SWORD, 6),
    NETHERITE_SWORD(Material.NETHERITE_SWORD, 7),

    WOODEN_SHOVEL(Material.WOODEN_SHOVEL, 1),
    STONE_SHOVEL(Material.STONE_SHOVEL, 1),
    IRON_SHOVEL(Material.IRON_SHOVEL, 1),
    GOLDEN_SHOVEL(Material.GOLDEN_SHOVEL, 1),
    DIAMOND_SHOVEL(Material.DIAMOND_SHOVEL, 1),
    NETHERITE_SHOVEL(Material.NETHERITE_SHOVEL, 1),

    WOODEN_HOE(Material.WOODEN_HOE, 1),
    STONE_HOE(Material.STONE_HOE, 1),
    IRON_HOE(Material.IRON_HOE, 1),
    GOLDEN_HOE(Material.GOLDEN_HOE, 1),
    DIAMOND_HOE(Material.DIAMOND_HOE, 1),
    NETHERITE_HOE(Material.NETHERITE_HOE, 1),

    TRIDENT(Material.TRIDENT, 5),

    ;

    @Getter private final Material material;
    @Getter private final double attackDamage;

    public ItemStack getItem() {
        ItemStack axe = new ItemStack(material);
        ItemMeta axeMeta = axe.getItemMeta();
        Chaos.getPlugin().getPU().setAttackDamage(axeMeta, attackDamage);
        axe.setItemMeta(axeMeta);
        return axe;
    }
}

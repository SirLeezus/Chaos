package lee.code.chaos.recipes;

import lee.code.chaos.Chaos;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

@AllArgsConstructor
public enum CraftingRecipe {
    // A B C D E F G
    //axes
    WOODEN_AXE(Tool.WOODEN_AXE.getItem(), " BB, AB, A ", new Material[] {Material.STICK,  Material.OAK_PLANKS}),
    STONE_AXE(Tool.STONE_AXE.getItem(), " BB, AB, A ", new Material[] {Material.STICK,  Material.COBBLESTONE}),
    IRON_AXE(Tool.IRON_AXE.getItem(), " BB, AB, A ", new Material[] {Material.STICK,  Material.IRON_INGOT}),
    GOLDEN_AXE(Tool.GOLDEN_AXE.getItem(), " BB, AB, A ", new Material[] {Material.STICK,  Material.GOLD_INGOT}),
    DIAMOND_AXE(Tool.DIAMOND_AXE.getItem(), " BB, AB, A ", new Material[] {Material.STICK,  Material.DIAMOND}),
    NETHERITE_AXE(Tool.NETHERITE_AXE.getItem(), " BB, AB, A ", new Material[] {Material.STICK,  Material.NETHERITE_INGOT}),

    //pickaxes
    WOODEN_PICKAXE(Tool.WOODEN_PICKAXE.getItem(), "BBB, A , A ", new Material[] {Material.STICK,  Material.OAK_PLANKS}),
    STONE_PICKAXE(Tool.STONE_PICKAXE.getItem(), "BBB, A , A ", new Material[] {Material.STICK,  Material.COBBLESTONE}),
    IRON_PICKAXE(Tool.IRON_PICKAXE.getItem(), "BBB, A , A ", new Material[] {Material.STICK,  Material.IRON_INGOT}),
    GOLDEN_PICKAXE(Tool.GOLDEN_PICKAXE.getItem(), "BBB, A , A ", new Material[] {Material.STICK,  Material.GOLD_INGOT}),
    DIAMOND_PICKAXE(Tool.DIAMOND_PICKAXE.getItem(), "BBB, A , A ", new Material[] {Material.STICK,  Material.DIAMOND}),
    NETHERITE_PICKAXE(Tool.NETHERITE_PICKAXE.getItem(), "BBB, A , A ", new Material[] {Material.STICK,  Material.NETHERITE_INGOT}),

    //swords
    WOODEN_SWORD(Tool.WOODEN_SWORD.getItem(), " B , B , A ", new Material[] {Material.STICK,  Material.OAK_PLANKS}),
    STONE_SWORD(Tool.STONE_SWORD.getItem(), " B , B , A ", new Material[] {Material.STICK,  Material.COBBLESTONE}),
    IRON_SWORD(Tool.IRON_SWORD.getItem(), " B , B , A ", new Material[] {Material.STICK,  Material.IRON_INGOT}),
    GOLDEN_SWORD(Tool.GOLDEN_SWORD.getItem(), " B , B , A ", new Material[] {Material.STICK,  Material.GOLD_INGOT}),
    DIAMOND_SWORD(Tool.DIAMOND_SWORD.getItem(), " B , B , A ", new Material[] {Material.STICK,  Material.DIAMOND}),
    NETHERITE_SWORD(Tool.NETHERITE_SWORD.getItem(), " B , B , A ", new Material[] {Material.STICK,  Material.NETHERITE_INGOT}),

    //hoes
    WOODEN_HOE(Tool.WOODEN_HOE.getItem(), " BB, A , A ", new Material[] {Material.STICK,  Material.OAK_PLANKS}),
    STONE_HOE(Tool.STONE_HOE.getItem(), " BB, A , A ", new Material[] {Material.STICK,  Material.COBBLESTONE}),
    IRON_HOE(Tool.IRON_HOE.getItem(), " BB, A , A ", new Material[] {Material.STICK,  Material.IRON_INGOT}),
    GOLDEN_HOE(Tool.GOLDEN_HOE.getItem(), " BB, A , A ", new Material[] {Material.STICK,  Material.GOLD_INGOT}),
    DIAMOND_HOE(Tool.DIAMOND_HOE.getItem(), " BB, A , A ", new Material[] {Material.STICK,  Material.DIAMOND}),
    NETHERITE_HOE(Tool.NETHERITE_HOE.getItem(), " BB, A , A ", new Material[] {Material.STICK,  Material.NETHERITE_INGOT}),

    //shovels
    WOODEN_SHOVEL(Tool.WOODEN_SHOVEL.getItem(), " B , A , A ", new Material[] {Material.STICK,  Material.OAK_PLANKS}),
    STONE_SHOVEL(Tool.STONE_SHOVEL.getItem(), " B , A , A ", new Material[] {Material.STICK,  Material.COBBLESTONE}),
    IRON_SHOVEL(Tool.IRON_SHOVEL.getItem(), " B , A , A ", new Material[] {Material.STICK,  Material.IRON_INGOT}),
    GOLDEN_SHOVEL(Tool.GOLDEN_SHOVEL.getItem(), " B , A , A ", new Material[] {Material.STICK,  Material.GOLD_INGOT}),
    DIAMOND_SHOVEL(Tool.DIAMOND_SHOVEL.getItem(), " B , A , A ", new Material[] {Material.STICK,  Material.DIAMOND}),
    NETHERITE_SHOVEL(Tool.NETHERITE_SHOVEL.getItem(), " B , A , A ", new Material[] {Material.STICK,  Material.NETHERITE_INGOT}),

    ;

    @Getter private final ItemStack item;
    @Getter private final String craftingRecipe;
    @Getter private final Material[] recipeMaterial;

    public void registerRecipe() {
        Chaos plugin = Chaos.getPlugin();
        NamespacedKey key = new NamespacedKey(plugin, item.getType().name());
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        String[] shapeSplit = StringUtils.split(craftingRecipe, ',');
        recipe.shape(shapeSplit[0], shapeSplit[1], shapeSplit[2]);
        char c;
        int count = 0;
        int max = recipeMaterial.length;
        for (c = 'A'; c <= 'G'; ++c) {
            recipe.setIngredient(c, recipeMaterial[count]);
            count++;
            if (count >= max) break;
        }
        plugin.getServer().addRecipe(recipe);
    }
}

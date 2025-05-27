package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class EnlighteningPackRecipe : Recipe(
    "Enlightening Pack",
    "Enchanting Craft",
    ItemStack(Material.EXP_BOTTLE, 8),
    3,
    "enlightening_pack"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.EXP_BOTTLE, 8), id)).shape(" R ", "RPR", " R ")
            .setIngredient('R', Material.REDSTONE_BLOCK)
            .setIngredient('P', Material.GLASS_BOTTLE)
    }
}
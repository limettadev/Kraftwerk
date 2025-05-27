package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class HealingFruitRecipe : Recipe(
    "Healing Fruit",
    "Cooking Craft",
    ItemStack(Material.MELON),
    3,
    "healing_fruit"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.MELON), id)).shape("BSB", "SAS", "BSB")
            .setIngredient('B', Material.INK_SACK, 15)
            .setIngredient('S', Material.SEEDS)
            .setIngredient('A', Material.APPLE)
    }
}
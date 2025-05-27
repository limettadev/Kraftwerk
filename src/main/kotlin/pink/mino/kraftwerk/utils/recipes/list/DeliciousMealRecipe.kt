package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class DeliciousMealRecipe : Recipe(
    "Delicious Meal",
    "Survivalism Craft",
    ItemStack(Material.COOKED_BEEF, 10),
    3,
    "delicious_meal"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.COOKED_BEEF, 10), id)).shape("BBB", "BCB", "BBB")
            .setIngredient('B', Material.RAW_BEEF)
            .setIngredient('C', Material.COAL)
    }
}
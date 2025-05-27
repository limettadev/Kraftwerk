package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class LightAppleRecipe : Recipe(
    "Light Apple",
    "Cooking Ultimate",
    ItemStack(Material.GOLDEN_APPLE),
    1,
    "light_apple"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.GOLDEN_APPLE), id)).shape(" G ", "GAG", " G ")
            .setIngredient('G', Material.GOLD_INGOT)
            .setIngredient('A', Material.APPLE)
    }
}
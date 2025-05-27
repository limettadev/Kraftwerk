package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class IronEconomyRecipe : Recipe(
    "Iron Economy",
    "Engineering Craft",
    ItemStack(Material.IRON_INGOT, 10),
    3,
    "iron_economy"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.IRON_INGOT, 10), id)).shape("III", "ICI", "III")
            .setIngredient('I', Material.IRON_ORE)
            .setIngredient('C', Material.COAL)
    }
}
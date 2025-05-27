package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.ItemBuilder

class LeatherEconomyRecipe : Recipe(
    "Leather Economy",
    "Easy to make leather!",
    ItemStack(Material.LEATHER, 8),
    3,
    "leather_economy"
) {
    init {
        val leatherEconomy = ItemBuilder(Material.LEATHER)
            .setAmount(8)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(leatherEconomy, id)).shape("/L/", "/L/", "/L/")
            .setIngredient('/', Material.STICK)
            .setIngredient('L', Material.LEATHER)
    }
}
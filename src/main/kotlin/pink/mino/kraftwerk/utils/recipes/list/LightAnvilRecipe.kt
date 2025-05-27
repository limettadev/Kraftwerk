package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class LightAnvilRecipe : Recipe(
    "Light Anvil",
    "Enchanting Craft",
    ItemStack(Material.ANVIL),
    3,
    "light_anvil"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.ANVIL), id)).shape("III", " B ", "III")
            .setIngredient('I', Material.IRON_INGOT)
            .setIngredient('B', Material.IRON_BLOCK)
    }
}
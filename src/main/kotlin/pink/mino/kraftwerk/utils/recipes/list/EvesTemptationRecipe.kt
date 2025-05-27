package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class EvesTemptationRecipe : Recipe(
  "Eve's Temptation",
    "Cooking Craft",
    ItemStack(Material.APPLE, 2),
    3,
    "eves_temptation"
) {
  init {
    recipe = ShapelessRecipe(convertToRecipeItem(ItemStack(Material.APPLE, 2), id)).addIngredient(Material.APPLE)
      .addIngredient(Material.INK_SACK, 15)
  }
}
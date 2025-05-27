package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class DustOfLightRecipe : Recipe(
  "Dust of Light",
  "Alchemy Craft",
  ItemStack(Material.GLOWSTONE_DUST, 8),
  3,
  "dust_of_light"
) {
    init {
        val dustOfLight = ItemBuilder(Material.GLOWSTONE_DUST)
            .setAmount(8)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(dustOfLight, id)).shape("***", "*F*", "***")
            .setIngredient('*', Material.REDSTONE)
            .setIngredient('F', Material.FLINT)
    }
}
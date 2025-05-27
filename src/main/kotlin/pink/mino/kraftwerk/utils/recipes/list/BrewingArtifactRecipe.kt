package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class BrewingArtifactRecipe : Recipe(
    "Brewing Artifact",
    "Alchemy Craft",
    ItemStack(Material.NETHER_STALK),
    3,
    "brewing_artifact"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.NETHER_STALK), id)).shape(" S ", "SNS", " S ")
            .setIngredient('N', Material.FERMENTED_SPIDER_EYE)
            .setIngredient('S', Material.SEEDS)
    }
}
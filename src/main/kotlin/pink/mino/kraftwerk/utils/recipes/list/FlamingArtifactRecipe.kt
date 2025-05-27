package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class FlamingArtifactRecipe : Recipe(
    "Flaming Artifact",
    "Alchemy Ultimate",
    ItemStack(Material.BLAZE_ROD),
    1,
    "flaming_artifact"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.BLAZE_ROD), id)).shape("OLO", "OFO", "OLO")
            .setIngredient('O', Material.STAINED_GLASS, 1)
            .setIngredient('L', Material.LAVA_BUCKET)
            .setIngredient('F', Material.FIREWORK)
    }
}
package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.ItemBuilder

class GoldenHeadRecipe : Recipe(
    "Golden Head",
    "Bloodcraft Craft",
    ItemStack(Material.SKULL_ITEM),
    3,
    "golden_head"
) {
    init {
        val goldenHead = ItemBuilder(Material.SKULL_ITEM)
            .toSkull()
            .setOwner("PhantomTupac")
            .name("&6Golden Head")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(goldenHead, id)).shape("GGG", "GHG", "GGG")
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('G', Material.GOLD_INGOT)
    }
}
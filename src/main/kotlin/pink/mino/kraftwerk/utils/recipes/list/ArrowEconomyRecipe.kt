/*
 * Project: Kraftwerk
 * Class: ArrowEconomyRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class ArrowEconomyRecipe : Recipe(
    "Arrow Economy",
    "Hunter Craft",
    ItemStack(Material.ARROW, 20),
    3,
    "arrow_economy"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.ARROW, 20), id)).shape("FFF", "SSS", "RRR")
            .setIngredient('F', Material.FLINT)
            .setIngredient('R', Material.FEATHER)
            .setIngredient('S', Material.STICK)
    }
}
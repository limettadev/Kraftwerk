/*
 * Project: Kraftwerk
 * Class: SaddleRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class SaddleRecipe : Recipe(
    "Saddle",
    "Hunter Craft",
    ItemStack(Material.SADDLE),
    3,
    "saddle"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.SADDLE), id)).shape("LLL", "SLS", "I I")
            .setIngredient('I', Material.IRON_INGOT)
            .setIngredient('S', Material.STRING)
            .setIngredient('L', Material.LEATHER)
    }
}
/*
 * Project: Kraftwerk
 * Class: SugarRushRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class SugarRushRecipe : Recipe(
    "Sugar Rush",
    "Invention Craft",
    ItemStack(Material.SUGAR_CANE, 4),
    3,
    "sugar_rush"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.SUGAR_CANE, 4), id))
            .shape(" G ", "SRS", "   ")
            .setIngredient('G', Material.SAPLING)
            .setIngredient('R', Material.SUGAR)
            .setIngredient('S', Material.SEEDS)
    }
}
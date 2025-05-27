/*
 * Project: Kraftwerk
 * Class: GoldPackRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class GoldPackRecipe : Recipe(
    "Gold Pack",
    "Invention Craft",
    ItemStack(Material.GOLD_INGOT, 10),
    3,
    "gold_pack"
) {
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.GOLD_INGOT, 10), id))
            .shape("GGG", "GCG", "GGG")
            .setIngredient('G', Material.GOLD_ORE)
            .setIngredient('C', Material.COAL)
    }
}
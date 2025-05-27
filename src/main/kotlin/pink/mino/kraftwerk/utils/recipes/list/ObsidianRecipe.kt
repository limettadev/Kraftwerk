/*
 * Project: Kraftwerk
 * Class: ObsidianRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class ObsidianRecipe : Recipe(
    "Obsidian",
    "Engineering Craft",
    ItemStack(Material.OBSIDIAN),
    4,
    "obsidian"
) {
    init {
        recipe = ShapelessRecipe(convertToRecipeItem(ItemStack(Material.OBSIDIAN), id))
            .addIngredient(Material.WATER_BUCKET)
            .addIngredient(Material.LAVA_BUCKET)
    }
}
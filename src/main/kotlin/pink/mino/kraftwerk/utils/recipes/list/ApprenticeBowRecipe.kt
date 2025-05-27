/*
 * Project: Kraftwerk
 * Class: ApprenticeBowRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class ApprenticeBowRecipe : Recipe(
    "Apprentice Bow",
    "Gains power after a certain amount of time.",
    ItemStack(Material.BOW),
    1,
    "apprentice_bow"
) {
    init {
        val apprenticeBow = ItemBuilder(Material.BOW)
            .name("&5Apprentice Bow")
            .addLore("&7Gains &fPower I&7 after 10 minutes&7.")
            .addLore("&7Gains &fPower II&7 after 20 minutes&7.")
            .addLore("&7Gains &fPower III&7 after 40 minutes&7.")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(apprenticeBow, id)).shape(" RS", "R S", " RS")
            .setIngredient('R', Material.REDSTONE_TORCH_ON)

            .setIngredient('S', Material.STRING)
    }
}
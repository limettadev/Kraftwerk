/*
 * Project: Kraftwerk
 * Class: CupidsBowRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class CupidsBowRecipe : Recipe(
    "Cupid's Bow",
    "Bloodcraft Ultimate",
    ItemStack(Material.BOW),
    1,
    "cupids_bow"
) {
    init {
        val cupidsBow = ItemBuilder(Material.BOW)
            .name("&5Cupid's Bow")
            .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
            .addEnchantment(Enchantment.ARROW_FIRE, 1)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(cupidsBow, id)).shape(" R ", "HBH", " L ")
            .setIngredient('B', Material.BOW)
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('L', Material.LAVA_BUCKET)
            .setIngredient('R', Material.BLAZE_ROD)
    }
}
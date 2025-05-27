/*
 * Project: Kraftwerk
 * Class: PotionOfVelocityRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.PotionBuilder

class PotionOfVelocityRecipe : Recipe(
    "Potion of Velocity",
    "Hunter Craft",
    ItemStack(Material.GLASS_BOTTLE),
    3,
    "potion_of_velocity"
) {
    init {
        val potionOfVelocity =
            PotionBuilder.createPotion(PotionEffect(PotionEffectType.SPEED, 20 * 12, 3, false, true))
        recipe = ShapedRecipe(convertToRecipeItem(potionOfVelocity, id)).shape(" C ", " S ", " P ")
            .setIngredient('C', Material.COCOA)
            .setIngredient('S', Material.SUGAR)
            .setIngredient('P', Material.GLASS_BOTTLE)
    }
}
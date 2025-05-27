/*
 * Project: Kraftwerk
 * Class: NectarRecipe.kt
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

class NectarRecipe : Recipe(
    "Nectar",
    "Alchemy Craft",
    ItemStack(Material.GLASS_BOTTLE),
    3,
    "nectar"
) {
    init {
        val nectar = PotionBuilder.createPotion(PotionEffect(PotionEffectType.REGENERATION, 200, 1, false, true))
        recipe = ShapedRecipe(convertToRecipeItem(nectar, id)).shape(" E ", "GMG", " P ")
            .setIngredient('P', Material.GLASS_BOTTLE)
            .setIngredient('E', Material.EMERALD)
            .setIngredient('G', Material.GOLD_INGOT)
            .setIngredient('M', Material.MELON)
    }
}
/*
 * Project: Kraftwerk
 * Class: PanaceaRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
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

class PanaceaRecipe : Recipe(
    "Panacea",
    "Bloodcraft Craft",
    ItemStack(Material.GLASS_BOTTLE),
    3,
    "panacea"
) {
    init {
        val panacea = PotionBuilder.createPotion(PotionEffect(PotionEffectType.HEAL, 1, 3, false, true))
        recipe = ShapedRecipe(convertToRecipeItem(panacea, id)).shape("   ", "HGH", " P ")
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('P', Material.GLASS_BOTTLE)
            .setIngredient('G', Material.SPECKLED_MELON)
    }
}
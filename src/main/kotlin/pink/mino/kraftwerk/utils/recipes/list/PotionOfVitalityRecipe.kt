/*
 * Project: Kraftwerk
 * Class: PotionOfVitalityRecipe.kt
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

class PotionOfVitalityRecipe : Recipe(
    "Potion of Vitality",
    "Extra Ultimate",
    ItemStack(Material.POTION),
    1,
    "potion_of_vitality"
) {
    init {
        val potionOfVitality = PotionBuilder.createPotion(
            PotionEffect(PotionEffectType.SPEED, 20 * 12, 1, false, true),
            PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 1, false, true),
            PotionEffect(PotionEffectType.WEAKNESS, 20 * 12, 1, false, true),
            PotionEffect(PotionEffectType.WITHER, 20 * 6, 1, false, true)
        )
        recipe = ShapedRecipe(convertToRecipeItem(potionOfVitality, id)).shape(" S ", " N ", " B")
            .setIngredient('S', Material.SKULL_ITEM, 0)
            .setIngredient('N', Material.NETHER_WARTS)
            .setIngredient('B', Material.GLASS_BOTTLE)
    }
}
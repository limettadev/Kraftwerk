/*
 * Project: Kraftwerk
 * Class: FlaskOfIchorRecipe.kt
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
import pink.mino.kraftwerk.utils.PotionBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class FlaskOfIchorRecipe : Recipe(
    "Flask of Ichor",
    "Extra Ultimate",
    ItemStack(Material.EXP_BOTTLE),
    1,
    "flask_of_ichor"
) {
    init {
        val flaskOfIchor = PotionBuilder.createPotion(PotionEffect(PotionEffectType.HARM, 1, 2, false, false))
        recipe = ShapedRecipe(convertToRecipeItem(flaskOfIchor, id)).shape(" H ", "BPB", " S ")
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('B', Material.BROWN_MUSHROOM)
            .setIngredient('P', Material.GLASS_BOTTLE)
            .setIngredient('S', Material.INK_SACK)
    }
}
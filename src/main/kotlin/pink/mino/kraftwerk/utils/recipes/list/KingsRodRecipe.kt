/*
 * Project: Kraftwerk
 * Class: KingsRodRecipe.kt
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

class KingsRodRecipe : Recipe(
    "King's Rod",
    "idk, cool rod, ig",
    ItemStack(Material.FISHING_ROD),
    1,
    "kings_rod"
) {
    init {
        val kingsRod = ItemBuilder(Material.FISHING_ROD)
            .name("<yellow>King's Rod")
            .addEnchantment(Enchantment.LUCK, 10)
            .addEnchantment(Enchantment.LURE, 5)
            .addEnchantment(Enchantment.DURABILITY, 10)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(kingsRod, id)).shape(" F ", "LCL", " W ")
            .setIngredient('F', Material.FISHING_ROD)
            .setIngredient('L', Material.WATER_LILY)
            .setIngredient('C', Material.COMPASS)
            .setIngredient('W', Material.WATER_BUCKET)
    }
}
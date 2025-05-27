/*
 * Project: Kraftwerk
 * Class: HideOfLeviathanRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.ItemBuilder

class HideOfLeviathanRecipe : Recipe(
    "Hide of Leviathan",
    "Extra Ultimate",
    ItemStack(Material.DIAMOND_LEGGINGS),
    1,
    "hide_of_leviathan"
) {
    init {
        val hideOfLeviathan = ItemBuilder(Material.DIAMOND_LEGGINGS)
            .name("&eHide of Leviathan")
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
            .addEnchantment(Enchantment.OXYGEN, 3)
            .addEnchantment(Enchantment.WATER_WORKER, 1)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(hideOfLeviathan, id)).shape("LWL", "DGD", "P P")
            .setIngredient('L', Material.LAPIS_BLOCK)
            .setIngredient('W', Material.WATER_BUCKET)
            .setIngredient('D', Material.DIAMOND)
            .setIngredient('G', Material.DIAMOND_LEGGINGS)
            .setIngredient('P', Material.WATER_LILY)
    }
}
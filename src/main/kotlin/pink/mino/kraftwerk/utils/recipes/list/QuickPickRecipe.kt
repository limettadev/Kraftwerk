/*
 * Project: Kraftwerk
 * Class: QuickPickRecipe.kt
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

class QuickPickRecipe : Recipe(
    "Quick Pick",
    "Toolsmithing Craft",
    ItemStack(Material.IRON_PICKAXE),
    3,
    "quick_pick"
) {
    init {
        val quickPick = ItemBuilder(Material.IRON_PICKAXE)
            .name("&5Quick Pick")
            .addEnchantment(Enchantment.DIG_SPEED, 1)
            .make()

        recipe = ShapedRecipe(quickPick).shape("III", "CSC", " S ")
            .setIngredient('I', Material.IRON_ORE)
            .setIngredient('C', Material.COAL)
            .setIngredient('S', Material.STICK)

    }
}
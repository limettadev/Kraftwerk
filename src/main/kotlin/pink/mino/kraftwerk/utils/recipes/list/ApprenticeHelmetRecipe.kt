/*
 * Project: Kraftwerk
 * Class: ApprenticeHelmetRecipe.kt
 *
 * Copyright (c) 2023-2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class ApprenticeHelmetRecipe : Recipe(
    "Apprentice Helmet",
    "Apprentice Craft",
    ItemStack(Material.IRON_HELMET),
    1,
    "apprentice_helmet"
) {
    init {
        val apprenticeHelmet = ItemBuilder(Material.IRON_HELMET)
            .name("&5Apprentice Helmet")
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1)
            .addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1)
            .make()
        recipe = ShapedRecipe(apprenticeHelmet).shape("III", "IRI", "   ")
            .setIngredient('I', Material.IRON_INGOT)
            .setIngredient('R', Material.REDSTONE_TORCH_ON)
    }
}
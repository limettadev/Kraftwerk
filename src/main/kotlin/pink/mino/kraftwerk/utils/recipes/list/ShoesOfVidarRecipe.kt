/*
 * Project: Kraftwerk
 * Class: ShoesOfVidarRecipe.kt
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

class ShoesOfVidarRecipe : Recipe(
    "Shoes of Vidar",
    "Extra Ultimate",
    ItemStack(Material.DIAMOND_BOOTS),
    1,
    "shoes_of_vidar"
) {
    init {
        val shoesOfVidar = ItemBuilder(Material.DIAMOND_BOOTS)
            .name("&eShoes of Vidar")
            .addEnchantment(Enchantment.DURABILITY, 3)
            .addEnchantment(Enchantment.DEPTH_STRIDER, 2)
            .addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2)
            .addEnchantment(Enchantment.THORNS, 1)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(shoesOfVidar, id)).shape(" P ", "BDB", " R ")
            .setIngredient('P', Material.RAW_FISH, 3)
            .setIngredient('B', Material.POTION)
            .setIngredient('D', Material.DIAMOND_BOOTS)
            .setIngredient('R', Material.FISHING_ROD)
    }
}
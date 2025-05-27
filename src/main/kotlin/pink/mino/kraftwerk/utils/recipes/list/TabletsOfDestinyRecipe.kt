/*
 * Project: Kraftwerk
 * Class: TabletsOfDestinyRecipe.kt
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

class TabletsOfDestinyRecipe : Recipe(
    "Tablets of Destiny",
    "Extra Ultimate",
    ItemStack(Material.ENCHANTED_BOOK),
    1,
    "tablets_of_destiny"
) {
    init {
        val tabletsOfDestiny = ItemBuilder(Material.ENCHANTED_BOOK)
            .name("&eTablets of Destiny")
            .toEnchant()
            .addStoredEnchant(Enchantment.DAMAGE_ALL, 3)
            .addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
            .addStoredEnchant(Enchantment.ARROW_DAMAGE, 3)
            .addStoredEnchant(Enchantment.FIRE_ASPECT, 1)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(tabletsOfDestiny, id)).shape(" M ", "SBO", "XXX")
            .setIngredient('M', Material.MAGMA_CREAM)
            .setIngredient('S', Material.DIAMOND_SWORD)
            .setIngredient('B', Material.BOOK_AND_QUILL)
            .setIngredient('O', Material.BOW)
            .setIngredient('X', Material.EXP_BOTTLE)
    }
}
/*
 * Project: Kraftwerk
 * Class: PhilosophersPickaxeRecipe.kt
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

class PhilosophersPickaxeRecipe : Recipe(
    "Philosopher's Pickaxe",
    "Engineering Ultimate",
    ItemStack(Material.DIAMOND_PICKAXE),
    1,
    "philosophers_pickaxe"
) {
    init {
        val philosophersPickaxe = ItemBuilder(Material.DIAMOND_PICKAXE)
            .name("&5Philosopher's Pickaxe")
            .addEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2)
        philosophersPickaxe.setDurability((philosophersPickaxe.item.type.maxDurability - 2).toShort())
        recipe = ShapedRecipe(convertToRecipeItem(philosophersPickaxe.make(), id)).shape("IGI", "LSL", " S ")
            .setIngredient('I', Material.IRON_ORE)
            .setIngredient('G', Material.GOLD_ORE)
            .setIngredient('L', Material.LAPIS_BLOCK)
            .setIngredient('S', Material.STICK)
    }
}
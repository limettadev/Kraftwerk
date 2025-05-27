/*
 * Project: Kraftwerk
 * Class: BarbarianChestplateRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class BarbarianChestplateRecipe : Recipe(
    "Barbarian Chestplate",
    "While wearing: Gives Strength I, Resistance I",
    ItemStack(Material.DIAMOND_CHESTPLATE),
    1,
    "barbarian_chestplate"
) {
    init {
        val barbarianChestplate = ItemBuilder(Material.DIAMOND_CHESTPLATE)
            .name("&eBarbarian Chestplate")
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .addLore("&7While wearing: Gives Strength I, Resistance I")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(barbarianChestplate, id)).shape("RCR", "ISI", "   ")
            .setIngredient('R', Material.BLAZE_ROD)
            .setIngredient('C', Material.DIAMOND_CHESTPLATE)
            .setIngredient('I', Material.IRON_BLOCK)
            .setIngredient('S', Material.POTION, 8201)
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.inventory.chestplate != null && player.inventory.chestplate.hasItemMeta() && player.inventory.chestplate.itemMeta.displayName == Chat.colored(
                            "&eBarbarian Chestplate"
                        )
                    ) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 3, 0, false, true))
                        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 3, 0, false, true))
                    }
                }
            }
        }.runTaskTimer(Kraftwerk.instance, 0L, 20L)
    }
}
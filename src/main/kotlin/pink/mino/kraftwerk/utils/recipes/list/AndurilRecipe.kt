/*
 * Project: Kraftwerk
 * Class: AndurilRecipe.kt
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

class AndurilRecipe : Recipe(
    "Andūril",
    "Come on, you know what this does. Grants a speed boost while holding the item.",
    ItemStack(Material.IRON_SWORD),
    1,
    "anduril"
) {
    init {
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.inventory.itemInHand != null && player.inventory.itemInHand.hasItemMeta() && player.inventory.itemInHand.itemMeta.displayName == Chat.colored(
                            "&eAndūril"
                        )
                    ) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, (20 * 2).toInt(), 0, false, true))
                        player.addPotionEffect(
                            PotionEffect(
                                PotionEffectType.DAMAGE_RESISTANCE,
                                (20 * 2).toInt(), 0, false, true
                            )
                        )
                    }
                }
            }
        }.runTaskTimer(Kraftwerk.instance, 0L, 20L)
        val anduril = ItemBuilder(Material.IRON_SWORD)
            .name("&eAndūril")
            .addEnchantment(Enchantment.DAMAGE_ALL, 2)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(anduril, id)).shape("FIF", "FIF", "FBF")
            .setIngredient('F', Material.FEATHER)
            .setIngredient('I', Material.IRON_BLOCK)
            .setIngredient('B', Material.BLAZE_ROD)
    }
}
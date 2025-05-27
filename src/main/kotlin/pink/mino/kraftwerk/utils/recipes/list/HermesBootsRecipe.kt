/*
 * Project: Kraftwerk
 * Class: HermesBootsRecipe.kt
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
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder

class HermesBootsRecipe : Recipe(
    "Hermes' Boots",
    "While wearing: 10% movement speed increase",
    ItemStack(Material.DIAMOND_BOOTS),
    1,
    "hermes_boots"
) {
    init {
        val hermesBoots = ItemBuilder(Material.DIAMOND_BOOTS)
            .name("&eHermes' Boots")
            .addEnchantment(Enchantment.PROTECTION_FALL, 1)
            .addEnchantment(Enchantment.DURABILITY, 2)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
            .addLore("&7While wearing: 10% movement speed increase.")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(hermesBoots, id)).shape("DHD", "PBP", "F F")
            .setIngredient('D', Material.DIAMOND)
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('P', Material.BLAZE_POWDER)
            .setIngredient('F', Material.FEATHER)
            .setIngredient('B', Material.DIAMOND_BOOTS)
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.inventory.boots != null && player.inventory.boots.hasItemMeta() && player.inventory.boots.itemMeta.displayName == Chat.colored(
                            "&eHermes' Boots"
                        )
                    ) {
                        player.walkSpeed = 0.2F + 0.02F
                    } else {
                        player.walkSpeed = 0.2F
                    }
                }
            }
        }.runTaskTimer(Kraftwerk.instance, 0L, 20L)
    }
}
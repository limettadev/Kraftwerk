/*
 * Project: Kraftwerk
 * Class: BloodlustRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe
import java.util.*

class BloodlustRecipe : Recipe(
    "Bloodlust",
    "Gains sharpness progressively. Extra Ultimate",
    ItemStack(Material.DIAMOND_SWORD),
    1,
    "bloodlust"
) {
    val kills = hashMapOf<UUID, Int>()

    init {
        val bloodlust = ItemBuilder(Material.DIAMOND_SWORD)
            .name("<yellow>Bloodlust")
            .addEnchantment(Enchantment.DAMAGE_ALL, 1)
            .addLore("<gray>Gains Sharpness II After 1 kill")
            .addLore("<gray>Gains Sharpness III after 3 kill")
            .addLore("<gray>Gains Sharpness IV after 6 kill")
            .addLore("<gray>Gains Sharpness V after 10 kill")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(bloodlust, id)).shape("RDR", "RSR", "RXR")
            .setIngredient('R', Material.REDSTONE_BLOCK)
            .setIngredient('D', Material.DIAMOND)
            .setIngredient('S', Material.DIAMOND_SWORD)
            .setIngredient('X', Material.EXP_BOTTLE)
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (e.entity.killer == null) return
        if (e.entity.killer !is Player) return
        if (e.entity.killer.itemInHand != null && e.entity.killer.itemInHand.hasItemMeta() && e.entity.killer.itemInHand.itemMeta.displayName == Chat.colored(
                "<yellow>Bloodlust"
            )
        ) {
            if (kills[e.entity.killer.uniqueId] == null) {
                kills[e.entity.killer.uniqueId] = 1
                e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 2)
            } else {
                kills[e.entity.killer.uniqueId] = kills[e.entity.killer.uniqueId]!! + 1
                if (kills[e.entity.killer.uniqueId] == 3) {
                    e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 3)
                } else if (kills[e.entity.killer.uniqueId] == 6) {
                    e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 4)
                } else if (kills[e.entity.killer.uniqueId] == 10) {
                    e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 5)
                }
            }
        }
    }
}
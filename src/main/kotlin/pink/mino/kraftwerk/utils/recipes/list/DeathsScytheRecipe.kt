/*
 * Project: Kraftwerk
 * Class: DeathsScytheRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class DeathsScytheRecipe : Recipe(
    "Death's Scythe",
    "Damage dealt will be 20% of the target's current health, bypassses armor",
    ItemStack(Material.IRON_HOE),
    1,
    "deaths_scythe"
) {
    init {
        val deathsScythe = ItemBuilder(Material.IRON_HOE)
            .name("<yellow>Death's Scythe")
            .addLore("<gray>Damage dealt will be 20% of the target's current health, bypassses armor")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(deathsScythe, id)).shape(" HH", " BW", "B  ")
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('B', Material.BONE)
            .setIngredient('W', Material.WATCH)
    }

    @EventHandler
    fun onPvP(e: EntityDamageByEntityEvent) {
        if (e.damager is Player && e.entity is Player) {
            if ((e.damager as Player).inventory.itemInHand != null && (e.damager as Player).inventory.itemInHand.hasItemMeta() && (e.damager as Player).inventory.itemInHand.itemMeta.displayName == Chat.colored(
                    "<yellow>Death's Scythe"
                )
            ) {
                (e.entity as Player).damage((e.entity as Player).health * .2)
            }
        }
    }
}
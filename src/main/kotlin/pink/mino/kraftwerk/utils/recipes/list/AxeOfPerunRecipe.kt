/*
 * Project: Kraftwerk
 * Class: AxeOfPerunRecipe.kt
 *
 * Copyright (c) 2023-2023 Juan Pichardo (juanp)
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

class AxeOfPerunRecipe : Recipe(
    "Axe of Perun",
    "Extra Ultimate",
    ItemStack(Material.DIAMOND_AXE),
    1,
    "axe_of_perun"
) {
    val perunCooldownsMap = hashMapOf<Player, Long>()

    init {
        val axeOfPerun = ItemBuilder(Material.DIAMOND_AXE)
            .name("<yellow>Axe of Perun")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(axeOfPerun, id)).shape("DTF", "DS ", " S ")
            .setIngredient('D', Material.DIAMOND)
            .setIngredient('T', Material.TNT)
            .setIngredient('F', Material.FIREBALL)
            .setIngredient('S', Material.STICK)
    }

    @EventHandler
    fun onPvP(e: EntityDamageByEntityEvent) {
        if ((e.damager as Player).inventory.itemInHand != null && (e.damager as Player).inventory.itemInHand.hasItemMeta() && (e.damager as Player).inventory.itemInHand.itemMeta.displayName == Chat.colored(
                "<yellow>Axe of Perun"
            )
        ) {
            if (perunCooldownsMap[e.damager as Player] == null || perunCooldownsMap[e.damager as Player]!! < System.currentTimeMillis()) {
                (e.damager as Player).world.strikeLightning((e.entity as Player).location)
                perunCooldownsMap[e.damager as Player] = System.currentTimeMillis() + 8000
            } else {
                return
            }
        }
    }

}
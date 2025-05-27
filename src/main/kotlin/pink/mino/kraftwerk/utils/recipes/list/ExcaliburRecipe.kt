/*
 * Project: Kraftwerk
 * Class: ExcaliburRecipe.kt
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
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class ExcaliburRecipe : Recipe(
    "Excalibur",
    "2 hearts of true damage on hit, 5 seconds cooldown",
    ItemStack(Material.DIAMOND_SWORD),
    1,
    "excalibur"
) {
    val excaliburCooldowns = hashMapOf<Player, Long>()

    init {
        val excalibur = ItemBuilder(Material.DIAMOND_SWORD)
            .name("&eExcalibur")
            .addLore("&72 hearts of true damage on hit, 5 seconds cooldown")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(excalibur, id)).shape("SBS", "STS", "SDS")
            .setIngredient('S', Material.SOUL_SAND)
            .setIngredient('T', Material.TNT)
            .setIngredient('D', Material.DIAMOND_SWORD)
    }

    @EventHandler
    fun onPvP(e: EntityDamageByEntityEvent) {
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("champions"))) return
        if ((e.damager as Player).inventory.itemInHand != null && (e.damager as Player).inventory.itemInHand.hasItemMeta() && (e.damager as Player).inventory.itemInHand.itemMeta.displayName == Chat.colored(
                "&eExcalibur"
            )
        ) {
            if (excaliburCooldowns[e.damager as Player] == null || excaliburCooldowns[e.damager as Player]!! < System.currentTimeMillis()) {
                (e.entity as Player).damage((e.entity as Player).health * .2)
                excaliburCooldowns[e.damager as Player] = System.currentTimeMillis() + 5000
            } else {
                return
            }
        }
    }
}
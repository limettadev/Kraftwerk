/*
 * Project: Kraftwerk
 * Class: FenrirRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Wolf
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.material.SpawnEgg
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.recipes.Recipe

class FenrirRecipe : Recipe(
    "Fenrir",
    "Hunter Ultimate",
    SpawnEgg(EntityType.WOLF).toItemStack(1),
    1,
    "fenrir"
) {
    init {
        val fenrir = SpawnEgg(EntityType.WOLF).toItemStack(1)
        recipe = ShapedRecipe(convertToRecipeItem(fenrir, id)).shape("LLL", "BXB", "LLL")
            .setIngredient('L', Material.LEATHER)
            .setIngredient('X', Material.EXP_BOTTLE)
            .setIngredient('B', Material.BONE)
    }

    @EventHandler
    fun onFenrirSpawn(event: PlayerInteractEvent) {
        val player: Player = event.player

        val action: Action = event.action
        var item = event.item
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (item == null) {
            return
        }
        if (item.type != Material.MONSTER_EGG) {
            return
        }
        if (item.durability.toInt() != 95) {
            return
        }
        item = item.clone()
        item.amount = 1
        player.inventory.removeItem(item)
        var block: Block? = event.clickedBlock ?: return
        block = block!!.getRelative(event.blockFace)
        val wolf: Wolf = block.world.spawn(block.location.add(0.5, 0.1, 0.5), Wolf::class.java)
        wolf.isTamed = true
        wolf.owner = player
        wolf.maxHealth = 40.0
        wolf.health = 40.0
        wolf.addPotionEffect(PotionEffect(PotionEffectType.SPEED, Int.MAX_VALUE, 3))
        wolf.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Int.MAX_VALUE, 0))
        event.isCancelled = true
    }
}
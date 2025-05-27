/*
 * Project: Kraftwerk
 * Class: LumberjackAxeRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.PlayerUtils

class LumberjackAxeRecipe : Recipe(
    "Lumberjack Axe",
    "Drops all log blocks adjacent to the one you strike and the ones adjacent to them.",
    ItemStack(Material.IRON_AXE),
    3,
    "lumberjack_axe"
) {
    init {
        val lumberjackAxe = ItemBuilder(Material.IRON_AXE)
            .name("&5Lumberjack Axe")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(lumberjackAxe, id)).shape("IIF", "IS ", " S ")
            .setIngredient('I', Material.IRON_INGOT)
            .setIngredient('F', Material.FLINT)
            .setIngredient('S', Material.STICK)
    }

    @EventHandler
    fun onLumberjackAxe(e: BlockBreakEvent) {
        if (
            e.player.inventory.itemInHand != null &&
            e.player.inventory.itemInHand.hasItemMeta() &&
            e.player.inventory.itemInHand.itemMeta.displayName == Chat.colored("&5Lumberjack Axe")
        ) {
            timberTree(e.block.location, e.block.type, e.player)
        }
    }

    private fun timberTree(loc: Location, material: Material, player: Player) {
        for (x in loc.blockX - 1..loc.blockX + 1) {
            for (y in loc.blockY - 1..loc.blockY + 1) {
                for (z in loc.blockZ - 1..loc.blockZ + 1) {
                    val newLoc = Location(loc.world, x.toDouble(), y.toDouble(), z.toDouble())
                    if (loc.world.getBlockAt(x, y, z).type == material) {
                        loc.world.getBlockAt(x, y, z).breakNaturally()
                        loc.world.playSound(newLoc, Sound.DIG_WOOD, 1f, 1f)
                        timberTree(newLoc, material, player)
                    }
                }
            }
        }
    }
}
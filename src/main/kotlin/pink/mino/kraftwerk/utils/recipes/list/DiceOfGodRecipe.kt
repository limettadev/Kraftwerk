/*
 * Project: Kraftwerk
 * Class: DiceOfGodRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import com.google.common.collect.Lists
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe
import kotlin.random.Random

class DiceOfGodRecipe : Recipe(
    "Dice of God",
    "Grants a random Extra Ultimate",
    ItemStack(Material.ENDER_PORTAL_FRAME),
    1,
    "dice_of_god"
) {
    init {
        val diceOfGod = ItemBuilder(Material.ENDER_PORTAL_FRAME)
            .name("<yellow>Dice of God")
            .addLore("<gray>Grants a random Extra Ultimate.")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(diceOfGod, id)).shape("CHC", "CJC", "CCC")
            .setIngredient('C', Material.MOSSY_COBBLESTONE)
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('J', Material.JUKEBOX)
    }

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val player = event.whoClicked as Player
        val inv = event.inventory
        val item = inv.result
        if (!item.hasItemMeta() || !item.itemMeta.hasDisplayName()) {
            return
        }
        val name = item.itemMeta.displayName
        if (name == Chat.colored("<yellow>Dice of God")) {
            val block: Block? = player.getTargetBlock(null as Set<Material?>?, 10)
            if (block == null || block.type !== Material.WORKBENCH) {
                Chat.sendMessage(player, "${Chat.prefix} You are not looking at a crafting table.")
                event.isCancelled = true
                return
            }
            val extraUltimates: MutableList<ItemStack> = Lists.newArrayList()
            val it = Bukkit.recipeIterator()
            while (it.hasNext()) {
                val next = it.next()
                val result = next.result
                if (!result.hasItemMeta() || !result.itemMeta.hasDisplayName()) {
                    continue
                }
                if (result.itemMeta.displayName.startsWith("§1")) {
                    extraUltimates.add(result)
                }
            }
            event.currentItem = extraUltimates[Random.nextInt(extraUltimates.size)]
        }
    }
}
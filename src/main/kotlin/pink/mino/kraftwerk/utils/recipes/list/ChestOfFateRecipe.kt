/*
 * Project: Kraftwerk
 * Class: ChestOfFateRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.PotionBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe
import kotlin.random.Random

class ChestOfFateRecipe : Recipe(
    "Chest of Fate",
    "May give you a Absorption VII (1:45) and Speed Potion (level increases every 30 seconds up to III) or explode, dealing 10 hearts.",
    ItemStack(Material.CHEST),
    1,
    "chest_of_fate"
) {
    init {
        val chestOfFate = ItemBuilder(Material.CHEST)
            .name("&eChest of Fate")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(chestOfFate, id)).shape("WWW", "WHW", "WWW")
            .setIngredient('W', Material.WOOD)
            .setIngredient('H', Material.SKULL_ITEM, 3)
    }

    @EventHandler
    fun onCraftItemEvent(e: CraftItemEvent) {
        val player = e.whoClicked as Player
        val item = e.inventory.result
        val craftItem = CraftItemStack.asNMSCopy(item)
        if (craftItem.hasTag()) {
            val tag = craftItem.tag
            if (tag.getString("uhcId") != null) {
                if (tag.getString("uhcId") == "chest_of_fate") {
                    val block: Block? = player.getTargetBlock(null as Set<Material?>?, 10)
                    if (block == null || block.type !== Material.WORKBENCH) {
                        Chat.sendMessage(player, "&cYou are not looking at a crafting table.")
                        e.isCancelled = true
                        return
                    }
                    e.isCancelled = true
                    e.view.topInventory.clear()
                    if (Random.nextInt(1, 3) == 1) {
                        player.world.createExplosion(block.location, 5F, false)
                    } else {
                        val potion = PotionBuilder.createPotion(
                            PotionEffect(PotionEffectType.ABSORPTION, 105 * 20, 3, true, true),
                            PotionEffect(PotionEffectType.SPEED, 30 * 20, 2, true, true)
                        )
                        block.type = Material.CHEST
                        val chest: Chest = block.state as Chest
                        chest.inventory.setItem(13, potion)
                    }
                }
            }
        }
    }
}
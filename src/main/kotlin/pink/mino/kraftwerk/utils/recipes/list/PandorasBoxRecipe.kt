/*
 * Project: Kraftwerk
 * Class: PandorasBoxRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.BookBuilder
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.PotionBuilder
import kotlin.random.Random

class PandorasBoxRecipe : Recipe(
    "Pandora's Box",
    "Bloodcraft Craft",
    ItemStack(Material.CHEST),
    3,
    "pandoras_box"
) {
    init {
        val pandorasBox = ItemBuilder(Material.CHEST)
            .name("&ePandora's Box")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(pandorasBox, id)).shape("GGG", "GHG", "GGG")
                .setIngredient('G', Material.CHEST)
                .setIngredient('H', Material.SKULL_ITEM, 3)

    }

    private fun randomReward(): ItemStack? {
        var randomPerc = Random.nextDouble() * 100
        if (randomPerc < 2.0) {
            return BookBuilder.createEnchantedBook(Enchantment.ARROW_FIRE, 1)
        }
        randomPerc -= 2.0
        if (randomPerc < 2.0) {
            return BookBuilder.createEnchantedBook(Enchantment.FIRE_ASPECT, 2)
        }
        randomPerc -= 2.0
        if (randomPerc < 3.0) {
            return BookBuilder.createEnchantedBook(Enchantment.FIRE_ASPECT, 1)
        }
        randomPerc -= 3.0
        if (randomPerc < 5.0) {
            return BookBuilder.createEnchantedBook(Enchantment.DAMAGE_ALL, 4)
        }
        randomPerc -= 5.0
        if (randomPerc < 5.0) {
            return BookBuilder.createEnchantedBook(Enchantment.ARROW_DAMAGE, 4)
        }
        randomPerc -= 5.0
        if (randomPerc < 5.0) {
            return ItemStack(Material.DIAMOND, 7)
        }
        randomPerc -= 5.0
        if (randomPerc < 5.0) {
            return PotionBuilder.createPotion(PotionEffect(PotionEffectType.HEAL, 0, 3))
        }
        randomPerc -= 5.0
        if (randomPerc < 7.0) {
            return PotionBuilder.createPotion(PotionEffect(PotionEffectType.HEAL, 0, 2))
        }
        randomPerc -= 7.0
        if (randomPerc < 8.0) {
            return PotionBuilder.createPotion(
                PotionEffect(PotionEffectType.SPEED, 1800, 1, false, true),
                PotionEffect(PotionEffectType.ABSORPTION, 2400, 2, false, true)
            )
        }
        randomPerc -= 8.0
        if (randomPerc < 9.0) {
            return PotionBuilder.createPotion(
                PotionEffect(PotionEffectType.SPEED, 1800, 1, false, true),
                PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1800, 1, false, true)
            )
        }
        randomPerc -= 9.0
        if (randomPerc < 12.0) {
            return ItemStack(Material.GOLDEN_APPLE, 3)
        }
        randomPerc -= 12.0
        return if (randomPerc < 15.0) {
            ItemStack(Material.GOLD_INGOT, 24)
        } else ItemStack(Material.EXP_BOTTLE, 48)
    }

    @EventHandler
    fun onCraftItemEvent(e: CraftItemEvent) {
        val player = e.whoClicked as Player
        val item = e.inventory.result
        val craftItem = CraftItemStack.asNMSCopy(item)
        if (craftItem.hasTag()) {
            val tag = craftItem.tag
            if (tag.getString("uhcId") != null) {
                if (tag.getString("uhcId") == "pandoras_box") {
                    val block: Block? = player.getTargetBlock(null as Set<Material?>?, 10)
                    if (block == null || block.type !== Material.WORKBENCH) {
                        Chat.sendMessage(player, "&cYou are not looking at a crafting table.")
                        e.isCancelled = true
                        return
                    }
                    e.isCancelled = true
                    e.view.topInventory.clear()
                    block.type = Material.CHEST
                    val chest: Chest = block.state as Chest
                    chest.inventory.setItem(13, randomReward())
                }
            }
        }
    }
}
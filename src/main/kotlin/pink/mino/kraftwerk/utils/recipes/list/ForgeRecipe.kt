/*
 * Project: Kraftwerk
 * Class: ForgeRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Furnace
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe
import java.util.*

class ForgeRecipe : Recipe(
    "Forge",
    "Instantly smelts items, will break after 10 uses.",
    ItemStack(Material.FURNACE),
    3,
    "forge"
) {
    init {
        val forge = ItemBuilder(Material.FURNACE)
            .name("&eForge")
            .addLore("&7Instantly smelts items. Breaks after 10 uses.")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(forge, id)).shape("CCC", "COC", "CCC")
            .setIngredient('C', Material.COBBLESTONE)
            .setIngredient('O', Material.COAL_BLOCK)
    }

    val forgeMap = hashMapOf<UUID, Int>()

    @EventHandler
    fun onForgePlace(e: BlockPlaceEvent) {
        if (e.block.type == Material.FURNACE && (e.block.state as Furnace).inventory.title == Chat.colored("&5Forge")) {
            val lava = ItemBuilder(Material.LAVA_BUCKET)
                .setAmount(64)
                .name("&5Forgium")
                .make()
            (e.block.state as Furnace).inventory.fuel = lava
        }
    }

    @EventHandler
    fun onForgeBreak(e: BlockBreakEvent) {
        if (e.block.type == Material.FURNACE || e.block.type == Material.BURNING_FURNACE) {
            if ((e.block.state as Furnace).inventory.title == Chat.colored("&5Forge")) {
                e.isCancelled = true
                val forge = ItemBuilder(Material.FURNACE)
                    .name("&5Forge")
                    .addLore("&7Instantly smelts items. Breaks after 10 uses.")
                    .make()
                for (item in (e.block.state as Furnace).inventory.contents) {
                    if (item == null) continue
                    if (item.hasItemMeta() && item.itemMeta.displayName == Chat.colored("&5Forgium")) continue
                    e.block.world.dropItemNaturally(e.block.location, item)
                }
                e.block.type = Material.AIR
                e.block.world.dropItemNaturally(e.block.location, forge)
            }
        }
    }

    @EventHandler
    fun onForgeSmelt(e: InventoryClickEvent) {
        if (e.inventory.type != InventoryType.FURNACE) return
        if (e.inventory.title != Chat.colored("&5Forge")) return
        if (e.clickedInventory == null) return
        if (e.currentItem.type == Material.LAVA_BUCKET && e.currentItem.hasItemMeta() && e.currentItem.itemMeta.displayName == Chat.colored(
                "&5Forgium"
            )
        ) {
            e.isCancelled = true
            return
        }
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), runnable@{
            val furnace = e.inventory.holder as Furnace
            var result: ItemStack? = null
            val iter: Iterator<org.bukkit.inventory.Recipe> = Bukkit.recipeIterator()
            val item = furnace.inventory.smelting
            while (iter.hasNext()) {
                val recipe = iter.next()
                if (recipe !is FurnaceRecipe) continue
                if (recipe.input.type != item.type) continue
                result = recipe.result
                break
            }
            if (result == null) return@runnable
            val amount = furnace.inventory.smelting.amount
            for (i in 0 until amount) {
                furnace.block.world.dropItemNaturally(furnace.block.location, result)
                val smelting = furnace.inventory.smelting
                smelting.amount--
                furnace.inventory.smelting = smelting
                if (forgeMap[e.whoClicked.uniqueId] == null) forgeMap[e.whoClicked.uniqueId] = 0
                forgeMap[e.whoClicked.uniqueId] = forgeMap[e.whoClicked.uniqueId]!! + 1
                if (forgeMap[e.whoClicked.uniqueId]!! >= 10) {
                    furnace.inventory.fuel = ItemStack(Material.AIR)
                    furnace.block.type = Material.AIR
                    forgeMap.remove(e.whoClicked.uniqueId)
                    Chat.sendMessage(e.whoClicked, "&c&oYour forge has broken from reaching its limit.")
                    return@runnable
                }
            }
        }, 1L)
    }
}
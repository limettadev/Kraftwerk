/*
 * Project: Kraftwerk
 * Class: EssenceOfYggdrasilRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class EssenceOfYggdrasilRecipe : Recipe(
    "Essence of Yggdrasil",
    "Gives 2 Enchanting Tables as well as 30 EXP levels",
    ItemStack(Material.EXP_BOTTLE, 32),
    1,
    "essence_of_yggdrasil"
) {
    init {
        val essenceOfYggdrasil = ItemBuilder(Material.EXP_BOTTLE)
            .name("<yellow>Essence of Yggdrasil")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(essenceOfYggdrasil, id)).shape("LEL", "GBG", "LRL")
            .setIngredient('L', Material.LAPIS_BLOCK)
            .setIngredient('E', Material.ENCHANTMENT_TABLE)
            .setIngredient('G', Material.GLOWSTONE)
            .setIngredient('R', Material.REDSTONE_BLOCK)
            .setIngredient('B', Material.GLASS_BOTTLE)
    }

    @EventHandler
    fun on(e: CraftItemEvent) {
        val player = e.whoClicked as Player
        val inv = e.inventory
        val item = inv.result
        if (!item.hasItemMeta() || !item.itemMeta.hasDisplayName()) {
            return
        }
        val name = item.itemMeta.displayName
        if (name == Chat.colored("<yellow>Essence of Yggdrasil")) {
            inv.result = null
            player.level += 30
            Chat.sendMessage(player, "<yellow>You've been blessed by the Essence of Yggdrasil.")
            player.inventory.addItem(ItemStack(Material.ENCHANTMENT_TABLE, 2))
        }
    }
}
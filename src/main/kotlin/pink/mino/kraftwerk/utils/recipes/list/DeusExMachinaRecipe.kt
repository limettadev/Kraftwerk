/*
 * Project: Kraftwerk
 * Class: DeusExMachinaRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.PotionBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class DeusExMachinaRecipe : Recipe(
    "Deus ex Machina",
    "Resistance V Potion - Takes half of your health to craft!",
    ItemStack(Material.POTION),
    1,
    "deus_ex_machina"
) {
    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        val player = e.whoClicked as Player
        val inv = e.inventory
        val item = inv.result
        if (!item.hasItemMeta() || !item.itemMeta.hasDisplayName()) {
            return
        }
        val craftItem = CraftItemStack.asNMSCopy(item)
        if (craftItem.hasTag()) {
            val tag = craftItem.tag
            if (tag.getString("uhcId") != null) {
                if (tag.getString("uhcId") == "deus_ex_machina") {
                    player.health = player.health / 2
                    Chat.sendMessage(player, "&eYour health has been siphoned to create a Deus Ex Machina.")
                }
            }
        }
    }

    init {
        val deusExMachina =
            PotionBuilder.createPotion(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 15, 4, false, true))
        recipe = ShapedRecipe(convertToRecipeItem(deusExMachina, id)).shape(" E ", " H ", " P ")
            .setIngredient('E', Material.EMERALD)
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('P', Material.GLASS_BOTTLE)
    }
}
/*
 * Project: Kraftwerk
 * Class: CornucopiaRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class CornucopiaRecipe : Recipe(
    "Cornucopia",
    "3 Golden Carrots (Saturation I 10:00, Regeneration I 0:10)",
    ItemStack(Material.GOLDEN_CARROT, 3),
    1,
    "cornucopia"
) {
    init {
        val cornucopia = ItemBuilder(Material.GOLDEN_CARROT)
            .name("&eCornucopia")
            .setAmount(3)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(cornucopia, id)).shape("CCC", "CGC", "CCC")
            .setIngredient('C', Material.CARROT)
            .setIngredient('G', Material.GOLDEN_APPLE)
    }

    @EventHandler
    fun onItemConsume(e: PlayerItemConsumeEvent) {
        if (e.item.hasItemMeta() && e.item.itemMeta.displayName == Chat.colored("&eCornucopia")) {
            e.player.addPotionEffect(
                PotionEffect(PotionEffectType.SATURATION, (10 * 60) * 20, 0, true, true)
            )
            e.player.addPotionEffect(
                PotionEffect(PotionEffectType.REGENERATION, (10) * 20, 0, true, true)
            )
        }
    }
}
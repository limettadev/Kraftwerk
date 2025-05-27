/*
 * Project: Kraftwerk
 * Class: DragonSwordRecipe.kt
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
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe


class DragonSwordRecipe : Recipe(
    "Dragon Sword",
    "Weaponsmith Ultimate",
    ItemStack(Material.DIAMOND_SWORD),
    1,
    "dragon_sword"
) {
    init {
        var dragonSword = ItemBuilder(Material.DIAMOND_SWORD)
            .name("&eDragon Sword")
            .noAttributes()
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(dragonSword, id)).shape(" B ", " S ", "OBO")
            .setIngredient('B', Material.BLAZE_POWDER)
            .setIngredient('S', Material.DIAMOND_SWORD)
            .setIngredient('O', Material.OBSIDIAN)
    }

    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if (
            e.damager is Player &&
            (e.damager as Player).itemInHand != null &&
            (e.damager as Player).itemInHand.hasItemMeta() &&
            (e.damager as Player).itemInHand.itemMeta.displayName != null &&
            (e.damager as Player).itemInHand.itemMeta.displayName == Chat.colored("&eDragon Sword")
            ) {
            e.damage = e.damage + 2.0
        }
    }
}
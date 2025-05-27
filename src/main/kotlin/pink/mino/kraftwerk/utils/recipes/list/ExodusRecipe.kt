/*
 * Project: Kraftwerk
 * Class: ExodusRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class ExodusRecipe : Recipe(
    "Exodus",
    "Upon hitting a player with sword or bow, gain Regeneration I for 2.5 seconds, totals to 1 heart per hit.",
    ItemStack(Material.DIAMOND_HELMET),
    1,
    "exodus"
) {
    init {
        val exodus = ItemBuilder(Material.DIAMOND_HELMET)
            .name("&eExodus")
            .addEnchantment(Enchantment.DURABILITY, 3)
            .addLore("&7Upon hitting a player with sword or bow, gain Regeneration I for 2.5 seconds, totals to 1 heart per hit.")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(exodus, id)).shape("DDD", "DHD", "ECE")
            .setIngredient('D', Material.DIAMOND)
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('E', Material.EMERALD)
            .setIngredient('C', Material.GOLDEN_CARROT)
    }

    @EventHandler
    fun onPvP(e: EntityDamageByEntityEvent) {
        if (e.damager is Player && e.entity is Player) {
            if ((e.damager as Player).inventory.helmet != null && (e.damager as Player).inventory.helmet.hasItemMeta() && (e.damager as Player).inventory.helmet.itemMeta.displayName == Chat.colored(
                    "&eExodus"
                )
            ) {
                if (!(e.damager as Player).hasPotionEffect(PotionEffectType.REGENERATION)) {
                    (e.damager as Player).addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 50, 0))
                }
            }
        }
    }
}
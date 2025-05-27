/*
 * Project: Kraftwerk
 * Class: ModularBowRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder

class ModularBowRecipe : Recipe(
    "Modular Bow",
    "Left click to switch modes. Mode 1: Punch I, Mode 2: Poison I, Mode 3: Lightning (1.5 hearts)",
    ItemStack(Material.BOW),
    1,
    "modular_bow"
) {
    init {
        val modularBow = ItemBuilder(Material.BOW)
            .name("&eModular Bow")
            .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1)
            .addLore("&dLeft click to switch modes")
            .addLore("&7Mode 1: Punch I, Mode 2: Poison I, Mode 3: Lightning (1.5 hearts)")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(modularBow, id)).shape(" W ", "PBP", "ESE")
            .setIngredient('W', Material.WATCH)
            .setIngredient('P', Material.BLAZE_POWDER)
            .setIngredient('E', Material.SPIDER_EYE)
            .setIngredient('B', Material.BOW)
            .setIngredient('S', Material.SLIME_BALL)
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        try {
            if (e.item != null) {
                if (e.item.type == Material.BOW && e.item.hasItemMeta() && e.item.itemMeta.displayName == Chat.colored("&eModular Bow")) {
                    val bow = ItemBuilder(Material.BOW).name(Chat.colored("&eModular Bow (Punch)"))
                        .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1).make()
                    e.player.itemInHand = bow
                    Chat.sendMessage(e.player, "&eModular Bow: Mode switched to Punch.")
                }
                if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                    if (e.item.type == Material.BOW && e.item.hasItemMeta() && e.item.itemMeta.displayName == Chat.colored(
                            "&eModular Bow (Punch)"
                        )
                    ) {
                        val bow = ItemBuilder(Material.BOW).name(Chat.colored("&eModular Bow (Poison I)")).make()
                        e.player.itemInHand = bow
                        Chat.sendMessage(e.player, "&eModular Bow: Mode switched to Poison I.")
                    }
                    if (e.item.type == Material.BOW && e.item.hasItemMeta() && e.item.itemMeta.displayName == Chat.colored(
                            "&eModular Bow (Poison I)"
                        )
                    ) {
                        val bow = ItemBuilder(Material.BOW).name(Chat.colored("&eModular Bow (Lightning)")).make()
                        e.player.itemInHand = bow
                        Chat.sendMessage(e.player, "&eModular Bow: Mode switched to Lightning.")
                    }
                    if (e.item.type == Material.BOW && e.item.hasItemMeta() && e.item.itemMeta.displayName == Chat.colored(
                            "&eModular Bow (Lightning)"
                        )
                    ) {
                        val bow = ItemBuilder(Material.BOW).name(Chat.colored("&eModular Bow (Punch)"))
                            .addEnchantment(Enchantment.ARROW_KNOCKBACK, 1).make()
                        e.player.itemInHand = bow
                        Chat.sendMessage(e.player, "&eModular Bow: Mode switched to Punch.")
                    }
                }
            }
        } catch (e: NullPointerException) {
            return
        }
    }

    @EventHandler
    fun onModularBowShot(e: EntityDamageByEntityEvent) {
        if (e.damager.type == EntityType.ARROW && ((e.damager as Arrow).shooter) is Player && e.entity.type == EntityType.PLAYER) {
            if (((e.damager as Arrow).shooter as Player).itemInHand.type == Material.BOW && ((e.damager as Arrow).shooter as Player).itemInHand.itemMeta.displayName == Chat.colored(
                    "&eModular Bow (Poison I)"
                )
            ) {
                (e.entity as Player).addPotionEffect(PotionEffect(PotionEffectType.POISON, 20 * 3, 0, false, true))
            }
            if (((e.damager as Arrow).shooter as Player).itemInHand.type == Material.BOW && ((e.damager as Arrow).shooter as Player).itemInHand.itemMeta.displayName == Chat.colored(
                    "&eModular Bow (Lightning)"
                )
            ) {
                e.damager.world.strikeLightning(e.entity.location)
            }
        }
    }

}
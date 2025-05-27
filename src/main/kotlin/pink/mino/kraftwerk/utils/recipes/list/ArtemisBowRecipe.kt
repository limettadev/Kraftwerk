/*
 * Project: Kraftwerk
 * Class: ArtemisBowRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe
import kotlin.random.Random

class ArtemisBowRecipe : Recipe(
    "Artemis' Bow",
    "Has a chance for your arrow to home into your opponent!",
    ItemStack(Material.BOW),
    1,
    "artemis_bow"
) {
    init {
        val artemisBow = ItemBuilder(Material.BOW)
            .name("&5Artemis' Bow")
            .addLore("&7Has a chance for your arrow to home into your opponent!")
            .addEnchantment(Enchantment.ARROW_DAMAGE, 3)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(artemisBow, id)).shape("FDF", "FBF", "FEF")
            .setIngredient('F', Material.FEATHER)
            .setIngredient('D', Material.DIAMOND)
            .setIngredient('B', Material.BOW)
            .setIngredient('E', Material.EYE_OF_ENDER)
    }

    @EventHandler
    fun onShootEvent(e: EntityShootBowEvent) {
        if (e.entity is Player && e.bow != null && e.bow.hasItemMeta() && e.bow.itemMeta.displayName == ChatColor.translateAlternateColorCodes(
                '&',
                "&5Artemis' Bow"
            ) && Random.nextInt(100) <= 25
        ) {
            val arrow = e.projectile as Arrow
            (e.entity as Player).playSound(e.entity.location, Sound.WOOD_CLICK, 1f, 1f)
            object : BukkitRunnable() {
                override fun run() {
                    val target = e.entity.getNearbyEntities(200.0, 200.0, 200.0)
                        .firstOrNull { it is Player && it != ((e.projectile as Arrow).shooter as Player) } as? Player
                    if (arrow.isOnGround || arrow.isDead || target == null || target.isDead) {
                        cancel()
                        return
                    }
                    arrow.velocity = target.location.toVector().subtract(arrow.location.toVector()).normalize()
                }
            }.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 5L, 1L)
        }
    }
}
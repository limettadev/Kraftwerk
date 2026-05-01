/*
 * Project: Kraftwerk
 * Class: DaredevilRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.world.entity.LivingEntity
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeInstance
import org.bukkit.block.Block
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.entity.SkeletonHorse
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class DaredevilRecipe : Recipe(
    "Daredevil",
    "fast horse yay",
    ItemStack(Material.HORSE_SPAWN_EGG),
    1,
    "daredevil"
) {
    init {
        val daredevil = ItemStack(Material.HORSE_SPAWN_EGG)
        val daredevilMeta = daredevil.itemMeta
        daredevilMeta.displayName(Chat.colored("<dark_blue>Daredevil"))
        daredevil.itemMeta = daredevilMeta
        recipe = ShapedRecipe(convertToRecipeItem(daredevil, id)).shape("HS ", "BBB", "B B")
            .setIngredient('H', Material.PLAYER_HEAD, 3)
            .setIngredient('S', Material.SADDLE)
            .setIngredient('B', Material.BONE)
    }

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val player = event.whoClicked as Player
        val inv = event.inventory
        val item = inv.result!!
        if (!item.hasItemMeta() || !item.itemMeta.hasDisplayName()) {
            return
        }
        val name = item.itemMeta.displayName()
        if (name == Chat.colored("<yellow>Daredevil")) {
            val block: Block? = player.getTargetBlock(null as Set<Material?>?, 10)
            if (block == null || block.type !== Material.CRAFTING_TABLE) {
                player.sendMessage("${Chat.prefix} You are not looking at a crafting table.")
                event.isCancelled = true
                return
            }
            event.isCancelled = true
            event.view.topInventory.clear()
            block.type = Material.AIR
            val h = player.world.spawn(block.location, SkeletonHorse::class.java)
            h.customName(MiniMessage.miniMessage().deserialize("<yellow>Daredevil"))
            h.maxHealth = 50.0
            h.health = 50.0
            h.inventory.saddle = ItemBuilder(Material.SADDLE).make()
            h.isTamed = true
            h.owner = player
            h.jumpStrength = 1.0
            h.getAttribute(Attribute.MOVEMENT_SPEED)?.baseValue = 0.3375

        }
    }
}
/*
 * Project: Kraftwerk
 * Class: DaredevilRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import net.minecraft.server.v1_8_R3.AttributeInstance
import net.minecraft.server.v1_8_R3.EntityLiving
import net.minecraft.server.v1_8_R3.GenericAttributes
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Horse
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.material.SpawnEgg
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class DaredevilRecipe : Recipe(
    "Daredevil",
    "fast horse yay",
    SpawnEgg(EntityType.HORSE).toItemStack(1),
    1,
    "daredevil"
) {
    init {
        val daredevil = SpawnEgg(EntityType.HORSE).toItemStack(1)
        val daredevilMeta = daredevil.itemMeta
        daredevilMeta.displayName = ChatColor.DARK_BLUE.toString() + "Daredevil"
        daredevil.itemMeta = daredevilMeta
        recipe = ShapedRecipe(convertToRecipeItem(daredevil, id)).shape("HS ", "BBB", "B B")
            .setIngredient('H', Material.SKULL_ITEM, 3)
            .setIngredient('S', Material.SADDLE)
            .setIngredient('B', Material.BONE)
    }

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        val player = event.whoClicked as Player
        val inv = event.inventory
        val item = inv.result
        if (!item.hasItemMeta() || !item.itemMeta.hasDisplayName()) {
            return
        }
        val name = item.itemMeta.displayName
        if (name == Chat.colored("<yellow>Daredevil")) {
            val block: Block? = player.getTargetBlock(null as Set<Material?>?, 10)
            if (block == null || block.type !== Material.WORKBENCH) {
                player.sendMessage("${Chat.prefix} You are not looking at a crafting table.")
                event.isCancelled = true
                return
            }
            event.isCancelled = true
            event.view.topInventory.clear()
            block.type = Material.AIR
            val h = player.world.spawn(block.location, Horse::class.java)
            h.customName = Chat.colored("<yellow>Daredevil")
            h.variant = Horse.Variant.SKELETON_HORSE
            h.maxHealth = 50.0
            h.health = 50.0
            h.inventory.saddle = ItemBuilder(Material.SADDLE).make()
            h.isTamed = true
            h.owner = player
            h.jumpStrength = 1.0
            val speed: AttributeInstance = ((h as CraftEntity).handle as EntityLiving)
                .getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)
            speed.value = 0.5

        }
    }
}
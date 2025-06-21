/*
 * Project: Kraftwerk
 * Class: FusionArmorRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class FusionArmorRecipe : Recipe(
    "Fusion Armor",
    "Crafting this item will allow you to receive a random piece of armor with Protection IV.",
    ItemStack(Material.DIAMOND_HELMET),
    2,
    "fusion_armor"
) {
    init {
        val fusionArmor = ItemBuilder(Material.DIAMOND_HELMET)
            .name("&5Fusion Armor")
            .addLore("<gray>Crafting this item will allow you to receive a random piece of armor with &fProtection IV<gray>!")
            .make()
        recipe = ShapelessRecipe(fusionArmor)
            .addIngredient(Material.DIAMOND_HELMET)
            .addIngredient(Material.DIAMOND_CHESTPLATE)
            .addIngredient(Material.DIAMOND_LEGGINGS)
            .addIngredient(Material.DIAMOND_BOOTS)
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        e.whoClicked as Player
        val item = e.inventory.result
        val craftItem = CraftItemStack.asNMSCopy(item)
        if (craftItem.hasTag()) {
            val tag = craftItem.tag
            if (tag.getString("uhcId") != null) {
                if (tag.getString("uhcId") == "fusion_armor") {
                    val helmet = ItemBuilder(Material.DIAMOND_HELMET)
                        .name("&5Fusion Helmet")
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                        .make()
                    val chestplate = ItemBuilder(Material.DIAMOND_CHESTPLATE)
                        .name("&5Fusion Chestplate")
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                        .make()
                    val leggings = ItemBuilder(Material.DIAMOND_LEGGINGS)
                        .name("&5Fusion Leggings")
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                        .make()
                    val boots = ItemBuilder(Material.DIAMOND_BOOTS)
                        .name("&5Fusion Boots")
                        .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
                        .make()
                    e.inventory.result = arrayListOf(helmet, chestplate, leggings, boots).random()
                }
            }
        }
    }
}
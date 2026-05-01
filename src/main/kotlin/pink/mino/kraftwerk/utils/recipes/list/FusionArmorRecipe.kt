/*
 * Project: Kraftwerk
 * Class: FusionArmorRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.craftbukkit.inventory.CraftItemStack
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
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
            .name("<dark_purple>Fusion Armor")
            .addLore("<gray>Crafting this item will allow you to receive a random piece of armor with <white>Protection IV<gray>!")
            .make()
        recipe = ShapelessRecipe(fusionArmor)
            .addIngredient(Material.DIAMOND_HELMET)
            .addIngredient(Material.DIAMOND_CHESTPLATE)
            .addIngredient(Material.DIAMOND_LEGGINGS)
            .addIngredient(Material.DIAMOND_BOOTS)
    }

    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        val item = e.inventory.result ?: return
        val meta = item.itemMeta ?: return
        val key = NamespacedKey(JavaPlugin.getPlugin(Kraftwerk::class.java), "uhcId")
        val uhcId = meta.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return

        if (uhcId == "fusion_armor") {
            val helmet = ItemBuilder(Material.DIAMOND_HELMET)
                .name("<dark_purple>Fusion Helmet")
                .addEnchantment(Enchantment.PROTECTION, 4)
                .make()
            val chestplate = ItemBuilder(Material.DIAMOND_CHESTPLATE)
                .name("<dark_purple>Fusion Chestplate")
                .addEnchantment(Enchantment.PROTECTION, 4)
                .make()
            val leggings = ItemBuilder(Material.DIAMOND_LEGGINGS)
                .name("<dark_purple>Fusion Leggings")
                .addEnchantment(Enchantment.PROTECTION, 4)
                .make()
            val boots = ItemBuilder(Material.DIAMOND_BOOTS)
                .name("<dark_purple>Fusion Boots")
                .addEnchantment(Enchantment.PROTECTION, 4)
                .make()
            e.inventory.result = arrayListOf(helmet, chestplate, leggings, boots).random()
        }
    }
}
/*
 * Project: Kraftwerk
 * Class: ApprenticeSwordRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class ApprenticeSwordRecipe : Recipe(
    "Apprentice Sword",
    "Gains sharpness after some time",
    ItemStack(Material.IRON_SWORD),
    1,
    "apprentice_sword"
) {
    init {
        val apprenticeSword = ItemBuilder(Material.IRON_SWORD)
            .name("&5Apprentice Sword")
            .addLore("<gray>Gains &fSharpness I<gray> after 10 minutes<gray>.")
            .addLore("<gray>Gains &fSharpness II<gray> after 20 minutes<gray>.")
            .addLore("<gray>Gains &fSharpness III<gray> after 40 minutes<gray>.")
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(apprenticeSword, id)).shape(" R ", " I ", " R ")
            .setIngredient('I', Material.IRON_SWORD)
            .setIngredient('R', Material.REDSTONE_BLOCK)
        object : BukkitRunnable() {
            override fun run() {
                if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("champions"))) return
                if (Kraftwerk.instance.game == null) return
                for (player in Bukkit.getOnlinePlayers()) {
                    for (item in player.inventory.contents) {
                        if (item == null) continue
                        if (!item.hasItemMeta()) continue
                        if (Kraftwerk.instance.game!!.timer > (10 * 60)) {
                            if (item.itemMeta.displayName == Chat.colored("&5Apprentice Bow")) {
                                val bow = ItemBuilder(Material.BOW)
                                    .name("&5Apprentice Bow")
                                    .addLore("<gray>Gains &fPower I<gray> after 10 minutes<gray>.")
                                    .addLore("<gray>Gains &fPower II<gray> after 20 minutes<gray>.")
                                    .addLore("<gray>Gains &fPower III<gray> after 40 minutes<gray>.")
                                    .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                                    .make()
                                item.itemMeta = bow.itemMeta
                            }
                            if (item.itemMeta.displayName == Chat.colored("&5Apprentice Sword")) {
                                val sword = ItemBuilder(Material.IRON_SWORD)
                                    .name("&5Apprentice Sword")
                                    .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                                    .addLore("<gray>Gains &fSharpness I<gray> after 10 minutes<gray>.")
                                    .addLore("<gray>Gains &fSharpness II<gray> after 20 minutes<gray>.")
                                    .addLore("<gray>Gains &fSharpness III<gray> after 40 minutes<gray>.")
                                    .make()
                                item.itemMeta = sword.itemMeta
                            }
                        } else if (Kraftwerk.instance.game!!.timer > (20 * 60)) {
                            if (item.itemMeta.displayName == Chat.colored("&5Apprentice Bow")) {
                                val bow = ItemBuilder(Material.BOW)
                                    .name("&5Apprentice Bow")
                                    .addLore("<gray>Gains &fPower I<gray> after 10 minutes<gray>.")
                                    .addLore("<gray>Gains &fPower II<gray> after 20 minutes<gray>.")
                                    .addLore("<gray>Gains &fPower III<gray> after 40 minutes<gray>.")
                                    .addEnchantment(Enchantment.ARROW_DAMAGE, 2)
                                    .make()
                                item.itemMeta = bow.itemMeta
                            }
                            if (item.itemMeta.displayName == Chat.colored("&5Apprentice Sword")) {
                                val sword = ItemBuilder(Material.IRON_SWORD)
                                    .name("&5Apprentice Sword")
                                    .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                                    .addLore("<gray>Gains &fSharpness I<gray> after 10 minutes<gray>.")
                                    .addLore("<gray>Gains &fSharpness II<gray> after 20 minutes<gray>.")
                                    .addLore("<gray>Gains &fSharpness III<gray> after 40 minutes<gray>.")
                                    .make()
                                item.itemMeta = sword.itemMeta
                            }
                        } else if (Kraftwerk.instance.game!!.timer > (40 * 60)) {
                            if (item.itemMeta.displayName == Chat.colored("&5Apprentice Bow")) {
                                val bow = ItemBuilder(Material.BOW)
                                    .name("&5Apprentice Bow")
                                    .addLore("<gray>Gains &fPower I<gray> after 10 minutes<gray>.")
                                    .addLore("<gray>Gains &fPower II<gray> after 20 minutes<gray>.")
                                    .addLore("<gray>Gains &fPower III<gray> after 40 minutes<gray>.")
                                    .addEnchantment(Enchantment.ARROW_DAMAGE, 3)
                                    .make()
                                item.itemMeta = bow.itemMeta
                            }
                            if (item.itemMeta.displayName == Chat.colored("&5Apprentice Sword")) {
                                val sword = ItemBuilder(Material.IRON_SWORD)
                                    .name("&5Apprentice Sword")
                                    .addEnchantment(Enchantment.DAMAGE_ALL, 3)
                                    .addLore("<gray>Gains &fSharpness I<gray> after 10 minutes<gray>.")
                                    .addLore("<gray>Gains &fSharpness II<gray> after 20 minutes<gray>.")
                                    .addLore("<gray>Gains &fSharpness III<gray> after 40 minutes<gray>.")
                                    .make()
                                item.itemMeta = sword.itemMeta
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(Kraftwerk.instance, 0L, 50L)
    }
}
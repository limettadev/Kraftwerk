/*
 * Project: Kraftwerk
 * Class: DeusExMachinaRecipe.kt
 *
 * Copyright (c) 2023 Juan Pichardo (juanp)
 *
 */

package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.PotionBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class DeusExMachinaRecipe : Recipe(
    "Deus ex Machina",
    "Resistance V Potion - Takes half of your health to craft!",
    ItemStack(Material.POTION),
    1,
    "deus_ex_machina"
) {
    @EventHandler
    fun onCraft(e: CraftItemEvent) {
        val player = e.whoClicked as Player
        val item = e.inventory.result ?: return
        val meta = item.itemMeta ?: return
        val key = NamespacedKey(JavaPlugin.getPlugin(Kraftwerk::class.java), "uhcId")
        val uhcId = meta.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return

        if (uhcId == "deus_ex_machina") {
            player.health = player.health / 2
            Chat.sendMessage(player, "<yellow>Your health has been siphoned to create a Deus Ex Machina.")
        }
    }

    init {
        val deusExMachina =
            PotionBuilder.createPotion(PotionEffect(PotionEffectType.RESISTANCE, 20 * 15, 4, false, true))
        recipe = ShapedRecipe(convertToRecipeItem(deusExMachina, id)).shape(" E ", " H ", " P ")
            .setIngredient('E', Material.EMERALD)
            .setIngredient('H', Material.PLAYER_HEAD, 3)
            .setIngredient('P', Material.GLASS_BOTTLE)
    }
}
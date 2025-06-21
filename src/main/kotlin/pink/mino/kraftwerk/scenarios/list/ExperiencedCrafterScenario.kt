package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.CraftItemEvent
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat

class ExperiencedCrafterScenario : Scenario(
    "Experienced Crafter",
    "If you want to craft a certain item, it will cost you XP levels to craft it. An iron sword, bow, golden apple, or golden head, and a diamond pick costs 1 level. Crafting iron armor will cost 2 levels. Crafting an anvil or a diamond sword will cost 3 levels. Crafting diamond armor will cost 5 levels. Crafting an enchantment table will cost 6 levels. Lastly, crafting a brewing stand will cost 10 levels.",
    "experiencedcrafter",
    Material.WORKBENCH
) {
    @EventHandler
    fun onCraftItem(e: CraftItemEvent) {
        if (!enabled) return
        val player = e.whoClicked as? Player ?: return
        val item = e.recipe.result.type

        val cost = when (item) {
            Material.IRON_SWORD, Material.BOW, Material.GOLDEN_APPLE, Material.DIAMOND_PICKAXE -> 1
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS -> 2
            Material.ANVIL, Material.DIAMOND_SWORD -> 3
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS -> 5
            Material.ENCHANTMENT_TABLE -> 6
            Material.BREWING_STAND -> 10
            else -> return // No cost for other items
        }

        if (player.level < cost) {
            Chat.sendMessage(player, "<red>You need $cost experience levels to craft this item.")
            e.isCancelled = true
            return
        }

        // Delay level deduction to InventoryClickEvent, after the item is actually taken
        Bukkit.getScheduler().runTaskLater(Kraftwerk.instance, Runnable {
            if (e.isCancelled) return@Runnable
            player.level -= cost
            Chat.sendMessage(player, "<yellow>You spent $cost experience level${if (cost > 1) "s" else ""} crafting ${item.name.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }}.")
        }, 20L)
    }

}
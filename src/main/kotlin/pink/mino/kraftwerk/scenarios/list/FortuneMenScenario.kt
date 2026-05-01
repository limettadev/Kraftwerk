package pink.mino.kraftwerk.scenarios.list

import me.lucko.helper.Schedulers
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.PrepareItemCraftEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class FortuneMenScenario : Scenario(
    "Fortune Men",
    "All tools are enchanted with fortune 3.",
    "fortunemen",
    Material.GOLD_BLOCK
) {
    private var types: List<Material> = ArrayList(
        listOf(
            Material.WOODEN_PICKAXE,
            Material.STONE_PICKAXE,
            Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE,
            Material.DIAMOND_PICKAXE,
            Material.WOODEN_SHOVEL,
            Material.GOLDEN_SHOVEL,
            Material.IRON_SHOVEL,
            Material.STONE_SHOVEL,
            Material.DIAMOND_SHOVEL,
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE
        )
    )

    @EventHandler
    fun onCraft(e: PrepareItemCraftEvent) {
        if (!enabled) return
        if (GameState.currentState !== GameState.INGAME) return
        if (types.contains(e.recipe!!.result.type)) {
            Schedulers.sync().runLater(Runnable@ {
                val item = e.recipe!!.result
                item.addEnchantment(Enchantment.FORTUNE, 3)
                e.inventory.result = item
            }, 20L)
        }
    }
}
package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState

class TripleIronScenario : Scenario(
    "Triple Iron",
    "All iron ores are triple what they drop.",
    "tripleiron",
    Material.IRON_ORE
) {
    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        when (e.block.type) {
            Material.IRON_ORE -> {
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.IRON_ORE))
                e.block.world.dropItemNaturally(e.block.location, ItemStack(Material.IRON_ORE))
            }
            else -> {}
        }
    }
}
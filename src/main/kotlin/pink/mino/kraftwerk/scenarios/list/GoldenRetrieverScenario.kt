package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class GoldenRetrieverScenario : Scenario(
    "Golden Retriever",
    "Players drop golden heads when they die.",
    "goldenretriever",
    Material.GOLDEN_APPLE
) {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        val head = ItemStack(Material.GOLDEN_APPLE)
        val meta: ItemMeta = head.itemMeta
        meta.displayName(Chat.colored("<gold>Golden Head"))
        meta.lore(listOf(Chat.colored("<dark_purple>Some say consuming the head of a\nfallen foe strengthens the blood.")))
        head.itemMeta = meta
        e.drops.add(head)
    }
}
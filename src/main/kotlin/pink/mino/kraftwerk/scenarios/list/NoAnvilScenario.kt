package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.PrepareAnvilEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat

class NoAnvilScenario : Scenario(
    "No Anvil",
    "Anvils are disabled and unusable.",
    "noanvil",
    Material.ANVIL
) {
    @EventHandler
    fun onPrepareAnvil(e: PrepareAnvilEvent) {
        if (!enabled) return
        val player = e.view.player as? Player ?: return
        e.result = null
        Chat.sendMessage(player, "<red>You cannot use the anvil in this gamemode.")
    }
}
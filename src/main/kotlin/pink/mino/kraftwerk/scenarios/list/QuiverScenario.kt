package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import pink.mino.kraftwerk.scenarios.Scenario

class QuiverScenario : Scenario(
    "Quiver",
    "You are only permitted to carry only 16 arrows in your inventory at one time.",
    "quiver",
    Material.BOW
) {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        if (!enabled) return
        val player = event.whoClicked as? Player ?: return
        enforceArrowLimit(player)
    }

    @EventHandler
    fun onPickup(event: PlayerPickupItemEvent) {
        if (!enabled) return
        if (event.item.itemStack.type == Material.ARROW) {
            val player = event.player
            // Delay to let the pickup finish, then enforce
            player.server.scheduler.runTaskLater(
                pink.mino.kraftwerk.Kraftwerk.instance,
                { enforceArrowLimit(player) },
                1L
            )
        }
    }

    private fun enforceArrowLimit(player: Player) {
        val arrows = player.inventory.contents
            .filter { it != null && it.type == Material.ARROW }
            .toMutableList()
        var total = arrows.sumOf { it!!.amount }
        if (total <= 16) return

        var toRemove = total - 16
        for (item in arrows) {
            if (toRemove <= 0) break
            val remove = minOf(item!!.amount, toRemove)
            item.amount -= remove
            toRemove -= remove
            if (item.amount <= 0) {
                player.inventory.remove(item)
            }
        }
        player.updateInventory()
    }
}
package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import pink.mino.kraftwerk.scenarios.Scenario

class LifestealScenario : Scenario(
    "Lifesteal",
    "You gain half a heart to your max health after every kill.",
    "lifesteal",
    Material.GOLDEN_APPLE
) {
    private val maxHealthCap = 40.0 // 20 hearts

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if (!enabled) return
        val killer = event.entity.killer ?: return
        if (killer !is Player) return

        val currentMax = killer.maxHealth
        val newMax = (currentMax + 1.0).coerceAtMost(maxHealthCap)
        if (newMax > currentMax) {
            killer.maxHealth = newMax
        }
    }
}
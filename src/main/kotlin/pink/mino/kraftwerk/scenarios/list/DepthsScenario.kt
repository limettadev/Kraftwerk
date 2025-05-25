package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import pink.mino.kraftwerk.scenarios.Scenario

class DepthsScenario : Scenario(
    "Depths",
    "As your Y level goes down, the monsters will get stronger and stronger.",
    "depths",
    Material.STONE
) {
    @EventHandler
    fun onEntityDamageByEntity(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        val victim = e.entity
        val damager = e.damager
        if (victim !is Player || damager !is LivingEntity || damager is Player) return

        val y = victim.location.y
        val multiplier = when {
            y >= 60 -> 1.0
            y >= 45 -> 1.5
            y >= 30 -> 2.0
            y >= 15 -> 3.0
            else -> 5.0
        }

        e.damage = e.damage * multiplier
    }

}
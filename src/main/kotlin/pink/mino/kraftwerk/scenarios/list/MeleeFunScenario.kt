package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import pink.mino.kraftwerk.scenarios.Scenario

class MeleeFunScenario : Scenario(
    "Melee Fun",
    "There is no delay for when hitting a player, removes the noDamageTicks. So this means players can get insane combos! It is sort of like Combo UHC.",
    "meleefun",
    Material.IRON_SWORD
) {
    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if (!enabled) return
        val damager = event.damager
        val victim = event.entity
        if (damager is Player && victim is Player) {
            victim.noDamageTicks = 0
        }
    }
}
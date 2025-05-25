package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat

class CupidScenario : Scenario(
    "Cupid",
    "Every time you shoot and hit a player with your bow, you gain 1% of your health back.",
    "cupid",
    Material.BOW
) {
    @EventHandler
    fun onPlayerShoot(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (e.damager.type == EntityType.ARROW && ((e.damager as Arrow).shooter) is Player) {
            val shooter = ((e.damager as Arrow).shooter) as Player
            e.entity as Player
            shooter.health += 0.02
            Chat.sendMessage(shooter, "&a+1 health!")
        }
    }
}
package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat

class SwitcherooScenario : Scenario(
    "Switcheroo",
    "Shooting someone with a bow swaps your position with theirs.",
    "switcheroo",
    Material.ENDER_PEARL
) {
    @EventHandler
    fun onPlayerShoot(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (e.damager.type == EntityType.ARROW && ((e.damager as Arrow).shooter) is Player) {
            if (e.finalDamage >= (e.entity as Player).health) return
            val shooter = ((e.damager as Arrow).shooter) as Player
            val victim = e.entity as Player
            val shooterLoc = shooter.location
            val victimLoc = victim.location
            shooter.teleport(victimLoc)
            victim.teleport(shooterLoc)
            Chat.sendMessage(victim, "<dark_gray>[${Chat.primaryColor}Switcheroo<dark_gray>]<gray> You swapped positions with ${Chat.secondaryColor}${shooter.name}<gray>!")
            Chat.sendMessage(shooter, "<dark_gray>[${Chat.primaryColor}Switcheroo<dark_gray>]<gray> You swapped positions with ${Chat.secondaryColor}${victim.name}<gray>!")
        }
    }
}
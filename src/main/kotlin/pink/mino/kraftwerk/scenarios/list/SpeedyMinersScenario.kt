package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.scenarios.Scenario

class SpeedyMinersScenario : Scenario(
    "Speedy Miners",
    "Players that are under Y: 32 will receive Speed 1.",
    "speedyminers",
    Material.DIAMOND_PICKAXE
) {
    private val speedEffect = PotionEffect(PotionEffectType.SPEED, 60, 0, true, false)

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (!enabled) return
        handleSpeed(event.player)
    }

    @EventHandler
    fun onTeleport(event: PlayerTeleportEvent) {
        if (!enabled) return
        handleSpeed(event.player)
    }

    private fun handleSpeed(player: Player) {
        if (!enabled) return
        val y = player.location.y
        if (y < 32) {
            if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
                player.addPotionEffect(speedEffect)
            }
        } else {
            player.removePotionEffect(PotionEffectType.SPEED)
        }
    }
}
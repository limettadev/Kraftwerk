package pink.mino.kraftwerk.listeners

import org.bukkit.GameRules
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.WorldLoadEvent

class WorldInitializeListener : Listener {
    @EventHandler
    fun onWorldInitialize(e: WorldLoadEvent) {
        val world = e.world
        world.setGameRule(GameRules.ADVANCE_WEATHER, false)
        world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true)
        world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false)
        world.setGameRule(GameRules.NATURAL_HEALTH_REGENERATION, false)
        world.setGameRule(GameRules.LOCATOR_BAR, false)
    }
}
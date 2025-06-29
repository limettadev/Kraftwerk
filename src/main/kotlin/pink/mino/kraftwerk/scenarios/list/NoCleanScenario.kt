package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class NoCleanTask(val player: Player) : BukkitRunnable() {
    var timer = 15
    override fun run() {
        timer -= 1
        if (timer == 0) {
            cancel()
            NoCleanScenario.instance.noClean.remove(player)
            Chat.sendMessage(player, "<red>Your NoClean has expired!")
        }
    }
}

class NoCleanScenario : Scenario(
    "NoClean",
    "Players receive 15 seconds of invincibility from players after they kill players.",
    "noclean",
    Material.IRON_SWORD
) {
    companion object {
        val instance = NoCleanScenario()
    }
    val noClean: HashMap<Player, Boolean> = hashMapOf()

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity.killer != null) {
            Chat.sendMessage(e.entity.killer, "<red>You've been given 15 seconds of NoClean!")
            noClean[e.entity.killer] = true
            NoCleanTask(e.entity.killer).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        }
    }

    @EventHandler
    fun onPlayerDamage(e: org.bukkit.event.entity.EntityDamageEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity.type == EntityType.PLAYER && noClean[e.entity as Player] != null) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity.type == EntityType.PLAYER && e.damager.type == EntityType.PLAYER) {
            if (noClean[e.entity as Player] != null) {
                e.isCancelled = true
                Chat.sendMessage(e.damager as Player, "<red>${(e.entity as Player).name} is still on NoClean cooldown (${noClean[e.entity as Player]}s)")
            }
            if (noClean[e.damager as Player] != null) {
                noClean.remove(e.damager as Player)
                Chat.sendMessage(e.damager as Player, "<red>Your NoClean has been removed as you damaged another player!")
            }
        } else if ((e.entity.type == EntityType.PLAYER) && (e.damager.type === EntityType.ARROW) && ((e.damager as Arrow).shooter as Entity).type == EntityType.PLAYER) {
            if (noClean[e.entity as Player] != null) {
                e.isCancelled = true
                Chat.sendMessage(((e.damager as Arrow).shooter as Player), "<red>${(e.entity as Player).name} is still on NoClean cooldown (${noClean[e.entity as Player]}s)")
            }
            if (noClean[((e.damager as Arrow).shooter as Player)] != null) {
                noClean.remove(((e.damager as Arrow).shooter as Player))
                Chat.sendMessage(((e.damager as Arrow).shooter as Player), "<red>Your NoClean has been removed as you damaged another player!")
            }
        } else if (e.entity.type == EntityType.PLAYER && noClean[e.entity as Player] != null) {
            e.isCancelled = true
        }
    }
}
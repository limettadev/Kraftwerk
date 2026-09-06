package pink.mino.kraftwerk.listeners

import me.lucko.helper.Schedulers
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.commands.RespawnFeature
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.ScatterFeature
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.RedemptionScenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.Perk
import pink.mino.kraftwerk.utils.PerkChecker
import java.util.*

class DeathKick(val player: Player) : BukkitRunnable() {
    var timer = 60

    fun cancelDeathKick() {
        timer = 999999999
        Chat.sendMessage(player, "&cYour death kick has been cancelled.")
        cancel()
    }

    override fun run() {
        if (timer % 10 == 0) {
            Chat.sendMessage(player, "&cYou will be kicked in ${timer}s...")
        }
        if (player.isOnline == false) {
            cancel()
        }
        if (PerkChecker.checkPerks(player).contains(Perk.BYPASS_DEATH_KICK)) {
            cancel()
            Chat.sendMessage(player, "&cYour death kick has been cancelled to having bypass.")
        }
        timer--
        if (timer <= 0) {
            player.kickPlayer(Chat.colored(if (ConfigFeature.instance.config!!.getString("chat.deathKick") != null) ConfigFeature.instance.config!!.getString("chat.deathKick") else "no death kick message sent but... thanks for playing :3"))
            cancel()
        }
    }
}

class PlayerRespawnListener : Listener {
    companion object {
        val deathKicks = hashMapOf<UUID, DeathKick>()
    }

    @EventHandler
    fun onPlayerRespawn(e: PlayerRespawnEvent) {
        if (GameState.currentState == GameState.INGAME
            && ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("redemption"))
            && !e.player.hasPermission("uhc.staff")
            && !RedemptionScenario.trueDeadPlayers.contains(e.player.uniqueId)
        ) {
            ScatterFeature.scatterSolo(
                e.player,
                Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")),
                ConfigFeature.instance.data!!.getInt("pregen.border")
            )
            Schedulers.sync().runLater(runnable@{
                e.player.inventory.contents = RespawnFeature.instance.inventory[e.player.uniqueId] ?: e.player.inventory.contents
                Chat.sendMessage(e.player, "${Chat.prefix} You've been rescattered. Don't die again!")
            }, 1L)
            return
        }

        if (e.player.hasPermission("uhc.staff")) {
            e.respawnLocation = e.player.location
        } else {
            e.respawnLocation = SpawnFeature.instance.spawnLocation
        }
        Schedulers.sync().runLater(runnable@ {
            if (!e.player.hasPermission("uhc.staff")) {
                SpawnFeature.instance.send(e.player)
            } else {
                SpecFeature.instance.spec(e.player)
            }
        }, 1L)
        if (!ConfigOptionHandler.getOption("private")!!.enabled) {
            deathKicks[e.player.uniqueId] = DeathKick(e.player)
            deathKicks[e.player.uniqueId]!!.runTaskTimer(Kraftwerk.instance, 0L, 20L)
        }
    }
}
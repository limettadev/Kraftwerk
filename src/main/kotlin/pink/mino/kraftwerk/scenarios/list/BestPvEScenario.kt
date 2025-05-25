package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*

class BestPvEIterator : BukkitRunnable() {
    var timer = 600
    override fun run() {
        timer -= 1
        if (timer == 0) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.getSpecs().contains(player.name) && (ScenarioHandler.getScenario("bestpve") as BestPvEScenario).list.contains(player.uniqueId)) {
                    player.maxHealth += 2.0
                }
            }
            timer = 600
        }
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("bestpve"))) {
            cancel()
        }
        if (GameState.currentState != GameState.INGAME) {
            cancel()
        }
    }
}

class BestPvEScenario : Scenario(
    "Best PvE",
    "Every 10 minutes, if you have not taken damage yet, you will gain a heart. If you take damage, then you are off the list. The only way to get back onto the list is to kill a player.",
    "bestpve",
    Material.GOLDEN_CARROT
) {
    var task: BestPvEIterator? = null
    var list: ArrayList<UUID> = arrayListOf()
    override fun returnTimer(): Int? {
        return if (task != null) {
            task!!.timer
        } else {
            null
        }
    }

    override fun givePlayer(player: Player) {
        list.add(player.uniqueId)
        Chat.sendMessage(player, "${Chat.prefix} You've been added to the &eBest PvE&7 list!")
    }

    override fun onStart() {
        task = BestPvEIterator()
        task!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                list.add(player.uniqueId)
                Chat.sendMessage(player, "${Chat.prefix} You've been added to the &eBest PvE&7 list!")
            }
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (!enabled && GameState.currentState != GameState.INGAME) return
        if (e.entity is Player && e.finalDamage >= 0.0 && list.contains(e.entity.uniqueId)) {
            list.remove(e.entity.uniqueId)
            Chat.sendMessage(e.entity, "${Chat.prefix} You've been removed from the &eBest PvE&7 list for taking damage!")
        }
    }
}
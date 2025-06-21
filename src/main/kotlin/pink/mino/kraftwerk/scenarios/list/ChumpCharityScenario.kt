package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class ChumpCharityIterator : BukkitRunnable() {
    var timer = 600
    override fun run() {
        timer -= 1
        if (timer == 0) {
            if (WeakestLinkScenario().isAllSameHealth()) {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${Chat.secondaryColor}Everyone<gray> has the same health, not granting a heal."))
            } else {
                val player = WeakestLinkScenario().getLowestHealth()
                player.health = player.maxHealth
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${Chat.secondaryColor}${player.name}<gray> has the least health, they've been granted a &6heal<gray>!"))
            }
            timer = 600
        }
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("chumpcharity"))) {
            cancel()
        }
        if (GameState.currentState != GameState.INGAME) {
            cancel()
        }
    }
}

class ChumpCharityScenario : Scenario(
    "Chump Charity",
    "Every 10 minutes, the person who is on the lowest health gets fully healed.",
    "chumpcharity",
    Material.GOLDEN_APPLE
) {

    var task: ChumpCharityIterator? = null
    override fun returnTimer(): Int? {
        if (this.task == null) return null
        return this.task!!.timer
    }

    override fun onStart() {
        task = ChumpCharityIterator()
        task!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
    }
}
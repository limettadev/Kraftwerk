package pink.mino.kraftwerk.features

import me.lucko.spark.api.statistic.StatisticWindow.TicksPerSecond
import me.lucko.spark.api.statistic.types.DoubleStatistic
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat


class TabFeature : BukkitRunnable() {
    fun checkTps(tps: Double): String {
        return when {
            tps >= 19.0 -> "§a$tps"
            tps >= 16.0 -> "§e$tps"
            tps >= 10.0 -> "§c$tps"
            else -> "§4$tps"
        }
    }


    fun checkPing(ping: Int): String {
        return if (ping < 50) {
            "§a" + ping.toString()
        } else if (ping < 100) {
            "§e" + ping.toString()
        } else if (ping < 200) {
            "§c" + ping.toString()
        } else if (ping < 500) {
            "§4" + ping.toString()
        } else {
            "§8" + ping.toString()
        }
    }

    fun scenarioTextWrap(text: String, width: Int): ArrayList<String> {
        val words = text.split(" ")
        val lines = ArrayList<String>()
        var currentLine = ""
        for (word in words) {
            if (currentLine.length + word.length + 1 > width) {
                lines.add(Chat.colored("&f${currentLine}"))
                currentLine = "&f$word "
            } else {
                currentLine += "&f$word "
            }
        }
        lines.add(Chat.colored("&f${currentLine}"))
        return lines
    }

    fun sendTablist(p: Player) {
        val player = p
        val tps: DoubleStatistic<TicksPerSecond>? = JavaPlugin.getPlugin(Kraftwerk::class.java).spark.tps()
        val tpsLast10Secs = tps!!.poll(TicksPerSecond.SECONDS_10)
        val scenarios = ArrayList<String>()
        for (scenario in ScenarioHandler.getActiveScenarios()) {
            scenarios.add(scenario.name)
        }
        if (scenarios.isEmpty()) {
            scenarios.add("Vanilla+")
        }
        val header = "<color:${Chat.primaryColor}>${Chat.scoreboardTitle}</color>\n" +
            "<gray>TPS: ${checkTps(Math.round(tpsLast10Secs * 100.0) / 100.0)} <dark_gray>|</dark_gray> Ping: <white>${checkPing(player.ping)}ms</white></gray>" +
            (if (!ConfigOptionHandler.getOption("nobranding")!!.enabled) "\n<blue>/discord</blue>" else "")
        var game = "${ConfigFeature.instance.data!!.getString("game.host")}'s ${ConfigFeature.instance.data!!.getString("matchpost.team")}"
        if (ConfigFeature.instance.data!!.getString("matchpost.team") == null) {
            game = "Not set"
        }
        val footer = if (!ConfigOptionHandler.getOption("nobranding")!!.enabled) {
            "<gray>Game: <color:${Chat.secondaryColor}>${game}</color>\nScenarios: <color:${Chat.secondaryColor}>${
                scenarioTextWrap(scenarios.joinToString(", "), 40).joinToString("\n")
            }</color></gray>"
        } else {
            "<gray>Scenarios: <color:${Chat.secondaryColor}>${
                scenarioTextWrap(scenarios.joinToString(", "), 40).joinToString("\n")
            }</color></gray>"
        }
        player.sendPlayerListHeader(MiniMessage.miniMessage().deserialize(header))
        player.sendPlayerListFooter(MiniMessage.miniMessage().deserialize(footer))
    }

    override fun run() {
        for (p in Bukkit.getOnlinePlayers()) {
            sendTablist(p)
        }
    }
}
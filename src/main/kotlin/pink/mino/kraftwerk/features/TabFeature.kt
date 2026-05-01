package pink.mino.kraftwerk.features

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat


class TabFeature : BukkitRunnable() {
    fun checkTps(tps: Double): String {
        return when {
            tps >= 19.0 -> "<green>$tps"
            tps >= 16.0 -> "<yellow>$tps"
            tps >= 10.0 -> "<red>$tps"
            else -> "<dark_red>$tps"
        }
    }


    fun checkPing(ping: Int): String {
        return if (ping < 50) {
            "<green>" + ping.toString()
        } else if (ping < 100) {
            "<yellow>" + ping.toString()
        } else if (ping < 200) {
            "<red>" + ping.toString()
        } else if (ping < 500) {
            "<dark_red>" + ping.toString()
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
                lines.add("<white>${currentLine}")
                currentLine = "<white>$word "
            } else {
                currentLine += "<white>$word "
            }
        }
        lines.add("<white>${currentLine}")
        return lines
    }

    fun sendTablist(p: Player) {
        val player = p
        val tps = Bukkit.getServer().tps[0]
        val scenarios = ArrayList<String>()
        for (scenario in ScenarioHandler.getActiveScenarios()) {
            scenarios.add(scenario.name)
        }
        if (scenarios.isEmpty()) {
            scenarios.add("Vanilla+")
        }
        val header = "${Chat.primaryColor}${Chat.scoreboardTitle}\n" +
            "<gray>TPS: ${checkTps(Math.round(tps * 100.0) / 100.0)} <dark_gray>|</dark_gray> <gray>Ping: <white>${checkPing(player.ping)}ms</white></gray>" +
            (if (!ConfigOptionHandler.getOption("nobranding")!!.enabled) "\n<blue>/discord</blue>" else "")
        var game = "${ConfigFeature.instance.data!!.getString("game.host")}'s ${ConfigFeature.instance.data!!.getString("matchpost.team")}"
        if (ConfigFeature.instance.data!!.getString("matchpost.team") == null) {
            game = "Not set"
        }
        val footer = if (!ConfigOptionHandler.getOption("nobranding")!!.enabled) {
            "<gray>Game: ${Chat.secondaryColor}${game}\nScenarios: ${Chat.secondaryColor}${
                scenarioTextWrap(scenarios.joinToString(", "), 40).joinToString("\n")
            }</gray>"
        } else {
            "<gray>Scenarios: ${Chat.secondaryColor}${
                scenarioTextWrap(scenarios.joinToString(", "), 40).joinToString("\n")
            }</gray>"
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
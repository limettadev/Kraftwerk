package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.MiscUtils

class TimersCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game == null) {
            Chat.sendMessage(sender, "<red>No game is running!")
            return true
        }
        val valid = arrayListOf<Scenario>()
        Chat.sendMessage(sender, Chat.line)
        Chat.sendMessage(sender, "${Chat.primaryColor}<bold>Scenario Timers")
        for (scenario in ScenarioHandler.getActiveScenarios()) {
            if (scenario.returnTimer() != null) {
                valid.add(scenario)
                Chat.sendMessage(sender, "<gray>${scenario.name} <dark_gray>- ${Chat.secondaryColor}${MiscUtils.timeToString(scenario.returnTimer()!!.toLong())}")
            }
        }
        if (valid.isEmpty()) {
            Chat.sendMessage(sender, "<red>No valid scenarios with timers are running.")
        }
        Chat.sendMessage(sender, Chat.line)
        return true
    }

}
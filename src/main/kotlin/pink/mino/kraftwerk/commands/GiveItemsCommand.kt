package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat

class GiveItemsCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command!")
                return false
            }
        }
        val target = Bukkit.getPlayer(args[0])
        if (target == null) {
            Chat.sendMessage(sender, "<red>Player not found!")
            return false
        }
        for (scenario in ScenarioHandler.getActiveScenarios()) {
            scenario.givePlayer(target)
        }
        Chat.sendMessage(sender, "${Chat.prefix} Given items to ${Chat.secondaryColor}${target.name}<gray>.")
        Chat.sendMessage(target, "${Chat.prefix} You received items from ${Chat.secondaryColor}${sender.name}<gray>.")
        return true
    }

}
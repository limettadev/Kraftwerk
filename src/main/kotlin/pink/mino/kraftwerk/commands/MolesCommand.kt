package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.MolesScenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class MolesCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            Chat.sendMessage(sender, "You probably shouldn't use this command as you aren't a player.")
            return false
        }
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles"))) {
            Chat.sendMessage(sender, "${Chat.prefix} ${Chat.primaryColor}Moles<gray> isn't enabled!")
            return false
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "${Chat.prefix} ${Chat.primaryColor}Moles<gray> isn't available right now!")
            return false
        }
        if (MolesScenario.instance.moles[sender.uniqueId] == null) {
            Chat.sendMessage(sender, "${Chat.prefix} <gray>You aren't a mole!")
            return false
        }
        Chat.sendMessage(sender, Chat.line)
        Chat.sendCenteredMessage(sender, "${Chat.primaryColor}&lMoles Help")
        Chat.sendMessage(sender, "${Chat.prefix} ${Chat.secondaryColor}/molekit [kit] <dark_gray>-<gray> Chooses a mole kit.")
        Chat.sendMessage(sender, "${Chat.prefix} ${Chat.secondaryColor}/mcl <dark_gray>-<gray> Sends your location out to other moles.")
        Chat.sendMessage(sender, "${Chat.prefix} ${Chat.secondaryColor}/mcc <message> <dark_gray>-<gray> Message other moles.")
        Chat.sendMessage(sender, "${Chat.prefix} ${Chat.secondaryColor}/mcp <dark_gray>-<gray> View the list of other moles.")
        Chat.sendMessage(sender, Chat.line)
        return true
    }
}
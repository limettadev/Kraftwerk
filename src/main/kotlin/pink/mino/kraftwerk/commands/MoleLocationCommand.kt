package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.MolesScenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils
import kotlin.math.roundToInt

class MoleLocationCommand : CommandExecutor {
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
        val x = (sender.location.x * 100.0).roundToInt() / 100.0
        val y = (sender.location.y * 100.0).roundToInt() / 100.0
        val z = (sender.location.z * 100.0).roundToInt() / 100.0
        MolesScenario.instance.sendMoles("<dark_gray>[${Chat.primaryColor}Mole Chat<dark_gray>]&f ${PlayerUtils.getPrefix(sender)}${sender.name}<gray>'s location: ${Chat.secondaryColor}${x}, ${y}, ${z}<gray>. <dark_gray>| <gray>Dimension: ${Chat.primaryColor}${sender.location.world.worldType}<gray>.")
        return true
    }
}
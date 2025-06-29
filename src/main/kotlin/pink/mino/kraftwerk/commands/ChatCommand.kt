package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.MolesScenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class ChatCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you aren't a player.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Usage: /chat <moles/staff/team/public/spec>")
            Chat.sendMessage(sender, "${Chat.prefix} Your chat mode ${Chat.dash} ${Chat.secondaryColor}${JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.chatMode}")
            return true
        }
        JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!
        if (args[0] == "moles" || args[0] == "mole" || args[0] == "m") {
            if (GameState.currentState != GameState.INGAME) {
                Chat.sendMessage(sender, "<red>There is no game active.")
                return false
            }
            if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles"))) {
                Chat.sendMessage(sender, "<red>Moles is not enabled.")
                return false
            }
            if (MolesScenario.instance.moles[sender.uniqueId] == null) {
                Chat.sendMessage(sender, "<red>You aren't a mole!")
                return false
            }
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.chatMode = "MOLES"
            Chat.sendMessage(sender, "${Chat.prefix} Successfully set your chat mode to <dark_gray>'${Chat.secondaryColor}MOLES<dark_gray>'")
        } else if (args[0] == "staff" || args[0] == "s") {
            if (!sender.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.chatMode = "STAFF"
            Chat.sendMessage(sender, "${Chat.prefix} Successfully set your chat mode to <dark_gray>'${Chat.secondaryColor}STAFF<dark_gray>'")
        } else if (args[0] == "all" || args[0] == "global" || args[0] == "public" || args[0] == "g" || args[0] == "p") {
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.chatMode = "PUBLIC"
            Chat.sendMessage(sender, "${Chat.prefix} Successfully set your chat mode to <dark_gray>'${Chat.secondaryColor}PUBLIC<dark_gray>'")
        } else if (args[0] == "spec" || args[0] == "spectator"|| args[0] == "sc" || args[0] == "sp") {
            if (!SpecFeature.instance.isSpec(sender)) {
                Chat.sendMessage(sender, "<red>You are not a Spectator.")
                return false
            }
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.chatMode = "SPEC"
            Chat.sendMessage(sender, "${Chat.prefix} Successfully set your chat mode to <dark_gray>'${Chat.secondaryColor}SPEC<dark_gray>'")
        } else if (args[0] == "team" || args[0] == "t" || args[0] == "pm") {
            if (TeamsFeature.manager.getTeam(sender) == null) {
                Chat.sendMessage(sender, "<red>You are not in a team.")
                return false
            }
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.chatMode = "TEAM"
            Chat.sendMessage(sender, "${Chat.prefix} Successfully set your chat mode to <dark_gray>'${Chat.secondaryColor}TEAM<dark_gray>'")
        }
        return true
    }
}
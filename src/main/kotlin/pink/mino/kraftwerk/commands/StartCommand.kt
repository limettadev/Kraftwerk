package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.UHCFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class StartCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.start")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            sender.sendMessage(Chat.colored("${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/start <ffa/teams>"))
            return false
        }
        if (GameState.currentState != GameState.LOBBY) {
            Chat.sendMessage(sender, "<red>A game is currently occurring right now.")
            return false
        }
        if (ConfigFeature.instance.data!!.getString("pregen.world") == null) {
            Chat.sendMessage(sender, "<red>There is no world currently setup.")
            return false
        }
        if (args[0] == "ffa" || args[0] == "teams") {
            UHCFeature().start(args[0])
        } else {
            sender.sendMessage(Chat.colored("${Chat.prefix} Invalid mode: ${Chat.secondaryColor}/start <ffa/teams>"))
            return false
        }
        return true
    }

}
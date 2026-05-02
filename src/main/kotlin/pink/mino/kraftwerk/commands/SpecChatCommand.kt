package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat


//internal class SpecMessage(var uuid: UUID, var username: String, var message: String)

class SpecChatCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (SpecFeature.instance.getSpecs().contains(sender.name)) {
            val message = StringBuilder()
            if (args.isEmpty()) {
                sender.sendMessage("<red>Usage: /sc <message>")
                return true
            }
            for (element in args) {
                message.append("<gray>${element}").append(" " + "<gray>")
            }
            val msg = message.toString().trim()
            SpecFeature.instance.specChat("<dark_gray>[${Chat.primaryColor}Spec Chat<dark_gray>] <white>${sender.name} ${Chat.dash} <white>${msg}")
        } else {
            sender.sendMessage(Chat.colored("<red>You aren't in spectator mode!"))
            return false
        }
        return true
    }

}
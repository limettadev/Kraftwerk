package pink.mino.kraftwerk.commands

import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class TeleportPositionCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you aren't technically a player.")
            return false
        }
        if (!sender.hasPermission("uhc.staff.tp")) {
            Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/tppos <x> <y> <z><gray>.")
            return false
        }
        if (args.size != 3) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/tppos <x> <y> <z><gray>.")
            return false
        }
        if (
            args[0].toDoubleOrNull() == null ||
            args[1].toDoubleOrNull() == null ||
            args[2].toDoubleOrNull() == null
        ) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/tppos <x> <y> <z><gray>.")
            return false
        }
        val location = Location(sender.world, args[0].toDouble(), args[1].toDouble(), args[2].toDouble())
        sender.teleport(location)
        Chat.sendMessage(sender, "${Chat.prefix} Teleported to ${Chat.secondaryColor}${args[0]}<gray>, ${Chat.secondaryColor}${args[1]}<gray>, ${Chat.secondaryColor}${args[2]}<gray>.")
        return true
    }
}
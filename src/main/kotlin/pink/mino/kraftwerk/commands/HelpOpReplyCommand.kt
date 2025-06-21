package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.HelpOp

class HelpOpReplyCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.hr")) {
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty() || args.size <= 1) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/hr <id> <reply><gray>.")
            return false
        }
        if (args[0].toIntOrNull() == null) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid ID: ${Chat.secondaryColor}/hr <id> <reply><gray>.")
            return false
        }
        val message = StringBuilder()
        for ((index, element) in args.withIndex()) {
            if (index != 0) message.append("${ChatColor.GRAY}${element}").append(" " + ChatColor.GRAY)
        }
        val msg = message.toString().trim()
        val player = HelpOp.getHelpop(args[0].toInt())
        if (player == null) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid ID: ${Chat.secondaryColor}/hr <id> <reply><gray>.")
            return false
        }
        Chat.sendMessage(player, "<dark_gray>[${Chat.primaryColor}Help-OP<dark_gray>]${Chat.secondaryColor} ${sender.name}<gray> replied with ${Chat.dash} &f&o${msg}")
        Chat.sendMessage(sender, "<dark_gray>[${Chat.primaryColor}Help-OP<dark_gray>]<gray> Successfully responded to ${Chat.secondaryColor}${player.name} ${Chat.dash} &f&o${msg}")
        HelpOp.answered(args[0].toInt())
        for (p in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.getSpecs().contains(p.name) && p != sender) {
                if (player.name !== sender.name) Chat.sendMessage(p, "<dark_gray>[${Chat.primaryColor}Help-OP<dark_gray>]${Chat.secondaryColor} ${sender.name}<gray> responded to ${Chat.secondaryColor}${player.name}<gray> ${Chat.dash} &f&o${msg}")
            }
        }
        return true
    }

}
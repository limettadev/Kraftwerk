package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.HelpOp

class HelpopListCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        cmd: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.hl")) {
                Chat.sendMessage(sender, "${Chat.prefix} ${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        Chat.sendMessage(sender, "<red>Unanswered Help-Ops:")
        val count = HelpOp.getHelpops()
        if (count == 0) {
            Chat.sendMessage(sender, "<red>There are no unanswered Help-Ops.")
            return false
        }
        for (i in 1..count) {
            if (HelpOp.isHelpopAnswered(i)) {
                continue
            }
            if (sender is Player) {
                val text = net.kyori.adventure.text.Component.text()
                    .append(Chat.colored(" ${Chat.dash} <dark_gray>[${Chat.primaryColor}#${i}<dark_gray>] ${Chat.secondaryColor}${HelpOp.helpop[i]?.name} ${Chat.dash}<gray> ${HelpOp.helpopContent[i]}"))
                    .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand("/hr ${i} "))
                    .build()
                sender.sendMessage(text)
            } else {
                sender.sendMessage(Chat.colored(" ${Chat.dash} <dark_gray>[${Chat.primaryColor}#${i}<dark_gray>] ${Chat.secondaryColor}${HelpOp.helpop[i]!!.name} ${Chat.dash}<gray> ${HelpOp.helpopContent[i]}"))
            }
        }
        return true
    }
}
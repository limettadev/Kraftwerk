package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class ExtendCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.hasPermission("uhc.staff.extend")) {
            Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
            return true
        }

        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Usage: ${Chat.secondaryColor}/extend <minutes><gray>.")
            return true
        }

        if (Kraftwerk.instance.opening != null) {
            Kraftwerk.instance.opening!!.closing = (args[0].toInt() * 60).toLong()
            Kraftwerk.instance.opening!!.timer = 0
            Chat.broadcast("${Chat.prefix} The opening timer has been extended for ${Chat.secondaryColor}${args[0]} minutes<gray>.")
        } else {
            Kraftwerk.instance.opening = Opening((args[0].toInt() * 60).toLong())
            Kraftwerk.instance.opening!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
            Chat.broadcast("${Chat.prefix} The opening timer has been extended for ${Chat.secondaryColor}${args[0]} minutes<gray>.")
        }
        return true
    }
}
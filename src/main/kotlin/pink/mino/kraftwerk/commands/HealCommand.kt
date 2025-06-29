package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class HealCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.heal")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendMessage("You can't use this command as you technically aren't a player.")
                return false
            }
            sender.health = sender.maxHealth
            Chat.sendMessage(sender, "${Chat.prefix} <gray>You have healed yourself.")
            return true
        } else {
            if (args[0] == "*" || args[0] == "all") {
                for (online in ArrayList(Bukkit.getServer().onlinePlayers)) {
                    online.health = online.maxHealth
                    Chat.sendMessage(online, "${Chat.prefix} <gray>You have been healed by ${Chat.primaryColor}${sender.name}§7.")
                }
                Chat.sendMessage(sender as Player, "${Chat.prefix} <gray>You've healed all players.")
                return true
            } else {
                val target = Bukkit.getServer().getPlayer(args[0])
                if (target == null) {
                    Chat.sendMessage(sender as Player,"${Chat.prefix} <red>That player is not online or has never logged onto the server.")
                    return true
                }
                target.health = target.maxHealth
                Chat.sendMessage(target, "${Chat.prefix} <gray>You've been healed by ${Chat.primaryColor}${sender.name}<gray>.")
                Chat.sendMessage(sender as Player, "${Chat.prefix} <gray>Healed ${Chat.primaryColor}${target.name}<gray>.")
                return true
            }
        }
    }

}
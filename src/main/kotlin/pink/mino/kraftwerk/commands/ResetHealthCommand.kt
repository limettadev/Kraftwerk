package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class ResetHealthCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.resethealth")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            if (sender is Player) {
                sender.maxHealth = 20.0
                sender.health = 20.0
                Chat.sendMessage(sender, "${Chat.prefix} Your health has been reset.")
            } else {
                sender.sendMessage("You can't use this command as you technically aren't a player.")
            }
            return true
        } else {
            if (args[0] == "*") {
                for (online in ArrayList(Bukkit.getServer().onlinePlayers)) {
                    online.maxHealth = 20.0
                    online.health = 20.0
                    Chat.sendMessage(online, "${Chat.prefix} <gray>Your health has been reset by ${Chat.primaryColor}${sender.name}<gray>.")
                }
                Chat.sendMessage(sender as Player, "${Chat.prefix} <gray>Reset the health of all players.")
                return true
            } else {
                val target = Bukkit.getServer().getPlayer(args[0])
                if (target == null) {
                    Chat.sendMessage(sender as Player, "${Chat.prefix} <red>That player is not online or has never logged onto the server.")
                    return false
                }
                val effects = target.activePotionEffects
                for (effect in effects) {
                    target.removePotionEffect(effect.type)
                }
                Chat.sendMessage(sender as Player, "${Chat.prefix} <gray>Cleared the effects of ${Chat.primaryColor}${target.name}<gray>.")
                Chat.sendMessage(target, "${Chat.prefix} <gray>Your effects have been cleared by ${Chat.primaryColor}${sender.name}<gray>.")
                return true
            }
        }
    }

}
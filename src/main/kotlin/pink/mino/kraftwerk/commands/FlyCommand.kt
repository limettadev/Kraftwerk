package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.Perk
import pink.mino.kraftwerk.utils.PerkChecker

class FlyCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            Chat.sendMessage(sender, "<red>You can't use this command!")
            return false
        }
        if (sender.hasPermission("uhc.staff.fly")) {
            if (args.isEmpty()) {
                if (sender !is Player) {
                    sender.sendMessage("You can't use this command as you technically aren't a player.")
                    return false
                }
                val player = sender
                return if (!player.allowFlight) {
                    player.allowFlight = true
                    player.isFlying = true
                    Chat.sendMessage(player, "${Chat.prefix} <gray>You have <green>enabled<gray> flight for yourself.")
                    true
                } else {
                    player.isFlying = false
                    player.allowFlight = false
                    Chat.sendMessage(player, "${Chat.prefix} <gray>You have <red>disabled<gray> flight for yourself.")
                    true
                }

            } else {
                if (!sender.hasPermission("uhc.admin.fly")) {
                    Chat.sendMessage(sender, "${Chat.prefix} <red>You do not have permission to set flight to other players.")
                    return false
                }
                val target = Bukkit.getServer().getPlayer(args[0])
                if (target == null) {
                    Chat.sendMessage(sender, "${Chat.prefix} <red>That player is not online or has never logged onto the server.")
                    return false
                }
                return if (!target.allowFlight) {
                    target.allowFlight = true
                    target.isFlying = true
                    Chat.sendMessage(target, "${Chat.prefix} <gray>Your flight has been enabled by ${Chat.primaryColor}${sender.name}<gray>.")
                    Chat.sendMessage(sender, "${Chat.prefix} <gray>Enabled ${Chat.primaryColor}${target.name}'s<gray> flight.")
                    true
                } else {
                    target.allowFlight = false
                    target.isFlying = false
                    Chat.sendMessage(target, "${Chat.prefix} <gray>Your flight has been disabled by ${Chat.primaryColor}${sender.name}<gray>.")
                    Chat.sendMessage(sender, "${Chat.prefix} <gray>Disabled ${Chat.primaryColor}${target.name}'s<gray> flight.")
                    true
                }
            }
        } else {
            if (PerkChecker.checkPerks(sender).contains(Perk.SPAWN_FLY)) {
                if (sender.world.name != "Spawn" || GameState.currentState != GameState.LOBBY) {
                    Chat.sendMessage(sender, "<dark_gray>[&2$$$<dark_gray>] You can only use this command in the spawn world while a game isn't running.")
                    return false
                }
                if (sender.allowFlight) {
                    sender.allowFlight = true
                    sender.isFlying = true
                    Chat.sendMessage(sender, "<dark_gray>[&2$$$<dark_gray>] <gray>You have <green>enabled<gray> flight for yourself.")
                } else {
                    sender.allowFlight = false
                    sender.isFlying = false
                    Chat.sendMessage(sender, "<dark_gray>[&2$$$<dark_gray>] <gray>You have <red>disabled<gray> flight for yourself.")
                }
                return true
            } else {
                Chat.sendMessage(sender, "<red>Donator ranks can fly in spawn. Buy it at the store <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config tough tits"}")
                return false
            }
        }
    }
}
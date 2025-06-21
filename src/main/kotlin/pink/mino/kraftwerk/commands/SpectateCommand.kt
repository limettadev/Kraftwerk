package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class SpectateCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.spec")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
        }
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you aren't technically a player.")
            return false
        }
        if (args.size == 1 || args.size == 2) {
            if (args[0].lowercase() == "on") {
                if (SpecFeature.instance.getSpecs().contains(sender.name)) {
                    Chat.sendMessage(sender, "${Chat.prefix} You're already in Spectator mode!")
                } else {
                    SpecFeature.instance.spec(sender)
                }
                return true
            }
            if (args[0].lowercase() == "off") {
                if (SpecFeature.instance.getSpecs().contains(sender.name)) {
                    Chat.sendMessage(sender, "${Chat.prefix} You're already not a Spectator!")
                } else {
                    SpecFeature.instance.unspec(sender)
                }
                return true
            }
            val player = sender.server.getPlayer(args[0])
            if (player == null) {
                Chat.sendMessage(sender, "<red>Player not found.")
                return false
            }

            if (GameState.currentState == GameState.INGAME && args.size == 2 && args[1] != "CONFIRM") {
                Chat.sendMessage(sender, "<red>You're about to put someone into Spectator. If they are currently playing, this WILL kill them.\n<red>If you're sure you'd like to do this, use: /spec ${player.name} CONFIRM")
                return false
            }

            if (SpecFeature.instance.getSpecs().contains(player.name)) {
                SpecFeature.instance.unspec(player)
                Chat.sendMessage(sender, "${Chat.prefix} ${Chat.secondaryColor}${player.name}<gray> has been removed from Spectator mode.")
            } else {
                SpecFeature.instance.spec(player)
                Chat.sendMessage(sender, "${Chat.prefix} ${Chat.secondaryColor}${player.name}<gray> has been set to Spectator mode.")
            }
        } else {
            if (SpecFeature.instance.getSpecs().contains(sender.name)) {
                SpecFeature.instance.unspec(sender)
            } else {
                SpecFeature.instance.spec(sender)
            }
        }

        return true
    }
}
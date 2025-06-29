package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import kotlin.math.floor

class PortalPosCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command")
            return false
        }
        if (!SpecFeature.instance.isSpec(sender)) {
            Chat.sendMessage(sender, "<red>You aren't a Spectator!")
            return false
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "<red>You can only use this during a game.")
            return false
        }
        if (args.size != 1) {
            Chat.sendMessage(sender, "<red>Usage: /portalpos <player>")
            return false
        }
        val target = sender.server.getOfflinePlayer(args[0])
        val location = JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.portalLocations[target.uniqueId]
        if (location == null) {
            Chat.sendMessage(sender, "<red>This player has yet to enter the nether.")
            return false
        }
        Chat.sendMessage(sender, "${Chat.prefix} Teleported to ${Chat.secondaryColor}${target.name}<gray>'s last portal location.")
        Chat.sendMessage(sender, "${Chat.prefix} Coordinates: ${Chat.primaryColor}X: ${floor(location.x)}, ${Chat.primaryColor}Y: ${floor(location.y)}, ${Chat.primaryColor}Z: ${floor(location.z)}")

        return true
    }
}
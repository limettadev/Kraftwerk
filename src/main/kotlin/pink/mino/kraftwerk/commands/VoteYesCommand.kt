package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat

class VoteYesCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).vote == null) {
            Chat.sendMessage(sender, "<red>There is no poll running at the moment.")
            return false
        }

        val player = (sender as Player)

        if (SpecFeature.instance.isSpec(player)) {
            Chat.sendMessage(sender, "<red>You cannot vote while spectating.")
            return false
        }

        if (JavaPlugin.getPlugin(Kraftwerk::class.java).vote!!.voted.contains(player)) {
            Chat.sendMessage(sender, "<red>You already voted.")
            return false
        }
        JavaPlugin.getPlugin(Kraftwerk::class.java).vote!!.yes += 1
        JavaPlugin.getPlugin(Kraftwerk::class.java).vote!!.voted.add(player)
        Chat.sendMessage(sender, "${Chat.prefix} Successfully voted <green>yes<gray> on the current poll.")
        return true
    }
}
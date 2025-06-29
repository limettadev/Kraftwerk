package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import java.util.*

class ThanksCommand : CommandExecutor {
    val thanked = arrayListOf<UUID>()

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as a console sender.")
            return false
        }
        if (Kraftwerk.instance.game == null) {
            Chat.sendMessage(sender, "<red>There is no game running at the moment.")
            return false
        }
        if (thanked.contains(sender.uniqueId)) {
            Chat.sendMessage(sender, "<red>You already thanked the host this game.")
            return false
        }
        val player = Bukkit.getOfflinePlayer(Kraftwerk.instance.game!!.host)
        Kraftwerk.instance.statsHandler.lookupStatsPlayer(player).thankYous++
        thanked.add(sender.uniqueId)
        Chat.broadcast("${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has &dthanked<gray> the host! <dark_gray>(${Chat.secondaryColor}${thanked.size}<dark_gray>)")
        return true
    }
}


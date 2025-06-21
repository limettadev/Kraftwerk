package pink.mino.kraftwerk.commands

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat
import kotlin.math.roundToInt

class PMCCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        val player = sender
        if (player.scoreboard.getPlayerTeam(player) == null) {
            player.sendMessage("${ChatColor.RED}You must be on a team to send a message.")
            return true
        }
        val x = (player.location.x * 100.0).roundToInt() / 100.0
        val y = (player.location.y * 100.0).roundToInt() / 100.0
        val z = (player.location.z * 100.0).roundToInt() / 100.0
        if (player.scoreboard.getPlayerTeam(player) != null) {
            for (team in player.scoreboard.getPlayerTeam(player).players) {
                if (team is Player) {
                    Chat.sendMessage(team, "§8[${Chat.primaryColor}Team Chat§8] ${ChatColor.WHITE}${sender.name} ${Chat.dash} ${Chat.primaryColor}${player.name}'s<gray> location: ${Chat.primaryColor}${x}<gray>, ${Chat.primaryColor}${y}<gray>, ${Chat.primaryColor}${z} <dark_gray>| <gray>Dimension: ${Chat.primaryColor}${player.world.worldType.toString().uppercase()}")
                }
            }
        }
        return true
    }
}
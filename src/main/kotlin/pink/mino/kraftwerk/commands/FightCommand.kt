package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import java.util.*
import kotlin.math.floor

class FightCommand : CommandExecutor {
    var cooldowns = HashMap<UUID, Long>()

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
        if (Kraftwerk.instance.game!!.pvpHappened == false) {
            Chat.sendMessage(sender, "<red>PvP hasn't occurred yet.")
            return false
        }
        val cooldownTime = 300
        if (cooldowns.containsKey(sender.uniqueId)) {
            val secondsLeft: Long = cooldowns[sender.uniqueId]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
            if (secondsLeft > 0) {
                sender.sendMessage(Chat.colored("<red>You can't use this command for another $secondsLeft second(s)!"))
                return false
            }
        }
        cooldowns[sender.uniqueId] = System.currentTimeMillis()
        Chat.broadcast("<dark_gray>[${Chat.primaryColor}&lPvP<dark_gray>] &f${sender.name}<gray> is looking for a fight at ${Chat.secondaryColor}X: ${floor(sender.location.x)}<gray>, ${Chat.secondaryColor}Y: ${floor(sender.location.y)}<gray>, ${Chat.secondaryColor}Z: ${floor(sender.location.z)}<gray>!")
        return true
    }
}
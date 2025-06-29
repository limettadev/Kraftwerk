package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.Punishment
import pink.mino.kraftwerk.features.PunishmentFeature
import pink.mino.kraftwerk.features.PunishmentType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*

class DisqualifyCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("uhc.staff.disqualify")) {
            sender.sendMessage(Chat.colored("<red>You don't have permission to do that."))
            return true
        }

        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "<red>You can only disqualify during an active game.")
            return false
        }

        if (args.size < 2) {
            sender.sendMessage(Chat.colored("<red>Usage: /disqualify <player> [-s] <reason>"))
            return true
        }

        val target: OfflinePlayer = Bukkit.getOfflinePlayer(args[0])
        if (!target.hasPlayedBefore() && !target.isOnline) {
            sender.sendMessage(Chat.colored("<red>Player '${args[0]}' not found."))
            return true
        }
        if (target == sender) {
            Chat.sendMessage(sender, "<red>You cannot punish yourself.")
            return true
        }

        if ((target as Player).hasPermission("uhc.staff")) {
            Chat.sendMessage(sender, "<red>You cannot punish another staff member.")
            return true
        }


        var index = 1
        var silent = false

        if (args[index].equals("-s", ignoreCase = true)) {
            silent = true
            index++
        }

        if (args.size <= index) {
            sender.sendMessage(Chat.colored("<red>Usage: /disqualify <player> [-s] <reason>"))
            return true
        }

        val reason = args.copyOfRange(index, args.size).joinToString(" ")

        val punishment = Punishment(
            uuid = target.uniqueId,
            punisherUuid = if (sender is Player) sender.uniqueId else UUID(0, 0),
            type = PunishmentType.DISQUALIFICATION,
            expiresAt = System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000, // 1 year disqualification, arbitrary long duration
            reason = reason,
            silent = silent,
            punishedAt = System.currentTimeMillis(),
            revoked = false
        )

        PunishmentFeature.punish(target, punishment)

        val message = Chat.colored("<red>${target.name} has been disqualified. Reason: $reason")

        if (!silent) {
            Bukkit.broadcast(message, "uhc.staff")
        } else {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("uhc.staff")) {
                    player.sendMessage(Chat.colored("<gray>[Silent] $message"))
                }
            }
        }

        return true
    }
}
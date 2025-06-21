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
import java.util.*

class BanCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("uhc.staff.ban")) {
            sender.sendMessage(Chat.colored("<red>You do not have permission to use this command."))
            return true
        }

        if (args == null || args.size < 3) {
            sender.sendMessage(Chat.colored("<red>Usage: /ban <player> [-s] <duration> <reason...>"))
            return true
        }

        val targetName = args[0]
        val target: OfflinePlayer = Bukkit.getOfflinePlayer(targetName)

        val targetPlayerOnline = Bukkit.getPlayer(target.uniqueId)

        if (targetPlayerOnline != null) { // Target is online
            if (targetPlayerOnline == sender) {
                Chat.sendMessage(sender, "<red>You cannot punish yourself.")
                return true
            }

            if (targetPlayerOnline.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, "<red>You cannot punish another staff member who is currently online.")
                return true
            }
        }

        var hasSilentFlag = false
        var durationArgIndex = 1

        if (args[1].equals("-s", ignoreCase = true)) {
            hasSilentFlag = true
            durationArgIndex = 2
        }

        if (args.size <= durationArgIndex) {
            sender.sendMessage(Chat.colored("<red>Missing duration argument."))
            return true
        }

        val durationArg = args[durationArgIndex]
        val durationMillis = PunishmentFeature.parseDurationToMillis(durationArg)

        if (durationMillis == null) {
            sender.sendMessage(Chat.colored("<red>Invalid duration format. Try something like 1d, 2h, 30m, 1w, 1mo, etc."))
            return true
        }

        if (args.size <= durationArgIndex + 1) {
            sender.sendMessage(Chat.colored("<red>Missing reason for the ban."))
            return true
        }

        val reason = args.copyOfRange(durationArgIndex + 1, args.size).joinToString(" ")

        val punishment = Punishment(
            uuid = target.uniqueId,
            punisherUuid = if (sender is Player) sender.uniqueId else UUID.randomUUID(),
            type = PunishmentType.BAN,
            expiresAt = System.currentTimeMillis() + durationMillis,
            reason = reason,
            silent = hasSilentFlag,
            punishedAt = System.currentTimeMillis(),
            revoked = false
        )

        PunishmentFeature.punish(target, punishment)

        // Notify appropriately
        if (!hasSilentFlag) {
            Bukkit.broadcast(Chat.colored("<red>${target.name} has been banned for ${durationArg}. Reason: $reason"))
        } else {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("uhc.staff")) {
                    player.sendMessage(Chat.colored("<gray>[Silent] <red>${target.name} has been banned for ${durationArg}. Reason: $reason"))
                }
            }
        }

        return true
    }
}
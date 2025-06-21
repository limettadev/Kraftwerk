package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.PunishmentFeature
import pink.mino.kraftwerk.features.PunishmentType
import pink.mino.kraftwerk.utils.Chat

class UnbanCommand : CommandExecutor {

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player || !sender.hasPermission("uhc.staff.unban")) {
            sender.sendMessage(Chat.colored("<red>You don't have permission to use this command."))
            return true
        }

        if (args.isEmpty()) {
            sender.sendMessage(Chat.colored("<red>Usage: /unban <player>"))
            return true
        }

        val target = Bukkit.getOfflinePlayer(args[0])
        if (target.uniqueId == sender.uniqueId) {
            sender.sendMessage(Chat.colored("<red>You can't unban yourself."))
            return true
        }

        val activePunishment = PunishmentFeature.getActivePunishment(target, PunishmentType.BAN)
        if (activePunishment == null) {
            sender.sendMessage(Chat.colored("<red>That player is not currently banned."))
            return true
        }

        PunishmentFeature.revokePunishment(target.uniqueId, PunishmentType.BAN)

        val message = Chat.colored("<red>${sender.name} unbanned ${target.name}.")
        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission("uhc.staff") }
            .forEach { it.sendMessage(message) }

        return true
    }
}

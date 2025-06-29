package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.PunishmentFeature
import pink.mino.kraftwerk.features.PunishmentType
import pink.mino.kraftwerk.utils.Chat
import java.util.*

class AltsCommand : CommandExecutor {

    fun getColoredPlayerName(player: OfflinePlayer): String {
        val hasBan = PunishmentFeature.getActivePunishment(player, PunishmentType.BAN) != null
        val hasMute = PunishmentFeature.getActivePunishment(player, PunishmentType.MUTE) != null

        val baseName = player.name ?: "Unknown"

        return when {
            hasBan -> "<red>$baseName"
            hasMute -> "<yellow>$baseName"
            else -> "<green>$baseName"
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {

        if (sender is Player && !sender.hasPermission("uhc.staff.alts")){
            Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
            return false
        }

        val targetUuid = when {
            args.isNotEmpty() -> {
                val input = args[0]
                try {
                    UUID.fromString(input)
                } catch (e: IllegalArgumentException) {
                    Bukkit.getOfflinePlayer(input).uniqueId
                }
            }
            sender is Player -> sender.uniqueId
            else -> {
                Chat.sendMessage(sender, "${Chat.prefix} <gray>Usage: /alts <player>")
                return true
            }
        }

        val profile = Kraftwerk.instance.profileHandler.getProfile(targetUuid)

        if (profile == null) {
            Chat.sendMessage(sender, "${Chat.prefix} <gray>Profile not found for that player.")
            return true
        }

        val alts = profile.alts

        if (alts.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} <gray>${Chat.secondaryColor}${Bukkit.getOfflinePlayer(targetUuid).name ?: "Unknown"}<gray>'s alts: None")
            return true
        }

        val altNames = alts.map { getColoredPlayerName(Bukkit.getOfflinePlayer(it)) }
            .joinToString(", ") { it }

        val mainNameColored = getColoredPlayerName(Bukkit.getOfflinePlayer(targetUuid))
        Chat.sendMessage(sender, "${Chat.prefix} <gray>$mainNameColored<gray>'s alts: $altNames")

        return true
    }
}

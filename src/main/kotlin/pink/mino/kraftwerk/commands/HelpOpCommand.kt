package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.PunishmentFeature
import pink.mino.kraftwerk.features.PunishmentType
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.HelpOp


class HelpOpCommand : CommandExecutor {
    var cooldowns = HashMap<String, Long>()

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Now, why would *you* need to use this command?")
            return false
        }
        val mutePunishment = PunishmentFeature.getActivePunishment(sender, PunishmentType.HELPOP_MUTE)
        if (mutePunishment != null && !sender.hasPermission("uhc.staff")) {
            val remaining = mutePunishment.expiresAt - System.currentTimeMillis()
            if (remaining > 0) {
                val timeLeft = PunishmentFeature.timeToString(remaining)
                sender.sendMessage(Chat.colored("<red>You are help-op muted for another $timeLeft. Reason: ${mutePunishment.reason}"))
                return false
            }
        }

        val cooldownTime = 60 // Get number of seconds from wherever you want
        if (cooldowns.containsKey(sender.name)) {
            val secondsLeft: Long = cooldowns[sender.name]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
            if (secondsLeft > 0) {
                sender.sendMessage(Chat.colored("<red>You can't use this command for another $secondsLeft second(s)!"))
                return false
            }
        }
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}Usage: /helpop <message>")
            return true
        }
        val message = StringBuilder()
        for (element in args) {
            message.append("${ChatColor.GRAY}${element}").append(" " + ChatColor.GRAY)
        }
        val msg = message.toString().trim()
        val id = HelpOp.addHelpop(sender, msg)
        Chat.sendMessage(sender, "${Chat.prefix} Successfully sent your ${Chat.primaryColor}help-op<gray>, please wait for someone to answer it!")
        val text = net.kyori.adventure.text.Component.text()
            .append(Chat.colored("<dark_gray>[${Chat.primaryColor}Help-OP<dark_gray>] <dark_gray>[${Chat.primaryColor}#${id}<dark_gray>] ${Chat.secondaryColor}${sender.name} ${Chat.dash}<gray> $msg"))
            .clickEvent(net.kyori.adventure.text.event.ClickEvent.suggestCommand("/hr $id "))
            .build()

        if (GameState.currentState != GameState.INGAME) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("uhc.staff")) {
                    player.sendMessage(text)
                }
            }
        } else {
            for (name in SpecFeature.instance.getSpecs()) {
                val player = Bukkit.getPlayer(name)
                if (player != null && player.isOnline) {
                    player!!.sendMessage(text)
                }
            }
        }
        cooldowns[sender.name] = System.currentTimeMillis()
        return true
    }
}
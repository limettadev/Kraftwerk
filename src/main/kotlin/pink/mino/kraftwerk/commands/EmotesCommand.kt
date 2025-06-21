package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.utils.Chat

class EmotesCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Nope!")
            return false
        }
        Chat.sendMessage(sender, "&l&6Emotes:")
        Chat.sendMessage(sender, " <dark_gray>- <yellow>:shrug: ${Chat.dash} <yellow>¯\\_(ツ)_/¯&r")
        Chat.sendMessage(sender, " <dark_gray>- <yellow>:yes: ${Chat.dash} &l<green>✔&r")
        Chat.sendMessage(sender, " <dark_gray>- <yellow>:no: ${Chat.dash} &l<red>✖&r")
        Chat.sendMessage(sender, " <dark_gray>- <yellow>123 ${Chat.dash} <green>1<yellow>2<red>3&r")
        Chat.sendMessage(sender, " <dark_gray>- <yellow><3 ${Chat.dash} <red>❤&r")
        Chat.sendMessage(sender, " <dark_gray>- <yellow>o/ ${Chat.dash} &d(・∀・)ノ&r")
        Chat.sendMessage(sender, " <dark_gray>- <yellow>:star: ${Chat.dash} <yellow>✰&r")
        Chat.sendMessage(sender, " <dark_gray>- <yellow>:100: ${Chat.dash} <red>&o&l&n100&r")
        Chat.sendMessage(sender, " <dark_gray>- <yellow>o7 ${Chat.dash} <yellow>(｀-´)>&r")
        Chat.sendMessage(sender, " <dark_gray>- <yellow>:blush: ${Chat.dash} &d(◡‿◡✿)&r")
        return true
    }
}
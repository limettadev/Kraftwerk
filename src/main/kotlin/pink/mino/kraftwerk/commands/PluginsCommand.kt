package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

class PluginsCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        var gitHash = "(unknown version)"
        try {
            val process = Runtime.getRuntime().exec("git rev-parse --short HEAD", null, File("."))
            process.waitFor()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            gitHash = reader.readLine()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Chat.sendMessage(sender, "${Chat.prefix} &7Running ${Chat.primaryColor}${Kraftwerk.instance.description.name} ${Kraftwerk.instance.description.version}&7 on ${Chat.primaryColor}UHC Gameserver ${gitHash}")
        return true
    }
}

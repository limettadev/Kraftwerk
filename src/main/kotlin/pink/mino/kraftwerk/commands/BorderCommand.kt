package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat

class BorderCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.border")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/border <radius><gray>.")
            return false
        }
        if (args[0].toIntOrNull() == null) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid border size: ${Chat.secondaryColor}/border <radius><gray>.")
            return false
        }
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            "wb ${ConfigFeature.instance.data!!.getString("pregen.world")} setcorners ${args[0]} ${args[0]} -${args[0]} -${args[0]}"
        )
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
            val border = Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")!!)!!.worldBorder
            border.size = args[0].toDouble() * 2
            border.setCenter(0.0, 0.0)
        }, 5L)
        ConfigFeature.instance.data!!.set("pregen.border", args[0].toInt())
        ConfigFeature.instance.saveData()
        if (sender !is ConsoleCommandSender) {
            Bukkit.broadcast(Chat.colored("${Chat.prefix} The world border has been set to ${Chat.secondaryColor}${args[0]}x${args[0]}<gray>."))
        }
        return true
    }
}
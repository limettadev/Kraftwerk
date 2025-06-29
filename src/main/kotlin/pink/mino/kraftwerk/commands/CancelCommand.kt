package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat

class CancelCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.matchpost")) {
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        ConfigFeature.instance.data!!.set("matchpost", null)
        ConfigFeature.instance.data!!.set("matchpost.cancelled", true)
        ConfigFeature.instance.data!!.set("matchpost.fake", false)
        ConfigFeature.instance.data!!.set("whitelist.enabled", true)
        ConfigFeature.instance.data!!.set("whitelist.list", ArrayList<String>())
        ConfigFeature.instance.saveData()
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart")
        }, 900L)
        Bukkit.broadcast(Chat.colored("${Chat.prefix} The game has now been cancelled, the server will restart in ${Chat.secondaryColor}45 seconds<gray>."))
        return true
    }

}
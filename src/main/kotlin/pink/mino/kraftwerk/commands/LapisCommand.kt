package pink.mino.kraftwerk.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.Perk
import pink.mino.kraftwerk.utils.PerkChecker

class LapisCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command!")
            return false
        }
        if (!PerkChecker.checkPerks(sender).contains(Perk.TOGGLE_PICKUPS)) {
            Chat.sendMessage(sender, "<red>You must be a &2Donator<red> to use this command. Buy it at <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config tough tits"}")
            return false
        }
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.disableLapisPickup) {
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.disableLapisPickup = false
            Chat.sendMessage(sender, "${Chat.prefix} <gray>You have enabled &1Lapis<gray> pickups!")
        } else {
            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.disableLapisPickup = true
            Chat.sendMessage(sender, "${Chat.prefix} <gray>You have disabled &1Lapis<gray> pickups!")
        }
        return true
    }
}
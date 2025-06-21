package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ArenaFeature
import pink.mino.kraftwerk.features.CombatLogFeature
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class ArenaCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (args.size == 0) {
            if (sender !is Player) {
                sender.sendMessage("You can't use this command as you aren't technically a player.")
                return false
            }
            if (GameState.currentState == GameState.LOBBY) {
                if (sender.world.name == "Arena") {
                    SpawnFeature.instance.send(sender)
                    sender.sendMessage(Chat.colored("<dark_gray>[${Chat.primaryColor}Arena<dark_gray>]<gray> <gray>You left the arena."))
                    return false
                }
                if (!JavaPlugin.getPlugin(Kraftwerk::class.java)!!.arena) {
                    sender.sendMessage(Chat.colored("<dark_gray>[${Chat.primaryColor}Arena<dark_gray>]<gray> <gray>Arena is currently disabled."))
                    return false
                }
                ArenaFeature.instance.send(sender)
                Chat.sendMessage(sender, "<dark_gray>[${Chat.primaryColor}Arena<dark_gray>]<gray> Welcome to the arena, ${Chat.secondaryColor}${sender.name}<gray>!")
                Chat.sendMessage(sender, "<dark_gray>(<gray>Cross-teaming in the arena is not allowed!<dark_gray>)")
            } else {
                Chat.sendMessage(sender, "<red>The arena is disabled at the moment.")
            }
        } else {
            if (!sender.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
            val plugin = JavaPlugin.getPlugin(Kraftwerk::class.java)
            if (args[0].equals("on", true)) {
                if (plugin.arena) {
                    Chat.sendMessage(sender, "<red>The arena is already enabled.")
                    return false
                }
                plugin.arena = true
                Bukkit.broadcast(Chat.colored("${Chat.prefix} <gray>The arena has been enabled!"))
                return true
            }
            if (args[0].equals("off", true)) {
                if (!plugin.arena) {
                    Chat.sendMessage(sender, "<red>The arena is already disabled.")
                    return false
                }
                plugin.arena = false
                Bukkit.broadcast(Chat.colored("${Chat.prefix} <gray>The arena has been disabled!"))
                for (arenaPlayer in ArenaFeature.instance.getPlayers()) {
                    SpawnFeature.instance.send(arenaPlayer)
                    CombatLogFeature.instance.removeCombatLog(arenaPlayer.name)
                    arenaPlayer.sendMessage(Chat.colored("${ArenaFeature.instance.prefix} You've been sent back to spawn because the arena has been <red>disabled<gray>."))
                }
                return true
            }
        }
        return true
    }
}
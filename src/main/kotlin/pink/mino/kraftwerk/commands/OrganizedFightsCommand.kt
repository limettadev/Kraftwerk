package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.PlayerUtils
import java.util.*

class OrganizedFights : Listener {
    companion object {
        val instance = OrganizedFights()
    }

    var enabled = false
    var assigned = arrayListOf<UUID>()

    fun clearList() {
        assigned.clear()
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} The assigned organized fights list has been cleared!"))
    }

    fun addPlayer(player: Player) {
        if (assigned.contains(player.uniqueId)) {
            return
        }
        assigned.add(player.uniqueId)
        val list = arrayListOf<String>()
        assigned.forEach {
            if (Bukkit.getOfflinePlayer(it).isOnline) list.add(
                "${
                    PlayerUtils.getPrefix(
                        Bukkit.getOfflinePlayer(
                            it
                        ) as Player
                    )
                }${Bukkit.getOfflinePlayer(it).name}"
            )
        }
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${PlayerUtils.getPrefix(player)}${player.name}<gray> has been assigned to fight!"))
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} <gray>&oAssigned players: ${list.joinToString("<gray>, &r")}"))
    }

    fun removePlayer(player: Player) {
        if (!assigned.contains(player.uniqueId)) {
            return
        }
        assigned.remove(player.uniqueId)
        val list = arrayListOf<String>()
        assigned.forEach {
            if (Bukkit.getOfflinePlayer(it).isOnline) list.add(
                "${
                    PlayerUtils.getPrefix(
                        Bukkit.getOfflinePlayer(
                            it
                        ) as Player
                    )
                }${Bukkit.getOfflinePlayer(it).name}"
            )
        }
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${PlayerUtils.getPrefix(player)}${player.name}<gray> has been removed from the fight!"))
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} <gray>&oAssigned players: ${list.joinToString("<gray>, &r")}"))
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Player) {
            val player = event.entity as Player
            val damager = event.damager as Player
            if (enabled) {
                if (assigned.contains(player.uniqueId) && assigned.contains(damager.uniqueId)) {
                    return
                } else {
                    Chat.sendMessage(damager, "<red>You can't damage players while not being assigned!")
                    event.isCancelled = true
                }
            }
        }
        if (event.damager is Arrow && event.entity is Player) {
            val arrow = event.damager as Arrow
            if (arrow.shooter is Player) {
                val player = event.entity as Player
                val damager = arrow.shooter as Player
                if (enabled) {
                    if (assigned.contains(player.uniqueId) && assigned.contains(damager.uniqueId)) {
                        return
                    } else {
                        Chat.sendMessage(damager, "<red>You can't damage players while not being assigned!")
                        event.isCancelled = true
                    }
                }
            }
        }
    }
}

class OrganizedFightsCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.orgs")) {
                sender.sendMessage(Chat.colored("<red>You don't have permission to use this command!"))
                return true
            }
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Usage: ${Chat.primaryColor}/orgs <assign/unassign/clear/start/stop>")
            return true
        }
        if (args[0] == "start") {
            OrganizedFights.instance.enabled = true
            for (player in Bukkit.getOnlinePlayers()) {
                Chat.sendMessage(player, Chat.colored(Chat.line))
                Chat.sendMessage(player, " ")
                Chat.sendCenteredMessage(player, "${Chat.primaryColor}&lORGANIZED FIGHTS HAS BEEN ENABLED!")
                Chat.sendMessage(player, "<gray>Organized Fights has been enabled, PvP is now disabled. Please standby for more instructions.")
                Chat.sendMessage(player, " ")
                Chat.sendMessage(player, Chat.colored(Chat.line))
                player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 1F, 1F)
            }
        } else if (args[0] == "stop") {
            OrganizedFights.instance.enabled = false
            for (player in Bukkit.getOnlinePlayers()) {
                Chat.sendMessage(player, Chat.colored(Chat.line))
                Chat.sendMessage(player, " ")
                Chat.sendCenteredMessage(player, "${Chat.primaryColor}&lORGANIZED FIGHTS HAS BEEN DISABLED!")
                Chat.sendMessage(player, "<gray>Organized Fights has been disabled, PvP is now enabled.")
                Chat.sendMessage(player, " ")
                Chat.sendMessage(player, Chat.colored(Chat.line))
                player.playSound(player.location, Sound.ENDERDRAGON_GROWL, 1F, 1F)
            }
        } else if (args[0] == "assign") {
            if (args.size < 2) {
                Chat.sendMessage(sender, "${Chat.prefix} Usage: ${Chat.primaryColor}/orgs assign <player>")
                return true
            }
            val player = Bukkit.getPlayer(args[1])
            if (player == null) {
                Chat.sendMessage(sender, "${Chat.prefix} <red>Player not found!")
                return true
            }
            OrganizedFights.instance.addPlayer(player)
        } else if (args[0] == "unassign") {
            if (args.size < 2) {
                Chat.sendMessage(sender, "${Chat.prefix} Usage: ${Chat.primaryColor}/orgs unassign <player>")
                return true
            }
            val player = Bukkit.getPlayer(args[1])
            if (player == null) {
                Chat.sendMessage(sender, "${Chat.prefix} <red>Player not found!")
                return true
            }
            OrganizedFights.instance.removePlayer(player)
        } else if (args[0] == "clear") {
            OrganizedFights.instance.clearList()
        }
        return true
    }
}
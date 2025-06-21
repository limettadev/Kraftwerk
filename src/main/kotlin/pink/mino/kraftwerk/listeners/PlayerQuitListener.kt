package pink.mino.kraftwerk.listeners

import me.lucko.helper.Schedulers
import me.lucko.helper.scheduler.Task
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils
import pink.mino.kraftwerk.utils.Scoreboard
import java.util.*

class PlayerQuitListener : Listener {
    private val logoutTimers = mutableMapOf<UUID, Task>()
    private var vaultChat: net.milkbowl.vault.chat.Chat? = null

    init {
        vaultChat = Bukkit.getServer().servicesManager.load(net.milkbowl.vault.chat.Chat::class.java)
    }
    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player
        val group: String = vaultChat!!.getPrimaryGroup(player)
        val prefix: String = if (vaultChat!!.getGroupPrefix(player.world, group) != "<gray>") Chat.colored(vaultChat!!.getGroupPrefix(player.world, group)) else Chat.colored("<red>")
        e.quitMessage = ChatColor.translateAlternateColorCodes('&', "<dark_gray>(&4-<dark_gray>)&r ${prefix}${player.displayName} <dark_gray>[&4${Bukkit.getOnlinePlayers().size - 1}<dark_gray>/&4${Bukkit.getServer().maxPlayers}<dark_gray>]")
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
            Scoreboard.setScore(Chat.colored("${Chat.dash} <gray>Playing..."), Math.max(PlayerUtils.getPlayingPlayers().size, 0))
        }, 3L)

        if (GameState.currentState == GameState.INGAME) {
            var secondsLeft = 5 * 60

            val task = Schedulers.sync().runRepeating(Runnable {
                if (Bukkit.getPlayer(player.uniqueId) != null) {
                    logoutTimers.remove(player.uniqueId)?.stop()
                    return@Runnable
                }

                secondsLeft--

                if (secondsLeft <= 0) {
                    logoutTimers.remove(player.uniqueId)?.stop()

                    val list = ConfigFeature.instance.data!!.getStringList("game.list")
                    if (list.contains(player.name)) {
                        list.remove(player.name)
                        ConfigFeature.instance.data!!.set("game.list", list)
                        ConfigFeature.instance.saveData()

                        if (!player.hasPermission("uhc.staff")) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl remove ${player.name}")
                        }

                        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${Chat.secondaryColor}${player.name}<gray> has been disqualified for being offline for more than 5 minutes."))
                    }
                }

            }, 20L, 20L)

            logoutTimers[player.uniqueId] = task
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        // Cancel any disqualification timer
        logoutTimers.remove(e.player.uniqueId)?.stop()
    }
}
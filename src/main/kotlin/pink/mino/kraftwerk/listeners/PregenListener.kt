package pink.mino.kraftwerk.listeners

import com.wimbli.WorldBorder.Config
import com.wimbli.WorldBorder.Events.WorldBorderFillFinishedEvent
import com.wimbli.WorldBorder.Events.WorldBorderFillStartEvent
import me.lucko.spark.api.statistic.StatisticWindow.TicksPerSecond
import me.lucko.spark.api.statistic.types.DoubleStatistic
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.ActionBar
import pink.mino.kraftwerk.utils.Chat
import kotlin.math.roundToInt

class PregenListener : Listener {
    fun checkTps(tps: Double): String {
        return when {
            tps >= 19.0 -> "§a$tps"
            tps >= 16.0 -> "§e$tps"
            tps >= 10.0 -> "§c$tps"
            else -> "§4$tps"
        }
    }

    val prefix = "<dark_gray>[${Chat.primaryColor}Server<dark_gray>]<gray>"
    @EventHandler
    fun on(event: WorldBorderFillStartEvent) {
        object : BukkitRunnable() {
            override fun run() {
                if (Config.fillTask.valid()) {
                    (Config.fillTask.percentageCompleted * 100.0).roundToInt() / 100.0
                    val tps: DoubleStatistic<TicksPerSecond>? = JavaPlugin.getPlugin(Kraftwerk::class.java).spark.tps()
                    val tpsLast10Secs = tps!!.poll(TicksPerSecond.SECONDS_10)
                    if (!(event.fillTask.refWorld() == null)) {
                        val rounded = (Config.fillTask.percentageCompleted * 100.0).roundToInt() / 100.0
                        for (player in Bukkit.getOnlinePlayers()) {
                            ActionBar.sendActionBarMessage(player, ChatColor.translateAlternateColorCodes('&', "${Chat.prefix} <gray>Progress: ${Chat.primaryColor}${rounded}% <dark_gray>| <gray>World: <dark_gray>'${Chat.primaryColor}${Config.fillTask.refWorld()}<dark_gray>' <dark_gray>| <gray>TPS: ${checkTps(
                                (tpsLast10Secs * 100.0).roundToInt() / 100.0
                            )}"))
                        }
                    } else {
                        cancel()
                        event.fillTask.cancel()
                        Bukkit.broadcastMessage(Chat.colored("${prefix} Cancelled pregen because no world was set."))
                    }
                } else {
                    cancel()
                }
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0, 1)
    }

    @EventHandler
    fun on(event: WorldBorderFillFinishedEvent) {
        Bukkit.broadcastMessage(Chat.colored("${prefix} Pregeneration in world '${Chat.primaryColor}${event.world.name}<gray>' finished."))
        Bukkit.broadcastMessage(Chat.colored("${prefix} Please wait for TPS to stabilize at <green>20.00 <gray>before restarting."))
    }
}
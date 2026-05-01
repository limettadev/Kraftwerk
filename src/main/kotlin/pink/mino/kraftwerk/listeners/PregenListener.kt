package pink.mino.kraftwerk.listeners

import me.lucko.spark.api.statistic.StatisticWindow.TicksPerSecond
import me.lucko.spark.api.statistic.types.DoubleStatistic
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.popcraft.chunky.api.event.task.GenerationCompleteEvent
import org.popcraft.chunky.api.event.task.GenerationProgressEvent
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat
import kotlin.math.roundToInt

class PregenListener : Listener {
    fun checkTps(tps: Double): String {
        return when {
            tps >= 19.0 -> "<green>$tps"
            tps >= 16.0 -> "<yellow>$tps"
            tps >= 10.0 -> "<red>$tps"
            else -> "<dark_red>$tps"
        }
    }

    val prefix = "<dark_gray>[${Chat.primaryColor}Server<dark_gray>]<gray>"
    @EventHandler
    fun on(event: GenerationProgressEvent) {
        val rounded = (event.progress * 100.0).roundToInt() / 100.0
        val tps = Bukkit.getServer().tps[0]

        for (player in Bukkit.getOnlinePlayers()) {
            player.sendActionBar(MiniMessage.miniMessage().deserialize(
                "${Chat.prefix} <gray>Progress: <${Chat.primaryColor}>${rounded}% <dark_gray>| <gray>World: <dark_gray>'<${Chat.primaryColor}>${event.world}<dark_gray>' <dark_gray>| <gray>TPS: ${checkTps(
                    (tps * 100.0).roundToInt() / 100.0
                )}"
            ))
        }
    }

    @EventHandler
    fun on(event: GenerationCompleteEvent) {
        Bukkit.broadcast(Chat.colored("${prefix} Pregeneration in world '${Chat.primaryColor}${event.world}<gray>' finished."))
        Bukkit.broadcast(Chat.colored("${prefix} Please wait for TPS to stabilize at <green>20.00 <gray>before restarting."))
    }
}
package pink.mino.kraftwerk.features

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class InfoFeature : BukkitRunnable() {
    val prefix: String = "<dark_gray>[${Chat.primaryColor}Info<dark_gray>]<gray>"
    private val announcements = listOf(
        Chat.colored("$prefix Join our discord server using ${Chat.secondaryColor}/discord<gray>!"),
        Chat.colored("$prefix Apply for staff using ${Chat.secondaryColor}/apply<gray>!"),
        Chat.colored("$prefix Want to host games on here? Apply for us! Use ${Chat.secondaryColor}/apply<gray>!"),
        Chat.colored("$prefix Like our server @ ${Chat.secondaryColor}namemc.com/server/${if (ConfigFeature.instance.config!!.getString("chat.serverIp") != null) ConfigFeature.instance.config!!.getString("chat.serverIp") else "no server ip setup in config tough tits"}<gray>!"),
        Chat.colored("$prefix Want more games hosted? Apply for staff @ ${Chat.secondaryColor}/apply<gray>!"),
        Chat.colored("$prefix Wanna know when games are hosted & more? Join our discord @ ${Chat.secondaryColor}/discord<gray>."),
       Chat.colored("$prefix View the store using ${Chat.secondaryColor}/store<gray>!"),
        Chat.colored("$prefix View the server rules using ${Chat.secondaryColor}/rules<gray>."),
        Chat.colored("$prefix View the health of other players using ${Chat.secondaryColor}/health<gray>!"),
        Chat.colored("$prefix View the stats of other players using ${Chat.secondaryColor}/stats <player><gray>!"),
        Chat.colored("$prefix View the UHC Configuration using ${Chat.secondaryColor}/config<gray>!"),
        Chat.colored("$prefix Check which scenarios are active using ${Chat.secondaryColor}/scenarios<gray>!"),
        Chat.colored("$prefix Don't know when the Loot Crate (or other) will spawn? Use ${Chat.secondaryColor}/timers<gray>!"),
        Chat.colored("$prefix Who has the top kills in the game? Use ${Chat.secondaryColor}/kt<gray>!"),
        Chat.colored("$prefix Message your team your mined ores using ${Chat.secondaryColor}/pmminedores<gray>!"),
        Chat.colored("$prefix Message your team the ores you have now using ${Chat.secondaryColor}/pmores<gray>!"),
        Chat.colored("$prefix Are you a content creator? Apply for media rank using ${Chat.secondaryColor}/media<gray>!"),
        Chat.colored("$prefix Is someone being annoying to you? Use ${Chat.secondaryColor}/ignore<gray>!"),
        Chat.colored("$prefix Can't see? Use ${Chat.secondaryColor}/fb<gray> to enable night vision!"),
        Chat.colored("$prefix Team with other players using ${Chat.secondaryColor}/team<gray>!"),
        Chat.colored("$prefix Announce where you are to fight with other players using ${Chat.secondaryColor}/fight<gray>!"),
        Chat.colored("$prefix Thank the host using ${Chat.secondaryColor}/thanks<gray>!")
    )
    override fun run() {
        if (Bukkit.getOnlinePlayers().isNotEmpty()) {
            if (!ConfigOptionHandler.getOption("private")!!.enabled) {
                Bukkit.broadcastMessage(announcements[Random.nextInt(announcements.size)])
            }
        }
    }
}
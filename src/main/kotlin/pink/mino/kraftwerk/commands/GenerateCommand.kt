package pink.mino.kraftwerk.commands

import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.ActionBar
import pink.mino.kraftwerk.utils.Chat

class GenerateTask(val world: World, private val chunks: ArrayList<Location>) : BukkitRunnable() {
    val size = chunks.size
    override fun run() {
        if (chunks.isEmpty()) {
            cancel()
            return
        }
        val chunk: Chunk = chunks.removeAt(chunks.size - 1).chunk

        for (y in 256 downTo 0) {
            for (x in 0..15) {
                for (z in 0..15) {
                    val block: Block = chunk.getBlock(x, y, z)
                    for (scenario in ScenarioHandler.getActiveScenarios()) {
                        scenario.handleBlock(block)
                    }
                }
            }
        }
        for (player in Bukkit.getOnlinePlayers()) {
            ActionBar.sendActionBarMessage(player, ChatColor.translateAlternateColorCodes('&', "<dark_gray>[${Chat.primaryColor}Scenario Generation<dark_gray>] <gray>Chunks Left: ${Chat.primaryColor}${chunks.size}<dark_gray>/${Chat.primaryColor}${size} <dark_gray>| <gray>World: <dark_gray>'${Chat.primaryColor}${world.name}<dark_gray>'"))
        }
    }
}

class GenerateCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.generate")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
        }
        val scenarios = ArrayList<Scenario>()
        for (scenario in ScenarioHandler.getActiveScenarios()){
            if (scenario.gen) scenarios.add(scenario)
        }
        if (scenarios.isEmpty()) {
            Chat.sendMessage(sender, "<red>There are no generation-based scenarios currently enabled.")
            return false
        }
        Chat.sendMessage(sender, "${Chat.prefix} Starting generate task...")
        val world = (sender as Player).world
        if (world.name == "Spawn" || world.name == "Arena") {
            Chat.sendMessage(sender, "<red>You cannot use this command in spawn or arena.")
            return false
        }
        val border = ConfigFeature.instance.data!!.getInt("pregen.border")
        val chunks = ArrayList<Location>()
        for (x in -border..border step 16) {
            for (z in -border..border step 16) {
                val location = Location(world, x.toDouble(), 1.0, z.toDouble())
                chunks.add(location)
            }
        }
        Chat.sendMessage(sender, "${Chat.prefix} <gray>Generating ${Chat.secondaryColor}${chunks.size}<gray> chunks...")
        GenerateTask(world, chunks).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 1L)
        return true
    }

}
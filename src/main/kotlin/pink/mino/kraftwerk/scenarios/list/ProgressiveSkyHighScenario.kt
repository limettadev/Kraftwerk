package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils

class ProgressiveSkyHighScenario : Scenario(
    "Progressive SkyHigh",
    "After PvP, stay near 0,0 and above Y: 150 or take increasing damage every 45 seconds.",
    "progressiveskyhigh",
    Material.FEATHER
), Listener {
    private var task: ProgressiveSkyHighIterator? = null
    private val playerStrikes = mutableMapOf<String, Int>()

    override fun onStart() {
        val shovel = ItemStack(Material.DIAMOND_SPADE)
        val meta = shovel.itemMeta
        meta.spigot().isUnbreakable = true
        meta.addEnchant(Enchantment.DIG_SPEED, 10, true)
        shovel.itemMeta = meta
        for (player in Bukkit.getOnlinePlayers()) {
            if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                PlayerUtils.bulkItems(player, arrayListOf(
                    ItemStack(Material.STAINED_CLAY, 128, 14),
                    ItemStack(Material.STAINED_CLAY, 128, 14),
                    ItemStack(Material.PUMPKIN, 2),
                    ItemStack(Material.SNOW_BLOCK, 8),
                    shovel
                ))
                Chat.sendMessage(player, "${Chat.prefix} You've been given your Progressive SkyHigh items.")
            }
        }
    }

    override fun returnTimer(): Int? {
        return if (task != null) {
            task!!.timer
        } else {
            null
        }
    }

    override fun onPvP() {
        startDamageTask()
    }

    private fun startDamageTask() {
        task = ProgressiveSkyHighIterator()
        task!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 30 * 20L)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Progressive SkyHigh is now active! Stay near 0,0 and above Y: 150 or take increasing damage every 30 seconds."))
    }

    override fun givePlayer(player: Player) {
        val shovel = ItemStack(Material.DIAMOND_SPADE)
        val meta = shovel.itemMeta
        meta.spigot().isUnbreakable = true
        meta.addEnchant(Enchantment.DIG_SPEED, 10, true)
        shovel.itemMeta = meta
        PlayerUtils.bulkItems(player, arrayListOf(
            ItemStack(Material.STAINED_CLAY, 128, 14),
            ItemStack(Material.STAINED_CLAY, 128, 14),
            ItemStack(Material.PUMPKIN, 2),
            ItemStack(Material.SNOW_BLOCK, 8),
            shovel
        ))
        Chat.sendMessage(player, "${Chat.prefix} You've been given your Progressive SkyHigh items.")
    }

    inner class ProgressiveSkyHighIterator : BukkitRunnable() {
        var timer = 45

        override fun run() {
            timer--
            if (timer == 0) {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (SpecFeature.instance.getSpecs().contains(player.name) || player.world.name == "Spawn") continue
                    if (player.location.y < 150) {
                        val strikes = playerStrikes.getOrDefault(player.name, 0) + 1
                        playerStrikes[player.name] = strikes
                        val damage = 1.0 * strikes
                        player.damage(damage)
                        Chat.sendMessage(player, "<red>You took ${damage / 2} hearts for not being above Y: 150 and near 0,0! (Strike $strikes)")
                    } else {
                        playerStrikes[player.name] = 0
                    }
                }
                timer = 45
            }

            if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("progressiveskyhigh"))) {
                cancel()
            }
            if (GameState.currentState != GameState.INGAME) {
                cancel()
            }
        }
    }
}
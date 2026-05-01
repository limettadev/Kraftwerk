package pink.mino.kraftwerk.config.options

import me.lucko.helper.Schedulers
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOption


class AbsorptionOption : ConfigOption(
    "Absorption",
    "Players gain absorption hearts when eating Golden Apples.",
    "options",
    "absorption",
    Material.GOLDEN_APPLE
) {
    @EventHandler
    fun onPlayerConsume(e: PlayerItemConsumeEvent) {
        if (enabled) {
            return
        }
        Schedulers.sync().runLater({
            e.player.removePotionEffect(PotionEffectType.ABSORPTION)
        }, 1L)
    }
}
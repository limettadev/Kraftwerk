package pink.mino.kraftwerk.scenarios.list

import me.lucko.helper.Schedulers
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*

class GappleRouletteScenario : Scenario(
    "Gapple Roulette",
    "Whenever you eat a Golden Apple, you will get a random potion effect for a certain time.",
    "gappleroulette",
    Material.GOLDEN_APPLE
) {
    val potionEffects = listOf<PotionEffectType>(
        PotionEffectType.BLINDNESS,
        PotionEffectType.DARKNESS,
        PotionEffectType.RESISTANCE,
        PotionEffectType.HASTE,
        PotionEffectType.FIRE_RESISTANCE,
        PotionEffectType.HUNGER,
        PotionEffectType.STRENGTH,
        PotionEffectType.INVISIBILITY,
        PotionEffectType.JUMP_BOOST,
        PotionEffectType.NIGHT_VISION,
        PotionEffectType.POISON,
        PotionEffectType.REGENERATION,
        PotionEffectType.SATURATION,
        PotionEffectType.SLOWNESS,
        PotionEffectType.MINING_FATIGUE,
        PotionEffectType.SPEED,
        PotionEffectType.WATER_BREATHING,
        PotionEffectType.WEAKNESS,
        PotionEffectType.WITHER
    )

    val prefix = "<dark_gray>[${Chat.primaryColor}Gapple Roulette<dark_gray>]<gray>"

    @EventHandler
    fun onPlayerConsume(e: PlayerItemConsumeEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.item.type == Material.GOLDEN_APPLE) {
            Schedulers.sync().runLater ({
                val effect = potionEffects.random()
                val seconds = Random().nextInt(90)
                e.player.addPotionEffect(PotionEffect(effect, 20 * seconds, 0))
                Chat.sendMessage(e.player, "${prefix} You got ${Chat.secondaryColor}${effect.name}<gray> for ${Chat.secondaryColor}${seconds} seconds<gray>!")
            }, 1L)
        }
    }
}
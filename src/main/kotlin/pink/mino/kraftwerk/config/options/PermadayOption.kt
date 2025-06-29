package pink.mino.kraftwerk.config.options

import org.bukkit.Bukkit
import org.bukkit.Material
import pink.mino.kraftwerk.config.ConfigOption
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat

class PermadayOption : ConfigOption(
    "Permaday",
    "The game will always be day, and the daylight cycle will be disabled.",
    "options",
    "permaday",
    Material.DANDELION
) {
    override fun onToggle(to: Boolean) {
        val world = Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")!!)
        if (world == null) {
            Bukkit.broadcastMessage("${Chat.prefix} Permaday cannot be toggled while there is no overworld world!")
            return
        }
        if (enabled) {
            world.time = 6000
            world.setGameRuleValue("doDaylightCycle", "false")
        } else {
            world.setGameRuleValue("doDaylightCycle", "true")
        }
    }
}
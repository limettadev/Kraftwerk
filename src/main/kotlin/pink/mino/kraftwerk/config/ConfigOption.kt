package pink.mino.kraftwerk.config

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.CommandExecutor
import org.bukkit.event.Listener
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat


abstract class ConfigOption(
    var name: String,
    var description: String,
    var category: String,
    var id: String,
    var material: Material,
    var enabled: Boolean = false,
    var command: Boolean = false,
    var commandName: String = "none",
    var executor: CommandExecutor? = null
): Listener {

    init {
        if (ConfigFeature.instance.data!!.getString("game.$category.$id").isNullOrEmpty()) {
            enabled = false
            ConfigFeature.instance.data!!.set("game.$category.$id", enabled)
            ConfigFeature.instance.saveData()
        }
        enabled = ConfigFeature.instance.data!!.getBoolean("game.$category.$id")
    }

    fun toggle() {
        enabled = !enabled
        val changerText: String = if (category === "rules") {
            if (enabled) {
                "&aallowed"
            } else {
                "&cdisallowed"
            }
        } else {
            if (enabled) {
                "&aenabled"
            } else {
                "&cdisabled"
            }
        }
        onToggle(enabled)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &e$name&7 is now $changerText&7."))
        ConfigFeature.instance.data!!.set("game.$category.$id", enabled)
        ConfigFeature.instance.saveData()
    }

    @JvmName("setEnabled1")
    fun setEnabled(to: Boolean) {
        enabled = to
        val changerText: String = if (category === "rules") {
            if (enabled) {
                "&aallowed"
            } else {
                "&cdisallowed"
            }
        } else {
            if (enabled) {
                "&aenabled"
            } else {
                "&cdisabled"
            }
        }
        onToggle(to)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &e$name&7 is now $changerText&7."))
        ConfigFeature.instance.data!!.set("game.$category.$id", to)
        ConfigFeature.instance.saveData()
    }

    open fun onToggle(to: Boolean) {}
}

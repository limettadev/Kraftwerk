package pink.mino.kraftwerk.scenarios

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat

abstract class Scenario(
    var name: String,
    var description: String,
    var id: String,
    var material: Material,
    val gen: Boolean = false,
    var enabled: Boolean = false
): Listener {
    init {
        if (ConfigFeature.instance.data!!.getString("game.scenarios.$id").isNullOrEmpty()) {
            enabled = false
            ConfigFeature.instance.data!!.set("game.scenarios.$id", enabled)
            ConfigFeature.instance.saveData()
        }
        enabled = ConfigFeature.instance.data!!.getBoolean("game.scenarios.$id")
    }

    fun toggle() {
        enabled = !enabled
        val changerText: String = if (enabled) {
            "<green>enabled"
        } else {
            "<red>disabled"
        }
        onToggle(enabled)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} <yellow>$name<gray> has been $changerText<gray>."))
        ConfigFeature.instance.data!!.set("game.scenarios.$id", enabled)
        ConfigFeature.instance.saveData()
    }

    @JvmName("setEnabled1")
    fun setEnabled(to: Boolean) {
        enabled = to
        val changerText: String = if (enabled) {
                "<green>enabled"
            } else {
                "<red>disabled"
            }
        onToggle(to)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} &d$name<gray> has been $changerText<gray>."))
        ConfigFeature.instance.data!!.set("game.scenarios.$id", to)
        ConfigFeature.instance.saveData()
    }

    open fun onPvP() {}
    open fun onFinalHeal() {}
    open fun onStart() {}
    open fun onMeetup() {}
    open fun givePlayer(player: Player) {}
    open fun handleBlock(block: Block) {}
    open fun onGuiClick(player: Player) {}
    open fun returnTimer(): Int? {
        return null
    }

    open fun onToggle(to: Boolean) {}
}

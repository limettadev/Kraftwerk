package pink.mino.kraftwerk.utils.menus.pagination

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.menus.Button
import pink.mino.kraftwerk.utils.menus.Menu
import java.beans.ConstructorProperties

class BackButton @ConstructorProperties(value = ["back"]) constructor(private val back: Menu?) : Button() {
    override fun getMaterial(var1: Player): Material {
        return Material.REDSTONE
    }

    override fun getDamageValue(player: Player): Byte {
        return 0
    }

    override fun getName(player: Player): String {
        return Chat.colored("<red>" + if (back == null) "Close" else "Back")
    }

    override fun getDescription(player: Player): List<String> {
        val lines: MutableList<String> = ArrayList()
        if (back != null) {
            lines.add(Chat.colored("<gray>Click here to return to"))
            lines.add(Chat.colored("<gray>the previous menu."))
        } else {
            lines.add(Chat.colored("<gray>Click here to"))
            lines.add(Chat.colored("<gray>close this menu."))
        }
        return lines
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType) {
        playNeutral(player)
        if (back == null) {
            player.closeInventory()
        } else {
            back.openMenu(player)
        }
    }
}
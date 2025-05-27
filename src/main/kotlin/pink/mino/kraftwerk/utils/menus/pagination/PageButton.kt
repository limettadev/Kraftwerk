package pink.mino.kraftwerk.utils.menus.pagination

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.menus.Button
import java.beans.ConstructorProperties

class PageButton @ConstructorProperties(value = ["mod", "menu"]) constructor(
    private val mod: Int,
    private val menu: PaginatedMenu
) :
    Button() {
    override fun clicked(player: Player, slot: Int, clickType: ClickType) {
        if (clickType == ClickType.RIGHT) {
            ViewAllPagesMenu(menu).openMenu(player)
            playNeutral(player)
        } else if (hasNext(player)) {
            menu.modPage(player, mod)
            Button.playNeutral(player)
        } else {
            Button.playFail(player)
        }
    }

    private fun hasNext(player: Player): Boolean {
        val pg: Int = menu.page + mod
        return pg > 0 && menu.getPages(player) >= pg
    }

    override fun getName(player: Player): String {
        if (!hasNext(player)) {
            return Chat.colored(if (mod > 0) "&7Last page" else "&7First page")
        }
        val str = "&7(&e" + (menu.page + mod) + "&e" + menu.getPages(player) + "&7)"
        return Chat.colored(if (mod > 0) "&a⟶" else "&a⟵")
    }

    override fun getDescription(player: Player): List<String> {
        return ArrayList()
    }

    override fun getMaterial(player: Player): Material {
        return Material.ARROW
    }
}
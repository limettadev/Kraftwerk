package pink.mino.kraftwerk.utils.menus.pagination

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.menus.Button
import java.beans.ConstructorProperties

class JumpToPageButton @ConstructorProperties(value = ["page", "menu"]) constructor(
    private val page: Int,
    private val menu: PaginatedMenu
) :
    Button() {
    override fun getName(player: Player): String {
        return Chat.colored("&ePage " + page)
    }

    override fun getDescription(player: Player): List<String> {
        return listOf()
    }

    override fun getMaterial(player: Player): Material {
        return Material.BOOK
    }

    override fun getAmount(player: Player): Int {
        return page
    }

    override fun getDamageValue(player: Player): Byte {
        return 0
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType) {
        menu.modPage(player, page - menu.page)
        playNeutral(player)
    }
}
package pink.mino.kraftwerk.utils.menus

import com.google.common.base.Joiner
import com.google.common.collect.ImmutableList
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

abstract class Button {
    abstract fun getName(player: Player): String
    abstract fun getDescription(player: Player): List<String>
    open fun getMaterial(player: Player): Material {
        return Material.DIRT
    }

    open fun getDamageValue(player: Player): Byte {
        return 0
    }

    fun getBedrockIcon(player: Player?): String {
        return "https://minotar.net/avatar/MHF_QUESTION/100.png"
    }

    open fun clicked(player: Player, slot: Int, clickType: ClickType) {}
    open fun shouldCancel(player: Player, slot: Int, clickType: ClickType): Boolean {
        return true
    }

    open fun getAmount(player: Player): Int {
        return 1
    }

    open fun getButtonItem(player: Player): ItemStack {
        val buttonItem = ItemStack(getMaterial(player)!!, getAmount(player))
        val meta = buttonItem.itemMeta
        meta.displayName = getName(player)
        val description = getDescription(player)
        if (description != null) {
            meta.lore = (
                description
            )
        }
        buttonItem.setItemMeta(meta)
        return buttonItem
    }

    open val isIgnoreUpdateOnClick: Boolean
        get() = false

    companion object {
        @Deprecated("")
        fun placeholder(material: Material, data: Byte, vararg title: String?): Button {
            return placeholder(
                material,
                data,
                if (title == null || title.size == 0) " " else Joiner.on(" ").join(title)
            )
        }

        fun placeholder(material: Material): Button {
            return placeholder(material, " ")
        }

        fun placeholder(material: Material, title: String?): Button {
            return placeholder(material, 0.toByte(), title)
        }

        fun placeholder(material: Material, data: Byte, title: String): Button {
            return object : Button() {
                override fun getName(player: Player): String {
                    return title
                }

                override fun getDescription(player: Player): List<String> {
                    return ImmutableList.of()
                }

                override fun getMaterial(player: Player): Material {
                    return material
                }

                override fun getDamageValue(player: Player): Byte {
                    return data
                }

                override val isIgnoreUpdateOnClick: Boolean
                    get() = true
            }
        }

        fun fromItem(item: ItemStack): Button {
            return object : Button() {
                override fun getButtonItem(player: Player): ItemStack {
                    return item
                }

                override fun getName(player: Player): String {
                    return "&7"
                }

                override fun getDescription(player: Player): List<String> {
                    return listOf()
                }

                override fun getMaterial(player: Player): Material {
                    return Material.AIR
                }
            }
        }

        fun playFail(player: Player) {
            player.playSound(player.location, Sound.ANVIL_LAND, 20.0f, 0.1f)
        }

        fun playSuccess(player: Player) {
            player.playSound(player.location, Sound.NOTE_PLING, 20.0f, 15.0f)
        }

        fun playNeutral(player: Player) {
            player.playSound(player.location, Sound.CLICK, 20.0f, 1.0f)
        }
    }
}
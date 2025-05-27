package pink.mino.kraftwerk.utils.menus

import com.google.common.base.Preconditions
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

abstract class Menu {
    val buttons = ConcurrentHashMap<Int, Button?>()
    open var isAutoUpdate = false
    var isUpdateAfterClick = true
    private var isPlaceholder = false
    private var staticTitle = ""
    var isNoncancellingInventory = false

    private fun createInventory(player: Player): Inventory {
        val invButtons = getButtons(player)
        val inv = Bukkit.createInventory(player, size(invButtons), getTitle(player))
        for ((key, value) in invButtons) {
            buttons[key] = value!!
            inv.setItem(key, value.getButtonItem(player))
        }
        if (isPlaceholder) {
            val placeholder = placeholderItem
            for (index in 0 until size(invButtons)) {
                if (invButtons[index] != null) continue
                buttons[index] = placeholder
                inv.setItem(index, placeholder.getButtonItem(player))
            }
        }
        return inv
    }

    constructor()
    constructor(staticTitle: String?) {
        this.staticTitle = Preconditions.checkNotNull(staticTitle as Any?) as String
    }

    fun openMenu(player: Player) {
        //val sp: ServerPlayer = (player as CraftPlayer).handle
        val inv = createInventory(player)
        try {
            player.openInventory(inv)
            update(player)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun update(player: Player) {
        cancelCheck(player)
        currentlyOpenedMenus!![player.name] = this
        onOpen(player)
        val runnable: BukkitRunnable = object : BukkitRunnable() {
            override fun run() {
                if (!player.isOnline) {
                    cancelCheck(player)
                    currentlyOpenedMenus!!.remove(player.name)
                }
                if (isAutoUpdate) {
                    player.openInventory.topInventory.contents = createInventory(player).contents
                }
            }
        }
        runnable.runTaskTimer(Kraftwerk.instance, 10L, 10L)
        checkTasks!![player.name] = runnable
    }

    fun size(buttons: Map<Int, Button?>): Int {
        var highest = 0
        for (buttonValue in buttons.keys) {
            if (buttonValue <= highest) continue
            highest = buttonValue
        }
        return (Math.ceil((highest + 1).toDouble() / 9.0) * 9.0).toInt()
    }

    fun getSlot(x: Int, y: Int): Int {
        return 9 * y + x
    }

    open fun getTitle(player: Player): String {
        return staticTitle
    }

    abstract fun getButtons(player: Player): Map<Int, Button?>
    open fun onOpen(player: Player) {}
    open fun onClose(player: Player) {}
    val placeholderItem: Button
        get() = Button.placeholder(Material.STAINED_GLASS_PANE)

    companion object {
        var openInventoryMethod: Method? = null
            get() {
                if (field == null) {
                    try {
                        field = (CraftHumanEntity::class.java).getDeclaredMethod(
                            "openCustomInventory",
                            Inventory::class.java,
                            CraftPlayer::class.java,
                            String::class.java
                        )

                        field!!.isAccessible = true
                    } catch (ex: NoSuchMethodException) {
                        ex.printStackTrace()
                    }
                }
                return field
            }
        var currentlyOpenedMenus: MutableMap<String, Menu>? = null
        var checkTasks: MutableMap<String, BukkitRunnable>? = null
        fun cancelCheck(player: Player) {
            if (checkTasks!!.containsKey(player.name)) {
                checkTasks!!.remove(player.name)!!.cancel()
            }
        }

        init {
            Kraftwerk.instance.server.pluginManager.registerEvents(ButtonListener(), Kraftwerk.instance)
            currentlyOpenedMenus = HashMap()
            checkTasks = HashMap()
        }
    }
}
package pink.mino.kraftwerk.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.util.function.Consumer

class GuiBuilder : Listener {
    private var name: String
    private var rows: Int
    private val items: HashMap<Int, ItemStack>
    private val runnableHashMap: HashMap<Int, Consumer<InventoryClickEvent>?>
    private var owner: Player? = null
    private var slot = 0

    fun rows(newRows: Int): GuiBuilder {
        rows = newRows
        return this
    }

    fun name(newName: String): GuiBuilder {
        name = ChatColor.translateAlternateColorCodes('&', newName)
        return this
    }

    fun item(slot: Int, item: ItemStack): GuiBuilder {
        items[slot] = item
        this.slot = slot
        return this
    }

    fun item(slot: Int, item: ItemStack, consumer: Consumer<InventoryClickEvent>?): GuiBuilder {
        items[slot] = item
        this.slot = slot
        runnableHashMap[slot] = consumer
        return this
    }

    fun owner(owner: Player?): GuiBuilder {
        this.owner = owner
        return this
    }

    fun onClick(runnable: Consumer<InventoryClickEvent>?) {
        runnableHashMap[slot] = runnable
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked is Player && owner != null) {
            val clicker = e.whoClicked as Player
            if (clicker.uniqueId == owner!!.uniqueId) {
                val view = e.view
                val inventoryTitle = view.title() // returns net.kyori.adventure.text.Component
                val expectedTitle = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(name)
                if (inventoryTitle == expectedTitle) {
                    val currentItem = e.currentItem
                    if (currentItem != null && currentItem.type != Material.AIR) {
                        val slot = e.slot
                        runnableHashMap[slot]?.accept(e)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerClose(event: InventoryCloseEvent) {
        if (event.player is Player && owner != null) {
            if (event.player.uniqueId == owner!!.uniqueId) {
                val inventoryTitle = event.view.title() // Component
                val expectedTitle = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(name)
                if (inventoryTitle == expectedTitle) {
                    HandlerList.unregisterAll(this)
                }
            }
        }
    }

    fun make(): Inventory {
        require(rows * 9 <= 54) { "Too many rows in the created inventory!" }
        val inv = Bukkit.createInventory(null, rows * 9, name)
        for (f in items.keys) {
            inv.setItem(f, items[f])
        }
        return inv
    }

    init {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getPlugin(Kraftwerk::class.java))
        name = "Inventory"
        rows = 1
        items = HashMap()
        runnableHashMap = HashMap()
    }
}

package pink.mino.kraftwerk.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import java.util.function.Consumer

class GuiBuilder : Listener {
    private var name: Component
    private var rows: Int
    private val items: HashMap<Int, ItemStack>
    private val runnableHashMap: HashMap<Int, Consumer<InventoryClickEvent>?>
    private var owner: Player? = null
    private var slot = 0

    fun rows(newRows: Int): GuiBuilder {
        rows = newRows
        return this
    }

    fun name(newName: Component): GuiBuilder {
        name = newName
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

    private fun expectedTitle(): Component = name.decoration(TextDecoration.ITALIC, false)

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (owner == null) return
        val clicker = e.whoClicked as? Player ?: return
        if (clicker.uniqueId != owner!!.uniqueId) return

        // Ignore clicks in the player's own bottom inventory
        if (e.clickedInventory != e.view.topInventory) {
            e.isCancelled = true
            return
        }

        if (e.view.title() != expectedTitle()) return

        e.isCancelled = true

        val currentItem = e.currentItem
        if (currentItem != null && currentItem.type != Material.AIR) {
            runnableHashMap[e.slot]?.accept(e)
        }
    }

    @EventHandler
    fun onPlayerClose(event: InventoryCloseEvent) {
        if (owner == null) return
        val player = event.player as? Player ?: return
        if (player.uniqueId != owner!!.uniqueId) return
        if (event.view.title() != expectedTitle()) return

        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (owner == null) return
        if (event.player.uniqueId != owner!!.uniqueId) return

        HandlerList.unregisterAll(this)
    }

    fun make(): Inventory {
        require(rows * 9 <= 54) { "Too many rows in the created inventory!" }
        val inv = Bukkit.createInventory(null, rows * 9, expectedTitle())
        for (f in items.keys) {
            inv.setItem(f, items[f])
        }
        return inv
    }

    init {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getPlugin(Kraftwerk::class.java))
        name = Chat.colored("Inventory")
        rows = 1
        items = HashMap()
        runnableHashMap = HashMap()
    }
}
package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.ItemBuilder

class BookceptionScenario : Scenario(
    "Bookception",
    "Players will drop a random enchanted book on death.",
    "bookception",
    Material.ENCHANTED_BOOK
) {
    val enchantments = listOf<Enchantment>(
        Enchantment.EFFICIENCY,
        Enchantment.SHARPNESS,
        Enchantment.FLAME,
        Enchantment.FIRE_ASPECT,
        Enchantment.FORTUNE,
        Enchantment.PROTECTION,
        Enchantment.POWER,
        Enchantment.INFINITY,
        Enchantment.UNBREAKING
    )

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled && GameState.currentState != GameState.INGAME) return
        if (e.entity.killer != null) {
            val book = ItemBuilder(Material.ENCHANTED_BOOK)
                .toEnchant()
                .addStoredEnchant(enchantments.random(), 1)
                .make()
            e.entity.location.world.dropItemNaturally(e.entity.location, book)
        }
    }
}
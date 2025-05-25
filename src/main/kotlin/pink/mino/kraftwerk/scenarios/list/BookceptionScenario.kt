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
        Enchantment.DIG_SPEED,
        Enchantment.DAMAGE_ALL,
        Enchantment.ARROW_FIRE,
        Enchantment.FIRE_ASPECT,
        Enchantment.LOOT_BONUS_BLOCKS,
        Enchantment.PROTECTION_ENVIRONMENTAL,
        Enchantment.ARROW_DAMAGE,
        Enchantment.ARROW_INFINITE,
        Enchantment.DURABILITY
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
package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.entity.PlayerDeathEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*

class EnchantProgressionScenario : Scenario(
    "Enchant Progression",
    "All players receive 1 enchanting table use for each kill they get.",
    "enchantprogression",
    Material.ENCHANTMENT_TABLE
) {
    var enchants: HashMap<UUID, Int> = hashMapOf()

    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            enchants[player.uniqueId] = 0
        }
    }

    override fun givePlayer(player: Player) {
        enchants[player.uniqueId] = 0
    }

    @EventHandler
    fun onPrepareItemEnchant(e: PrepareItemEnchantEvent) {
        if (!enabled) return
        val player = e.enchanter
        val uuid = player.uniqueId

        val remaining = enchants[uuid] ?: 0
        if (remaining < 1) {
            for (i in e.expLevelCostsOffered.indices) {
                e.expLevelCostsOffered[i] = 0
            }
            Chat.sendMessage(player, "&cYou have no enchantments left to use.")
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity.killer == null) return
        if (e.entity.killer.type == EntityType.PLAYER) {
            val killer = e.entity.killer as Player
            if (enchants[killer.uniqueId] == null) {
                enchants[killer.uniqueId] = 1
            } else {
                enchants[killer.uniqueId] = enchants[killer.uniqueId]!! + 1
            }
            Chat.sendMessage(killer, "${Chat.prefix} You have gained an enchant! (Enchants: ${Chat.secondaryColor}${enchants[killer.uniqueId]}&7)")
        } else if (e.entity.killer.type == EntityType.ARROW && (e.entity as Arrow).shooter is Player) {
            val killer = (e.entity as Arrow).shooter as Player
            if (enchants[killer.uniqueId] == null) {
                enchants[killer.uniqueId] = 1
            } else {
                enchants[killer.uniqueId] = enchants[killer.uniqueId]!! + 1
            }
            Chat.sendMessage(killer, "${Chat.prefix} You have gained an enchant! (Enchants: ${Chat.secondaryColor}${enchants[killer.uniqueId]})")
        } else {
            return
        }
    }


}
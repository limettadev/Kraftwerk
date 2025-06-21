package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.PrepareAnvilEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*

class AnvilProgressionScenario : Scenario(
    "Anvil Progression",
    "Any time you kill a player, you will get 1 use of the anvil. The more kills you accumulate, the more uses of the anvil you can use.",
    "anvilprogression",
    Material.ANVIL
) {
    var anvils: HashMap<UUID, Int> = hashMapOf()

    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            anvils[player.uniqueId] = 0
        }
    }

    override fun givePlayer(player: Player) {
        anvils[player.uniqueId] = 0
    }

    @EventHandler
    fun onPrepareAnvil(e: PrepareAnvilEvent) {
        if (!enabled) return
        val player = e.view.player as? Player ?: return
        val uuid = player.uniqueId

        val remaining = anvils[uuid] ?: 0
        if (remaining < 1) {
            e.result = null
            Chat.sendMessage(player, "<red>You have no anvil uses left.")
        }
    }


    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity.killer == null) return
        if (e.entity.killer!!.type == EntityType.PLAYER) {
            val killer = e.entity.killer as Player
            if (anvils[killer.uniqueId] == null) {
                anvils[killer.uniqueId] = 1
            } else {
                anvils[killer.uniqueId] = anvils[killer.uniqueId]!! + 1
            }
            Chat.sendMessage(killer, "${Chat.prefix} You have gained an anvil use! (Anvils: ${Chat.secondaryColor}${anvils[killer.uniqueId]}<gray>)")
        } else if (e.entity.killer!!.type == EntityType.ARROW && (e.entity as Arrow).shooter is Player) {
            val killer = (e.entity as Arrow).shooter as Player
            if (anvils[killer.uniqueId] == null) {
                anvils[killer.uniqueId] = 1
            } else {
                anvils[killer.uniqueId] = anvils[killer.uniqueId]!! + 1
            }
            Chat.sendMessage(killer, "${Chat.prefix} You have gained an anvil use! (Anvils: ${Chat.secondaryColor}${anvils[killer.uniqueId]})")
        } else {
            return
        }
    }
}
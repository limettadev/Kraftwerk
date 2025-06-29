package pink.mino.kraftwerk.utils

// TODO: ADD 1.8 SUPPORT

import net.minecraft.server.level.ServerPlayer
//import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.entity.CraftPlayer
//import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import kotlin.math.floor

class PlayerUtils {
    companion object {
        fun getPrefix(player: Player): String {
            return if (TeamsFeature.manager.getTeam(player) != null) {
                TeamsFeature.manager.getTeam(player)!!.prefix
            } else {
                "&f"
            }
        }

        fun getHealth(player: Player): String {
            val el: ServerPlayer = (player as CraftPlayer).handle
            val health = floor(player.health / 2 * 10 + el.absorptionAmount / 2 * 10)
            val color = HealthChatColorer.returnHealth(health)
            return "${color}${health}%"
        }

        fun inventoryFull(player: Player): Boolean {
            return player.inventory.firstEmpty() == -1
        }

        fun bulkItems(player: Player, bulk: ArrayList<ItemStack>) {
            for (item in bulk) {
                if (!inventoryFull(player)) {
                    player.inventory.addItem(item)
                } else {
                    player.world.dropItemNaturally(player.location, item)
                }
            }
        }

        fun getPlayingPlayers(): MutableCollection<out Player> {
            val players: ArrayList<Player> = arrayListOf()
            if (GameState.currentState == GameState.LOBBY || GameState.currentState == GameState.WAITING) {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (!SpecFeature.instance.isSpec(player)) {
                        players.add(player)
                    }
                }
            } else {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (SpecFeature.instance.getSpecs().contains(player.name) || player.world.name == "Spawn") continue
                    players.add(player)
                }
            }
            return players
        }
    }
}
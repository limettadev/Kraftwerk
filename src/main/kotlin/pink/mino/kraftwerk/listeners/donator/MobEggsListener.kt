package pink.mino.kraftwerk.listeners.donator

import me.lucko.helper.Schedulers
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.storage.ValueInput
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerEggThrowEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.*
import java.util.*

class MobEggsListener : Listener{

    private fun noAI(entity: Entity) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "data merge entity ${entity.uniqueId} {NoAI:1}")
    }

    @EventHandler
    fun onEggHit(event: ProjectileHitEvent) {
        if (GameState.currentState == GameState.INGAME) return
        if (event.entity !is Egg) return
        if (event.entity.shooter !is Player) return

        val player = event.entity.shooter as Player
        if (!PerkChecker.checkPerk(player, "mobEggs")) return

        val rand = Random()

        val types: ArrayList<EntityType> = arrayListOf()

        for (type in EntityType.values()) {
            if (!type.isAlive || !type.isSpawnable) continue
            types.add(type)
        }

        val entityType = types[rand.nextInt(types.size)]

        val loc = event.entity.location
        val world = event.entity.world

        if (world.name != "Spawn") return

        // find a better way to find a spawn loc, because with no ai, it'll just fly in the air where it lands
        val entity = world.spawnEntity(LocationUtils.getHighestBlock(loc).add(0.0, 1.0, 0.0), entityType)
        entity.customName(Chat.colored("<gold>${player.name}'s <yellow>${entityType.name}"))
        if (entity is LivingEntity) {
            noAI(entity)
        }
        Schedulers.sync().runLater({
            entity.remove()
        }, 20 * 10)
    }

    @EventHandler
    fun onEggThrow(event: PlayerEggThrowEvent) {
        if (GameState.currentState == GameState.INGAME) return
        event.isHatching = false
    }

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {
        if (GameState.currentState == GameState.INGAME) return
        if (event.item == null) return
        if (event.item!!.type != Material.EGG) return
        val player = event.player
        if (!PerkChecker.checkPerk(player, "mobEggs")) {
            player.sendMessage(Chat.colored("<red>You do not have the Mob Eggs perk, buy a rank on the store at <yellow>applejuice.tebex.io<red>!"))
            event.isCancelled = true
            return
        }

        if (event.item!!.amount == 1) {
            Chat.sendMessage(player, "<dark_gray>[<dark_green>$$$<dark_gray>] <gray>You have no more mob eggs! Giving you more in <red>10 seconds<gray>!")
            val mobEggs = ItemBuilder(Material.EGG)
                .name("<dark_green>Mob Eggs <gray>(Throw)")
                .addLore("<gray>Throw these eggs in the Spawn to spawn mobs!")
                .addLore("<gray><italic>Mobs last for 10 seconds.")
                .addLore("<gray><italic>Eggs replenish after 10 seconds of no eggs.")
                .setAmount(5)
                .make()
            Schedulers.sync().runLater({
                if (player.isOnline && player.world.name == "Spawn") {
                    player.inventory.setItem(7, mobEggs)
                }
            }, 20 * 10)
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (GameState.currentState == GameState.INGAME) return
        if (event.entity is Player) return
        if (event.entity.world.name != "Spawn") return
        event.isCancelled = true
    }

    @EventHandler
    fun onEntityChangeBlock(event: EntityChangeBlockEvent) {
        if (GameState.currentState == GameState.INGAME) return
        if (event.entity.world.name != "Spawn") return
        if (event.entity is Player) return
        event.isCancelled = true
    }

}
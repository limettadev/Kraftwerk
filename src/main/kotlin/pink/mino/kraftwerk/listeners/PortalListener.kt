package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPortalEvent
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.LocationUtils


class PortalListener : Listener {
    @EventHandler
    fun on(event: PlayerPortalEvent) {
        val player = event.player
        val from: Location = event.from
        val fromWorld: World = from.world
        if (!LocationUtils.hasBlockNearby(Material.NETHER_PORTAL, from)) {
            return
        }
        val fromName = fromWorld.name
        val targetName: String = when (fromWorld.environment) {
            World.Environment.NORMAL -> fromName + "_nether"
            World.Environment.NETHER -> {
                if (!fromName.endsWith("_nether")) {
                    Chat.sendMessage(player, "${Chat.prefix} <gray>You don't appear to be in the nether. Please helpop if this is incorrect.")
                    return
                }
                fromName.substring(0, fromName.length - 7)
            }
            else -> return
        }
        val targetWorld = Bukkit.getWorld(targetName)
        if (targetWorld == null) {
            Chat.sendMessage(player, Chat.prefix + "<gray>The nether hasn't been generated for this world.")
            return
        }
        if (!ConfigFeature.instance.data!!.getBoolean("game.nether.nether")) {
            Chat.sendMessage(player, Chat.prefix + "<gray>The nether is currently disabled.")
            return
        }
        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(player)!!.timesNether
        val multiplier = if (fromWorld.environment === World.Environment.NETHER) 8.0 else 0.125
        var to = Location(
            targetWorld,
            from.x * multiplier,
            from.y,
            from.z * multiplier,
            from.yaw,
            from.pitch
        )
        to.chunk.load(true)
        to = LocationUtils.findSafeLocationInsideBorder(to, 10)
        if (to.y < 0) {
            Chat.sendMessage(player, "${Chat.prefix} <gray>Couldn't find a safe place inside of the overworld, defaulting to 0,0.")
            to = LocationUtils.findSafeLocationInsideBorder(Location(targetWorld, 0.0, 100.0, 0.0), 10)
            event.to = to
        } else {
            event.to = to
        }
    }

    @EventHandler
    fun onEntityPortal(event: EntityPortalEvent) {
        val from: Location = event.from
        val fromWorld: World = from.world
        if (!LocationUtils.hasBlockNearby(Material.NETHER_PORTAL, from)) {
            return
        }
        val fromName = fromWorld.name
        val targetName: String = when (fromWorld.environment) {
            World.Environment.NORMAL -> fromWorld.name + "_nether"
            World.Environment.NETHER -> {
                if (!fromName.endsWith("_nether")) {
                    return
                }
                fromName.substring(0, fromName.length - 7)
            }
            else -> return
        }
        val targetWorld = Bukkit.getWorld(targetName) ?: return
        val multiplier = if (fromWorld.environment === World.Environment.NETHER) 8.0 else 0.125
        var to = Location(
            targetWorld,
            from.x * multiplier,
            from.y,
            from.z * multiplier,
            from.yaw,
            from.pitch
        )
        to = LocationUtils.findSafeLocationInsideBorder(to, 10)
        if (to.y < 0) {
            to = LocationUtils.findSafeLocationInsideBorder(Location(targetWorld, 0.0, 100.0, 0.0), 10)
            event.to = to
        } else {
            event.to = to
        }
    }
}
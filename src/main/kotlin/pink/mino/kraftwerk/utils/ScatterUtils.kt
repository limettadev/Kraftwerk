package pink.mino.kraftwerk.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import pink.mino.kraftwerk.features.SpecFeature
import java.util.*


/**
 * @author mrcsm
 * 2022-06-15
 */
class ScatterUtils {
    private val nospawn: Array<Material> = arrayOf<Material>(
        Material.WATER,
        Material.WATER,
        Material.LAVA,
        Material.CACTUS
    )

    fun getScatterLocations(world: World?, radius: Int, count: Int): List<Location> {
        val locs: ArrayList<Location> = ArrayList<Location>()
        for (i in 0 until count) {
            var min = 150.0
            for (j in 0..4003) {
                if (j == 4003) {
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (SpecFeature.instance.getSpecs().contains(player.name)) {
                            Chat.sendMessage(player, "${Chat.prefix} Could not find a scatter location. Aborting scatter.")
                        }
                    }
                    break
                }
                val rand = Random()
                val x: Int = rand.nextInt(radius * 2) - radius
                val z: Int = rand.nextInt(radius * 2) - radius
                val loc = Location(world, x + 0.5, 0.0, z + 0.5)
                var close = false
                for (l in locs) {
                    if (l.distanceSquared(loc) < min) {
                        close = true
                    }
                }
                min -= if (!close && isValid(loc.clone())) {
                    val y: Double = LocationUtils.highestTeleportableYAtLocation(loc)
                    loc.y = y + 2
                    locs.add(loc)
                    break
                } else {
                    1.0
                }
            }
        }
        return locs
    }

    fun getQuadsScatterLocations(world: World?, radius: Int, count: Int): List<Location>? {
        val locs: ArrayList<Location> = ArrayList<Location>()
        for (i in 0 until count) {
            var min = 150.0
            for (j in 0..4003) {
                if (j == 4003) {
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (SpecFeature.instance.getSpecs().contains(player.name)) {
                            Chat.sendMessage(player, "${Chat.prefix} Could not find a scatter location. Aborting scatter.")
                        }
                    }
                    break
                }
                val rand = Random()
                val x: Int = rand.nextInt(radius * 2) - radius
                val z: Int = rand.nextInt(radius * 2) - radius
                val loc = Location(world, x + 0.5, 0.0, z + 0.5)
                var close = false
                for (l in locs) {
                    if (l.distanceSquared(loc) < min) {
                        close = true
                    }
                }
                min -= if (!close && isValid(loc.clone())) {
                    val y: Double = LocationUtils.highestTeleportableYAtLocation(loc)
                    loc.y = y + 2
                    locs.add(loc)
                    break
                } else {
                    1.0
                }
            }
        }
        return locs
    }

    private fun isValid(loc: Location): Boolean {
        loc.y = loc.world.getHighestBlockYAt(loc).toDouble()
        val m: Material = loc.add(0.0, -1.0, 0.0).block.type
        var vaild = true
        if (loc.blockY < 60) {
            vaild = false
        }
        for (no in nospawn) {
            if (m === no) {
                vaild = false
            }
        }
        return vaild
    }
}
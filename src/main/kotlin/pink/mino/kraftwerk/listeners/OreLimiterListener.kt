package pink.mino.kraftwerk.listeners

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import pink.mino.kraftwerk.events.ChunkModifiableEvent
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.BlockUtil
import java.util.*

class OreLimiterListener : Listener {

    private val random = Random()
    private val blockUtil = BlockUtil()

    private val ores = hashSetOf(
        Material.COAL_ORE,         Material.DEEPSLATE_COAL_ORE,
        Material.IRON_ORE,         Material.DEEPSLATE_IRON_ORE,
        Material.GOLD_ORE,         Material.DEEPSLATE_GOLD_ORE,
        Material.REDSTONE_ORE,     Material.DEEPSLATE_REDSTONE_ORE,
        Material.LAPIS_ORE,        Material.DEEPSLATE_LAPIS_ORE,
        Material.DIAMOND_ORE,      Material.DEEPSLATE_DIAMOND_ORE,
        Material.EMERALD_ORE,      Material.DEEPSLATE_EMERALD_ORE,
    )

    private val caveBlocks = hashSetOf(
        Material.AIR,
        Material.WATER,
        Material.LAVA,
    )

    @EventHandler
    fun on(event: ChunkModifiableEvent) {
        val chunk = event.chunk
        val world = chunk.world
        val worldConfig = ConfigFeature.instance.worlds!!

        val goldRate = worldConfig.getInt("${world.name}.orerates.gold")
        val diamondRate = worldConfig.getInt("${world.name}.orerates.diamond")
        val oresOutsideCaves = worldConfig.getBoolean("${world.name}.oresOutsideCaves")

        val checked = HashSet<Block>()

        for (x in 0..15) {
            for (y in world.minHeight until world.maxHeight) {
                for (z in 0..15) {
                    val block = chunk.getBlock(x, y, z)
                    if (!checked.add(block)) continue

                    val type = block.type
                    if (type !in ores) continue

                    val vein = blockUtil.getVein(block)
                    checked.addAll(vein)

                    val replacement = if (type.name.startsWith("DEEPSLATE_")) Material.DEEPSLATE else Material.STONE

                    if (!oresOutsideCaves) {
                        val nearCave = vein.any { ore ->
                            blockUtil.getBlocks(ore, 2).any { it.type in caveBlocks }
                        }
                        if (!nearCave) {
                            vein.forEach { it.type = replacement }
                            continue
                        }
                    }

                    val roll = random.nextInt(100) + 1
                    val removeChance = when (type) {
                        Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE -> 40
                        Material.GOLD_ORE,     Material.DEEPSLATE_GOLD_ORE     -> goldRate
                        Material.DIAMOND_ORE,  Material.DEEPSLATE_DIAMOND_ORE  -> diamondRate
                        else -> 0
                    }

                    if (roll <= removeChance) {
                        vein.forEach { it.type = replacement }
                    }
                }
            }
        }
    }
}
package pink.mino.kraftwerk.utils

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import java.util.*


class BlockUtil {
    fun getBlocks(start: Block, radius: Int): ArrayList<Block> {
        val blocks = ArrayList<Block>()
        var x = start.location.x - radius
        while (x <= start.location.x + radius) {
            var y = start.location.y - radius
            while (y <= start.location.y + radius) {
                var z = start.location.z - radius
                while (z <= start.location.z + radius) {
                    val loc: Location = Location(start.world, x, y, z)
                    blocks.add(loc.block)
                    z++
                }
                y++
            }
            x++
        }
        return blocks
    }

    fun degradeDurability(player: Player) {
        val item = player.itemInHand

        if ((item.type == Material.AIR) || (item.type == Material.BOW) || item.type.maxDurability.toInt() == 0 || item.itemMeta.isUnbreakable) {
            return
        }

        var durability = item.durability
        val rand = Random()

        if (item.containsEnchantment(Enchantment.UNBREAKING)) {
            val chance = (100 / (item.getEnchantmentLevel(Enchantment.UNBREAKING) + 1))

            if (rand.nextDouble() <= (chance / 100)) {
                durability--
            }
        } else {
            durability--
        }

        if (durability >= item.type.maxDurability) {
            player.world.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F)
            player.itemInHand.type = Material.AIR
            return
        }

        item.durability = durability
        player.setItemInHand(item)
    }

    companion object {
        const val DEFAULT_VEIN_LIMIT = 64

        var logs = listOf(
            Material.OAK_LOG,
            Material.STRIPPED_OAK_LOG,
            Material.DARK_OAK_LOG,
            Material.STRIPPED_DARK_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG,
            Material.SPRUCE_LOG,
            Material.BIRCH_LOG,
            Material.STRIPPED_BIRCH_LOG,
            Material.JUNGLE_LOG,
            Material.STRIPPED_JUNGLE_LOG,
            Material.ACACIA_LOG,
            Material.STRIPPED_ACACIA_LOG,
            Material.MANGROVE_LOG,
            Material.STRIPPED_MANGROVE_LOG,
            Material.CHERRY_LOG,
            Material.STRIPPED_CHERRY_LOG,
            Material.PALE_OAK_LOG,
            Material.STRIPPED_PALE_OAK_LOG,
            Material.STRIPPED_CRIMSON_STEM,
            Material.CRIMSON_STEM,
            Material.STRIPPED_WARPED_STEM,
            Material.WARPED_STEM
        )

        var leaves = listOf(
            Material.OAK_LEAVES,
            Material.SPRUCE_LEAVES,
            Material.BIRCH_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.ACACIA_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.MANGROVE_LEAVES,
            Material.CHERRY_LEAVES,
            Material.PALE_OAK_LEAVES,
            Material.AZALEA_LEAVES,
            Material.FLOWERING_AZALEA_LEAVES
        )
    }

    fun getVein(start: Block): List<Block> = getVein(start, DEFAULT_VEIN_LIMIT)

    fun getVein(start: Block, maxVeinSize: Int): List<Block> {
        val targetType = start.type
        val visited = HashSet<Block>()
        val toCheck = ArrayDeque<Block>()

        visited.add(start)
        toCheck.add(start)

        while (toCheck.isNotEmpty()) {
            val check = toCheck.removeFirst()

            for (dx in -1..1) for (dy in -1..1) for (dz in -1..1) {
                if (dx == 0 && dy == 0 && dz == 0) continue
                val neighbor = check.getRelative(dx, dy, dz)
                if (!visited.add(neighbor)) continue
                if (neighbor.type != targetType) continue

                toCheck.add(neighbor)

                if (visited.size >= maxVeinSize) return visited.toList()
            }
        }
        return visited.toList()
    }

}
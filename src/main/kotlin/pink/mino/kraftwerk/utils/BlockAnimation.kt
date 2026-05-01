package pink.mino.kraftwerk.utils

import org.bukkit.Effect
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.entity.Player
import kotlin.random.Random


class BlockAnimation {
    fun blockBreakAnimation(player: Player?, block: Block) {
        block.world.players.forEach { p ->
            p.sendBlockDamage(block.location, 1.0f)
        }
        block.world.spawnParticle(
            Particle.BLOCK_CRUMBLE,
            block.location.add(0.5, 0.5, 0.5),
            10,
            block.blockData
        )
        block.world.playEffect(block.location, Effect.STEP_SOUND, block.type)
    }

    fun blockCrackAnimation(p: Player?, block: Block, stage: Int) {
        val progress = stage / 9.0f // stage is 0-9 in vanilla
        block.world.players.forEach { viewer ->
            viewer.sendBlockDamage(block.location, progress)
        }
    }
}
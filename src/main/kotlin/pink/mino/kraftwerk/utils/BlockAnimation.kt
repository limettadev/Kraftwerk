package pink.mino.kraftwerk.utils

import net.minecraft.server.v1_8_R3.BlockPosition
import org.bukkit.block.Block
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.CraftWorld
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import kotlin.random.Random


class BlockAnimation {
    fun blockBreakAnimation(player: Player?, block: Block) {
        val blockPosition = BlockPosition(block.x, block.y, block.z)
        val worldServer = (block.world as CraftWorld).handle
        val blockData = worldServer.getType(blockPosition)
        worldServer.a(
            if (player == null) null else (player as CraftPlayer).handle,
            2001,
            blockPosition,
            net.minecraft.server.v1_8_R3.Block.getCombinedId(blockData)
        )
    }

    fun blockCrackAnimation(p: Player?, block: Block, stage: Int) {
        val packet = net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket(
            Random.nextInt(1000),
            net.minecraft.core.BlockPos(block.x, block.y, block.z),
            stage
        )
        val world = block.world
        val server = p?.server
        if (server != null && world != null) {
            val location = block.location
            (server as CraftServer).handle.sendPacketNearby(
                location.x, location.y, location.z, 120.0,
                (world as CraftWorld).handle, packet
            )
            (server as CraftServer).handle.send
        }
    }
}
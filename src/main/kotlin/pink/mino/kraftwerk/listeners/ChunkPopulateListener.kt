package pink.mino.kraftwerk.listeners

import me.lucko.helper.Schedulers
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import pink.mino.kraftwerk.events.ChunkModifiableEvent
import pink.mino.kraftwerk.features.ConfigFeature

class ChunkPopulateListener : Listener {

    @EventHandler
    fun on(event: ChunkLoadEvent) {
        if (!event.isNewChunk) return

        val worldName = event.world.name
        val chunkX = event.chunk.x
        val chunkZ = event.chunk.z


        Schedulers.async().runLater({
            val world = Bukkit.getWorld(worldName) ?: return@runLater
            world.getChunkAtAsync(chunkX, chunkZ).thenAcceptAsync { chunk ->
                if (ConfigFeature.instance.worlds!!.getString("${world.name}.type") != null) {
                    Bukkit.getPluginManager().callEvent(ChunkModifiableEvent(chunk))
                }
            }
        }, 1L)
    }
}
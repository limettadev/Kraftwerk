package pink.mino.kraftwerk.features

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCDeathEvent
import net.citizensnpcs.api.npc.NPC
import net.citizensnpcs.api.trait.trait.Equipment
import net.citizensnpcs.api.trait.trait.Inventory
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.commands.WhitelistCommand
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState


class CombatLog(val player: Player) : BukkitRunnable() {
    var combatTimer = 20
    override fun run() {
        combatTimer -= 1
        if (combatTimer == 0) {
            cancel()
            CombatLogFeature.instance.removeCombatLog(player.name)
            var list = ConfigFeature.instance.data!!.getStringList("game.combatloggers")
            if (list == null) list = ArrayList<String>()
            if (list.contains(player.name)) {
                Bukkit.getLogger().info("${player.name} has been removed from the combat log.")
            }
        }
    }
}

class CombatLogFeature : Listener {
    companion object {
        val instance = CombatLogFeature()
    }

    val dropsHash: HashMap<NPC, ArrayList<ItemStack>> = HashMap()

    private fun addCombatLog(player: String) {
        var list = ConfigFeature.instance.data!!.getStringList("game.combatloggers")
        if (list == null) list = ArrayList<String>()
        if (!list.contains(player)) {
            list.add(player)
        }
        ConfigFeature.instance.data!!.set("game.combatloggers", list)
    }

    fun removeCombatLog(player: String) {
        var list = ConfigFeature.instance.data!!.getStringList("game.combatloggers")
        if (list == null) list = ArrayList<String>()
        list.remove(player)
        ConfigFeature.instance.data!!.set("game.combatloggers", list)
    }

    private fun getCombatLogList(): List<String> {
        return ConfigFeature.instance.data!!.getStringList("game.combatloggers")
    }

    @EventHandler
    fun onPlayerDamageByPlayer(e: EntityDamageByEntityEvent) {
        if (GameState.currentState == GameState.INGAME) {
            if (e.isCancelled) return
            if (e.entityType == EntityType.PLAYER && e.damager.type == EntityType.PLAYER) {

                addCombatLog((e.damager as Player).name)
                addCombatLog((e.entity as Player).name)

                if (getCombatLogList().contains((e.damager as Player).name)) {
                    CombatLog(e.damager as Player).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
                }
                if (getCombatLogList().contains((e.entity as Player).name)) {
                    CombatLog(e.entity as Player).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
                }
            }
        }
    }

    @EventHandler
    fun onNpcDeath(e: NPCDeathEvent) {
        e.drops.clear()
        for (item in dropsHash[e.npc]!!) {
            e.drops.add(item)
        }
        e.npc.destroy()
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (GameState.currentState == GameState.INGAME) {
            removeCombatLog(e.entity.name)
        }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        if (GameState.currentState == GameState.INGAME) {
            if (e.player.world.name != "Spawn") {
                if (getCombatLogList().contains(e.player.name)) {
                    WhitelistCommand().removeWhitelist(e.player.name)
                    val list = ConfigFeature.instance.data!!.getStringList("game.list")
                    list.remove(e.player.name)
                    ConfigFeature.instance.data!!.set("game.list", list)
                    ConfigFeature.instance.saveData()
                    removeCombatLog(e.player.name)
                    val npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, e.player.name)
                    npc.spawn(e.player.location)
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                        if (npc.isSpawned) {
                            npc.name = Chat.colored("<dark_gray>(<green>Logger<dark_gray>)&f ${e.player.name}")
                            npc.isProtected = false
                            npc.getOrAddTrait(Equipment::class.java)
                                .set(Equipment.EquipmentSlot.HELMET, e.player.inventory.helmet)
                            npc.getOrAddTrait(Equipment::class.java)
                                .set(Equipment.EquipmentSlot.CHESTPLATE, e.player.inventory.chestplate)
                            npc.getOrAddTrait(Equipment::class.java)
                                .set(Equipment.EquipmentSlot.LEGGINGS, e.player.inventory.leggings)
                            npc.getOrAddTrait(Equipment::class.java)
                                .set(Equipment.EquipmentSlot.BOOTS, e.player.inventory.boots)
                            npc.getOrAddTrait(Inventory::class.java)
                                .contents = e.player.inventory.contents
                            val drops = arrayListOf<ItemStack>()
                            e.player.inventory.contents.filterNotNull().forEach { drops.add(it) }
                            e.player.inventory.helmet?.let { drops.add(it) }
                            e.player.inventory.chestplate?.let { drops.add(it) }
                            e.player.inventory.leggings?.let { drops.add(it) }
                            e.player.inventory.boots?.let { drops.add(it) }
                            dropsHash[npc] = drops

                            val entity = npc as LivingEntity
                            entity.health = e.player.health
                            entity.maxHealth = e.player.maxHealth
                        }
                    }, 20L)
                    Bukkit.broadcastMessage(Chat.colored("${Chat.dash} ${Chat.secondaryColor}${e.player.name}<gray> has been removed from the game for combat logging."))
                }
            }
        }
    }

}
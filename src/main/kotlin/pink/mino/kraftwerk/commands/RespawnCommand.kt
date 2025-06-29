package pink.mino.kraftwerk.commands

import me.lucko.helper.utils.Log
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.listeners.PlayerRespawnListener
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder
import java.util.*
import kotlin.math.floor

class RespawnFeature : Listener {
    companion object {
        val instance = RespawnFeature()
    }

    val locations: HashMap<UUID, Location> = hashMapOf()
    val causes: HashMap<UUID, DamageCause> = hashMapOf()
    val inventory: HashMap<UUID, Array<ItemStack>> = hashMapOf()
    val xp: HashMap<UUID, Float> = hashMapOf()
    val level: HashMap<UUID, Int> = hashMapOf()
    val effects: HashMap<UUID, Array<PotionEffect>> = hashMapOf()
    val helmet: HashMap<UUID, ItemStack> = hashMapOf()
    val chestplate: HashMap<UUID, ItemStack> = hashMapOf()
    val leggings: HashMap<UUID, ItemStack> = hashMapOf()
    val boots: HashMap<UUID, ItemStack> = hashMapOf()
    val respawnablePlayers: ArrayList<UUID> = arrayListOf()

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (GameState.currentState != GameState.INGAME) return
        val player = e.entity
        causes[player.uniqueId] = player.lastDamageCause.cause
        locations[player.uniqueId] = player.location
        inventory[player.uniqueId] = player.inventory.contents
        xp[player.uniqueId] = player.exp
        level[player.uniqueId] = player.level
        effects[player.uniqueId] = player.activePotionEffects.toTypedArray()
        if (player.inventory.helmet != null) helmet[player.uniqueId] = player.inventory.helmet
        if (player.inventory.chestplate != null) chestplate[player.uniqueId] = player.inventory.chestplate
        if (player.inventory.leggings != null) leggings[player.uniqueId] = player.inventory.leggings
        if (player.inventory.boots != null) boots[player.uniqueId] = player.inventory.boots
        respawnablePlayers.add(player.uniqueId)
        Log.info("Saved ${player.name}'s death.")
    }
}

class RespawnCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.revive")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "<red>You can't use this command while there is no game running.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Opening list of respawnable players...")
            if (RespawnFeature.instance.respawnablePlayers.size == 0) {
                Chat.sendMessage(sender, "${Chat.prefix} There are no players to respawn.")
                return false
            }
            var size = 1
            if (RespawnFeature.instance.respawnablePlayers.size >= 9) {
                size = 2
            } else if (RespawnFeature.instance.respawnablePlayers.size >= 18) {
                size = 3
            } else if (RespawnFeature.instance.respawnablePlayers.size >= 27) {
                size = 4
            } else if (RespawnFeature.instance.respawnablePlayers.size >= 36) {
                size = 5
            } else if (RespawnFeature.instance.respawnablePlayers.size >= 45) {
                size = 6
            }
            val gui = GuiBuilder().rows(size).name(Chat.colored("${Chat.primaryColor}Respawnable Players")).owner(sender as Player)
            for ((index, player) in RespawnFeature.instance.respawnablePlayers.withIndex()) {
                val skull = ItemBuilder(Material.SKULL_ITEM)
                    .name("&d${Bukkit.getOfflinePlayer(player).name}")
                    .addLore("<gray>Location: ${Chat.primaryColor}${floor(RespawnFeature.instance.locations[player]!!.x)}<gray>, ${Chat.primaryColor}${floor(RespawnFeature.instance.locations[player]!!.y)}<gray>, ${Chat.primaryColor}${floor(RespawnFeature.instance.locations[player]!!.z)}")
                    .addLore("<gray>Cause: ${Chat.primaryColor}${RespawnFeature.instance.causes[player].toString()
                        .uppercase(Locale.getDefault())}")
                    .addLore(" ")
                    .addLore("${Chat.primaryColor}Left Click<gray> to teleport to the player's death location.")
                    .addLore("<green>Right Click<gray> to respawn the player.")
                    .toSkull()
                    .setOwner(Bukkit.getOfflinePlayer(player).name)
                    .make()
                gui.item(index, skull) {
                    it.isCancelled = true
                    if (it.isLeftClick) {
                        sender.teleport(RespawnFeature.instance.locations[player]!!)
                    } else if (it.isRightClick) {
                        Bukkit.dispatchCommand(sender, "revive ${Bukkit.getOfflinePlayer(player).name}")
                    }
                }
            }
            sender.openInventory(gui.make())
        } else {
            val player = Bukkit.getPlayer(args[0])
            if (player == null) {
                Chat.sendMessage(sender, "<red>You must insert a valid player to respawn.")
                return false
            }
            if (!RespawnFeature.instance.respawnablePlayers.contains(player.uniqueId)) {
                Chat.sendMessage(sender, "<red>${player.name} is not a respawnable player.")
                return false
            }
            if (SpecFeature.instance.isSpec(player)) {
                SpecFeature.instance.unspec(player)
            }
            player.playSound(player.location, Sound.WOOD_CLICK, 10F, 1F)
            player.health = 20.0
            player.maxHealth = 20.0
            player.isFlying = false
            player.allowFlight = false
            player.foodLevel = 20
            player.saturation = 20F
            player.gameMode = GameMode.SURVIVAL
            player.inventory.clear()
            player.inventory.armorContents = null
            player.itemOnCursor = ItemStack(Material.AIR)
            val openInventory = player.openInventory
            if (openInventory.type == InventoryType.CRAFTING) {
                openInventory.topInventory.clear()
            }
            val effects = player.activePotionEffects
            for (effect in effects) {
                player.removePotionEffect(effect.type)
            }
            player.teleport(RespawnFeature.instance.locations[player.uniqueId]!!)

            for (effect in RespawnFeature.instance.effects[player.uniqueId]!!) {
                player.addPotionEffect(effect)
            }
            player.inventory.contents = RespawnFeature.instance.inventory[player.uniqueId]!!
            val team = TeamsFeature.manager.getOfflineTeam(player)
            if (team != null) {
                team.addPlayer(player)
            }
            if (RespawnFeature.instance.helmet[player.uniqueId] != null) player.inventory.helmet = RespawnFeature.instance.helmet[player.uniqueId]!!
            if (RespawnFeature.instance.chestplate[player.uniqueId] != null) player.inventory.chestplate = RespawnFeature.instance.chestplate[player.uniqueId]!!
            if (RespawnFeature.instance.leggings[player.uniqueId] != null) player.inventory.leggings = RespawnFeature.instance.leggings[player.uniqueId]!!
            if (RespawnFeature.instance.boots[player.uniqueId] != null) player.inventory.boots = RespawnFeature.instance.boots[player.uniqueId]!!
            RespawnFeature.instance.respawnablePlayers.remove(player.uniqueId)

            player.exp = RespawnFeature.instance.xp[player.uniqueId]!!
            player.level = RespawnFeature.instance.level[player.uniqueId]!!

            var list = ConfigFeature.instance.data!!.getStringList("game.list")
            if (list == null) list = ArrayList()
            list.add(player.name)
            ConfigFeature.instance.data!!.set("game.list", list)
            ConfigFeature.instance.saveData()
            WhitelistCommand().addWhitelist(player.name.lowercase())
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 300, 1000, true, false))
            Chat.sendMessage(player, "${Chat.prefix} You have been respawned by ${Chat.secondaryColor}${sender.name}<gray>.")
            Chat.sendMessage(sender, "${Chat.prefix} ${Chat.secondaryColor}${player.name}<gray> has been respawned.")
            PlayerRespawnListener.deathKicks[player.uniqueId]!!.cancelDeathKick()
        }
        return true
    }
}

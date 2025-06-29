package pink.mino.kraftwerk.features

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.lunarclient.apollo.Apollo
import com.lunarclient.apollo.module.staffmod.StaffModModule
import com.mongodb.MongoException
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.*
import java.util.*
import kotlin.math.floor
import kotlin.math.round

class InvSeeFeature(private val player: Player, private val target: Player) : BukkitRunnable() {
    override fun run() {
        if (!target.isOnline) {
            cancel()
            return
        }
        if (player.openInventory.title != "${target.name}'s Inventory") {
            cancel()
            return
        }
        for ((index, item) in target.inventory.contents.withIndex()) {
            if (item == null) {
                player.openInventory.topInventory.setItem(index, ItemStack(Material.AIR))
            } else {
                player.openInventory.topInventory.setItem(index, item)
            }
        }

        if (target.inventory.helmet != null) player.openInventory.topInventory.setItem(38, target.inventory.helmet)
        if (target.inventory.chestplate != null) player.openInventory.topInventory.setItem(39, target.inventory.chestplate)
        if (target.inventory.leggings != null) player.openInventory.topInventory.setItem(41, target.inventory.leggings)
        if (target.inventory.boots != null) player.openInventory.topInventory.setItem(42, target.inventory.boots)

        val info = ItemBuilder(Material.BOOK)
            .name("<red>Player Info")
            .addLore(" ")
            .addLore("<red>Statistics: ")
            .addLore(" ${Chat.dot} Health ${Chat.dash} ${PlayerUtils.getHealth(target)}")
            .addLore(" ${Chat.dot} Hunger ${Chat.dash} ${Chat.primaryColor}${target.foodLevel / 2}")
            .addLore(" ${Chat.dot} XP Level ${Chat.dash} ${Chat.primaryColor}${target.level} <dark_gray>(${Chat.primaryColor}${round(target.exp * 100)}%<dark_gray>)")
            .addLore(" ${Chat.dot} Kills ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.kills." + target.name)}")
            .addLore(" ${Chat.dot} Location ${Chat.dash} ${Chat.primaryColor}${target.location.blockX}, ${target.location.blockY}, ${target.location.blockZ}")
            .addLore(" ${Chat.dot} World ${Chat.dash} ${Chat.primaryColor}${target.location.world.name}")
            .addLore(" ")
            .addLore("<red>Mining: ")
            .addLore(" ${Chat.dot} Diamond ${Chat.dash} &b${SpecFeature.instance.diamondsMined[target.uniqueId] ?: 0}")
            .addLore(" ${Chat.dot} Gold ${Chat.dash} &6${SpecFeature.instance.goldMined[target.uniqueId] ?: 0}")
            .addLore(" ")
            .addLore("<red>Potion Effects: ")
        if (target.activePotionEffects.isEmpty()) {
            info.addLore(" ${Chat.dot} ${Chat.primaryColor}None.")
        } else {
            for (eff in target.activePotionEffects) {
                info.addLore(" ${Chat.dot} ${Chat.primaryColor}${InvseeUtils().getPotionName(eff.type).uppercase()} ${InvseeUtils().integerToRoman(eff.amplifier + 1)} <dark_gray>(<red>${InvseeUtils().potionDurationToString(eff.duration / 20)}<dark_gray>)")
            }
        }
        info.addLore(" ")
        val actualBook = info.make()
        player.openInventory.topInventory.setItem(40, actualBook)
    }
}

class SpecClickFeature : PacketAdapter(JavaPlugin.getPlugin(Kraftwerk::class.java), ListenerPriority.MONITOR, PacketType.Play.Client.WINDOW_CLICK) {
    override fun onPacketReceiving(e: PacketEvent) {
        if (e.packetType.equals(PacketType.Play.Client.WINDOW_CLICK)) {
            val packet = e.packet
            if (SpecFeature.instance.isSpec(e.player)) {
                val p = e.player
                when (packet.integers.read(1)) {
                    19 -> {
                        p.teleport(Location(p.world, 0.0, 100.0, 0.0))
                        Chat.sendMessage(p, "${Chat.dash} You have been teleported to 0,0.")
                    }
                    21 -> {
                        Bukkit.dispatchCommand(p, "nearby")
                    }
                    22 -> {
                        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get()

// Cycle through modes: 0 -> 1 -> 2 -> 0
                        profile.specSocialSpy = (profile.specSocialSpy + 1) % 3

                        val mode = profile.specSocialSpy
                        val modeText = when (mode) {
                            1 -> "<green>Social Spy <gray>(Social)"
                            2 -> "<green>Social Spy <gray>(All)"
                            else -> "<red>Social Spy <gray>(Off)"
                        }

                        val statusText = when (mode) {
                            0 -> "Disabled"
                            1 -> "Enabled for &dsocial<gray> commands"
                            2 -> "Enabled for &dall<gray> commands"
                            else -> "Unknown"
                        }

                        Chat.sendMessage(p, "${SpecFeature.instance.prefix} $statusText!")



                        val socialSpyItem = ItemBuilder(Material.NAME_TAG)
                            .name(modeText)
                            .addLore("<gray>Click to cycle social spy modes.")
                            .make()

                        p.inventory.setItem(22, socialSpyItem)

                    }
                    23 -> {
                        val list = ArrayList<Player>()
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (player != p && !SpecFeature.instance.isSpec(player)) {
                                list.add(player)
                            }
                        }
                        if (list.isEmpty()) {
                            Chat.sendMessage(p, "${Chat.dash} There are no players nearby.")
                            return
                        }
                        Chat.sendMessage(p, Chat.line)
                        Chat.sendCenteredMessage(p, "${Chat.primaryColor}&lPlayer Locations")
                        for (player in list) {
                            Chat.sendMessage(
                                p,
                                "${Chat.dash} <gray>${player.name} <gray>is at &b${floor(player.location.x)}<gray>, &b${floor(player.location.y)}<gray>, &b${
                                    floor(player.location.z)
                                }"
                            )
                        }
                        Chat.sendMessage(p, Chat.line)
                    }
                    25 -> {
                        Bukkit.dispatchCommand(p, "respawn")
                    }
                    else -> { return }
                }
            }
        }
    }
}


class SpecFeature : Listener {
    val diamondsMined: HashMap<UUID, Int> = HashMap()
    val goldMined: HashMap<UUID, Int> = HashMap()

    companion object {
        val instance = SpecFeature()
    }
    val prefix = "<dark_gray>[${Chat.primaryColor}Spec<dark_gray>]<gray>"
    val specStartTimes: HashMap<UUID, Long> = hashMapOf()

    fun joinSpec(p: Player) {
        specStartTimes[p.uniqueId] = Date().time
        p.health = 20.0
        p.foodLevel = 20
        p.saturation = 20F
        p.exp = 0F
        p.level = 0
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.helmet = ItemStack(Material.AIR)
        p.inventory.chestplate = ItemStack(Material.AIR)
        p.inventory.leggings = ItemStack(Material.AIR)
        p.inventory.boots = ItemStack(Material.AIR)
        p.inventory.setItemInOffHand(ItemStack(Material.AIR))
        p.gameMode = GameMode.SPECTATOR

        var list = ConfigFeature.instance.data!!.getStringList("game.list")
        if (list.contains(p.name)) list.remove(p.name)
        ConfigFeature.instance.data!!.set("game.list", list)
        list = ConfigFeature.instance.data!!.getStringList("game.specs")
        if (!list.contains(p.name)) list.add(p.name)
        ConfigFeature.instance.data!!.set("game.specs", list)
        ConfigFeature.instance.saveData()

        specChat("${Chat.secondaryColor}${p.name}<gray> has entered spectator mode.", p)
        Scoreboard.setScore(Chat.colored("${Chat.dash} <gray>Playing..."), PlayerUtils.getPlayingPlayers().size)

        val teleportTo00 = ItemBuilder(Material.ENDER_EYE)
            .name("${Chat.primaryColor}Teleport to 0,0")
            .addLore("<gray>Click the item to teleport yourself to <red>0,0<gray>.")
            .make()
        val nearby = ItemBuilder(Material.COMPASS)
            .name("${Chat.primaryColor}Nearby Players")
            .addLore("<gray>Click the item to see a list of nearby players.")
            .make()
        val locations = ItemBuilder(Material.MAP)
            .name("${Chat.primaryColor}Player Locations")
            .addLore("<gray>Click the item to see a list of player locations.")
            .make()
        val respawn = ItemBuilder(Material.BONE)
            .name("${Chat.primaryColor}Respawn Players")
            .addLore("<gray>Click to view a list of dead players that can be respawned.")
            .make()
        val mode = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get().specSocialSpy
        val modeText = when (mode) {
            1 -> "<green>Social Spy <gray>(Social)"
            2 -> "<green>Social Spy <gray>(All)"
            else -> "<red>Social Spy <gray>(Off)"
        }

        val socialSpyItem = ItemBuilder(Material.NAME_TAG)
            .name(modeText)
            .addLore("<gray>Click to cycle social spy modes.")
            .make()

        p.inventory.setItem(19, teleportTo00)
        p.inventory.setItem(21, nearby)
        p.inventory.setItem(22, socialSpyItem)
        p.inventory.setItem(23, locations)
        p.inventory.setItem(25, respawn)

        Chat.sendMessage(p, "$prefix You are now in spectator mode.")

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
            if (Apollo.getPlayerManager().hasSupport(p.uniqueId)) {
                Chat.sendMessage(p, "${Chat.dash} <gray>Your &bLunar Client<gray> staff modules have been enabled.")
                val apolloPlayer = Apollo.getPlayerManager().getPlayer(p.uniqueId).get()
                val staffModule = Apollo.getModuleManager().getModule(StaffModModule::class.java)
                staffModule.enableAllStaffMods(apolloPlayer)
            }
        }, 5L)
    }

    val commands = arrayListOf(
        "/msg",
        "/pm",
        "/r",
        "/reply",
        "/tell",
        "/message",
        "/whisper",
        "/mcc"
    )

    @EventHandler
    fun onPlayerCommand(e: PlayerCommandPreprocessEvent) {
        val messageParts = e.message.lowercase().split(" ")
        val baseCommand = messageParts[0]

        for (spectator in getSpecs()) {
            val player = Bukkit.getPlayer(spectator) ?: continue

            val profile = JavaPlugin.getPlugin(Kraftwerk::class.java)
                .profileHandler.lookupProfile(player.uniqueId).get()

            when (profile.specSocialSpy) {
                1 -> { // Mode 1: Social only
                    if (baseCommand in commands) {
                        Chat.sendMessage(player, "<yellow>&o${e.player.name} ${Chat.dash} <gray>${messageParts.joinToString(" ")}")
                    }
                }
                2 -> { // Mode 2: All commands
                    Chat.sendMessage(player, "<yellow>&o${e.player.name} ${Chat.dash} <gray>${messageParts.joinToString(" ")}")
                }
            }
        }
    }


    fun spec(p: Player) {
        specStartTimes[p.uniqueId] = Date().time
        SpawnFeature.instance.send(p)
        p.health = 20.0
        p.foodLevel = 20
        p.saturation = 20F
        p.exp = 0F
        p.level = 0
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.helmet = ItemStack(Material.AIR)
        p.inventory.chestplate = ItemStack(Material.AIR)
        p.inventory.leggings = ItemStack(Material.AIR)
        p.inventory.boots = ItemStack(Material.AIR)
        p.inventory.setItemInOffHand(ItemStack(Material.AIR))
        p.gameMode = GameMode.SPECTATOR

        var list = ConfigFeature.instance.data!!.getStringList("game.list")
        if (list.contains(p.name)) list.remove(p.name)
        ConfigFeature.instance.data!!.set("game.list", list)
        list = ConfigFeature.instance.data!!.getStringList("game.specs")
        if (!list.contains(p.name)) list.add(p.name)
        ConfigFeature.instance.data!!.set("game.specs", list)
        ConfigFeature.instance.saveData()

        specChat("${Chat.secondaryColor}${p.name}<gray> has entered spectator mode.", p)
        Scoreboard.setScore(Chat.colored("${Chat.dash} <gray>Playing..."), PlayerUtils.getPlayingPlayers().size)

        val teleportTo00 = ItemBuilder(Material.ENDER_EYE)
            .name("${Chat.primaryColor}Teleport to 0,0")
            .addLore("<gray>Click the item to teleport yourself to <red>0,0<gray>.")
            .make()
        val nearby = ItemBuilder(Material.COMPASS)
            .name("${Chat.primaryColor}Nearby Players")
            .addLore("<gray>Click the item to see a list of nearby players.")
            .make()
        val locations = ItemBuilder(Material.MAP)
            .name("${Chat.primaryColor}Player Locations")
            .addLore("<gray>Click the item to see a list of player locations.")
            .make()
        val respawn = ItemBuilder(Material.BONE)
            .name("${Chat.primaryColor}Respawn Players")
            .addLore("<gray>Click to view a list of dead players that can be respawned.")
            .make()
        val mode = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get().specSocialSpy
        val modeText = when (mode) {
            1 -> "<green>Social Spy <gray>(Social)"
            2 -> "<green>Social Spy <gray>(All)"
            else -> "<red>Social Spy <gray>(Off)"
        }

        val socialSpyItem = ItemBuilder(Material.NAME_TAG)
            .name(modeText)
            .addLore("<gray>Click to cycle social spy modes.")
            .make()

        p.inventory.setItem(19, teleportTo00)
        p.inventory.setItem(21, nearby)
        p.inventory.setItem(22, socialSpyItem)
        p.inventory.setItem(23, locations)
        p.inventory.setItem(25, respawn)

        Chat.sendMessage(p, "${prefix} You are now in spectator mode.")

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
            if (Apollo.getPlayerManager().hasSupport(p.uniqueId)) {
                val apolloPlayer = Apollo.getPlayerManager().getPlayer(p.uniqueId).get()
                val staffModule = Apollo.getModuleManager().getModule(StaffModModule::class.java)
                staffModule.enableAllStaffMods(apolloPlayer)
                Chat.sendMessage(p, "${Chat.dash} <gray>Your &bLunar Client<gray> staff modules have been enabled.")
            }
        }, 5L)
    }

    fun unspec(p: Player) {
        val endTime = Date().time
        try {
            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(p)!!.timeSpectated += (endTime - specStartTimes[p.uniqueId]!!)
        } catch (e: MongoException) {
            e.printStackTrace()
        }
        p.health = 20.0
        p.foodLevel = 20
        p.saturation = 20F
        p.exp = 0F
        p.level = 0
        val effects = p.activePotionEffects
        for (effect in effects) {
            p.removePotionEffect(effect.type)
        }
        p.inventory.clear()
        p.inventory.helmet = ItemStack(Material.AIR)
        p.inventory.chestplate = ItemStack(Material.AIR)
        p.inventory.leggings = ItemStack(Material.AIR)
        p.inventory.boots = ItemStack(Material.AIR)
        p.inventory.setItemInOffHand(ItemStack(Material.AIR))

        SpawnFeature.instance.send(p)
        var list = ConfigFeature.instance.data!!.getStringList("game.list")
        if (!list.contains(p.name)) list.add(p.name)
        ConfigFeature.instance.data!!.set("game.list", list)
        list = ConfigFeature.instance.data!!.getStringList("game.specs")
        list.remove(p.name)
        ConfigFeature.instance.data!!.set("game.specs", list)
        ConfigFeature.instance.saveData()

        specChat("${Chat.secondaryColor}${p.name}<gray> has left spectator mode.", p)
        Chat.sendMessage(p, "${prefix} You are no longer in spectator mode.")
        Scoreboard.setScore(Chat.colored("${Chat.dash} <gray>Playing..."), PlayerUtils.getPlayingPlayers().size)

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
            if (Apollo.getPlayerManager().hasSupport(p.uniqueId)) {
                val apolloPlayer = Apollo.getPlayerManager().getPlayer(p.uniqueId).get()
                val staffModule = Apollo.getModuleManager().getModule(StaffModModule::class.java)
                staffModule.disableAllStaffMods(apolloPlayer)
                Chat.sendMessage(p, "${Chat.dash} <gray>Your &bLunar Client<gray> staff modules have been disabled.")
            }
        }, 5L)

    }

    fun getSpecs(): List<String> {
        return ConfigFeature.instance.data!!.getStringList("game.specs")
    }

    fun isSpec(p: OfflinePlayer): Boolean {
        return getSpecs().contains(p.name)
    }

    fun specChat(chat: String, p: Player? = null) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (getSpecs().contains(player.name)) {
                if (p != null && p == player) {
                    continue
                }
                Chat.sendMessage(player, "$prefix $chat")
            }
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        if (isSpec(p)) {
            e.isCancelled = true
            if (e.currentItem != null && e.currentItem!!.type != Material.AIR) {
                when (e.currentItem!!.itemMeta.displayName) {
                    "${Chat.primaryColor}Teleport to 0,0" -> {
                        p.teleport(Location(p.world, 0.0, 100.0, 0.0))
                        Chat.sendMessage(p, "${prefix} You have been teleported to 0,0.")
                    }
                    "${Chat.primaryColor}Nearby Players" -> {
                        Bukkit.dispatchCommand(p, "nearby")
                    }
                    "${Chat.primaryColor}Player Locations" -> {
                        val list = ArrayList<Player>()
                        for (player in Bukkit.getOnlinePlayers()) {
                            if (player != p && !isSpec(player)) {
                                list.add(player)
                            }
                        }
                        if (list.isEmpty()) {
                            Chat.sendMessage(p, "${prefix} There are no players online.")
                        }
                        Chat.sendMessage(p, Chat.line)
                        Chat.sendCenteredMessage(p, "${Chat.primaryColor}&lPlayer Locations")
                        for (player in list) {
                            Chat.sendMessage(p, "${prefix} <gray>${player.name} <gray>is at &b${floor(player.location.x)}, <gray>${floor(player.location.y)}, <gray>${floor(player.location.z)}")
                        }
                        Chat.sendMessage(p, Chat.line)
                    }
                    "${Chat.primaryColor}Respawn Players" -> {
                        Bukkit.dispatchCommand(p, "respawn")
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (getSpecs().contains(e.player.name)) {
            if (e.player.gameMode == GameMode.SPECTATOR) {
                if (e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK) {
                    e.isCancelled = true
                    val list = ArrayList<Player>()
                    for (player in Bukkit.getOnlinePlayers()) {
                        if (getSpecs().contains(player.name) || player.world.name == "Spawn") continue
                        list.add(player)
                    }
                    if (list.isEmpty()) {
                        Chat.sendMessage(e.player, "<red>No players to teleport to!")
                        return
                    }
                    val target = list.random()
                    e.player.teleport(target.location)
                    Chat.sendMessage(e.player, "${prefix} Teleported to ${Chat.secondaryColor}${target.name}<gray>!")
                }
            }
        }
    }

    @EventHandler
    fun onPlayerInteractWithPlayer(e: PlayerInteractEntityEvent) {
        if (e.player.itemInHand == null) return
        if (getSpecs().contains(e.player.name)) {
            if (e.rightClicked.type == EntityType.PLAYER) {
                val player = (e.rightClicked as Player)
                val gui = GuiBuilder().rows(5).name(ChatColor.translateAlternateColorCodes('&', "${player.name}'s Inventory"))
                e.player.openInventory(gui.make())
                InvSeeFeature(e.player, player).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0, 20L)
            } else {
                Chat.sendMessage(e.player, "<red>You aren't right clicking anyone.")
                return
            }
        }
    }

    @EventHandler
    fun onItemDrop(e: PlayerDropItemEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageByEntityEvent) {
        if (e.damager is Player) {
            if (getSpecs().contains(((e.damager) as Player).name)) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onItemPickup(e: PlayerPickupItemEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (getSpecs().contains(e.player.name)) {
            e.isCancelled = true
        }
    }

    var brokenBlocks: HashMap<UUID, HashSet<Block>> = HashMap<UUID, HashSet<Block>>()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onBreak(e: BlockBreakEvent) {
        if (GameState.currentState != GameState.INGAME) return
        val p = e.player
        if (brokenBlocks.containsKey(p.uniqueId)) {
            if (brokenBlocks[p.uniqueId]!!.contains(e.block)) return
        }
        if (e.block.type == Material.DIAMOND_ORE) {
            var diamonds = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.DIAMOND_ORE) {
                            diamonds++
                            if (diamondsMined[p.uniqueId] == null) diamondsMined[p.uniqueId] = 0
                            diamondsMined[p.uniqueId] = diamondsMined[p.uniqueId]!! + 1
                            if (brokenBlocks.containsKey(p.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[p.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name}<gray> has mined &bDiamond Ore<gray>. <dark_gray>(<gray>T: &b${diamondsMined[p.uniqueId]} <dark_gray>| <gray>V: &b${diamonds}<dark_gray>)"))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        } else if (e.block.type == Material.GOLD_ORE) {
            var gold = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.GOLD_ORE) {
                            gold++
                            if (goldMined[p.uniqueId] == null) goldMined[p.uniqueId] = 0
                            goldMined[p.uniqueId] = goldMined[p.uniqueId]!! + 1
                            if (brokenBlocks.containsKey(p.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[p.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[p.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name}<gray> has mined &6Gold Ore<gray>. <dark_gray>(<gray>T: &6${goldMined[p.uniqueId]} <dark_gray>| <gray>V: &6${gold}<dark_gray>)"))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity !is Player) return
        val p = e.entity as Player
        if (e.finalDamage == 0.0 || e.damage == 0.0 || e.isCancelled) return
        if (p.health - e.finalDamage <= 0) return
        val percentage = (e.finalDamage / 2) * 10
        when (e.cause) {
            EntityDamageEvent.DamageCause.FALL -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                            val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} <dark_gray>(${PlayerUtils.getHealth(p)}<dark_gray>)<gray> took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%<gray> due to &fFall<gray>."))
                            comp.clickEvent = ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/tp ${p.name}"
                            )
                            player.spigot().sendMessage(comp)
                        }, 1L)
                    }
                }
            }
            EntityDamageEvent.DamageCause.FIRE, EntityDamageEvent.DamageCause.FIRE_TICK, EntityDamageEvent.DamageCause.LAVA, EntityDamageEvent.DamageCause.MELTING -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} <dark_gray>(${PlayerUtils.getHealth(p)}<dark_gray>)<gray> took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%<gray> due to &fBurning<gray>."))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }
                }
            }
            EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.PROJECTILE -> {
                return
            }
            else -> {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                            val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} <dark_gray>(${PlayerUtils.getHealth(p)}<dark_gray>)<gray> took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%<gray> due to &fUnknown<gray>."))
                            comp.clickEvent = ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/tp ${p.name}"
                            )
                            player.spigot().sendMessage(comp)
                        }, 1L)
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerDamageByPlayer(e: EntityDamageByEntityEvent) {
        if (GameState.currentState != GameState.INGAME) return
        if (e.entity !is Player) return
        val p = e.entity as Player
        if (e.finalDamage == 0.0 || e.damage == 0.0 || e.isCancelled) return
        if (p.health - e.finalDamage <= 0) return
        val percentage = (e.finalDamage / 2) * 10
        if (e.entity is Monster) {
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} <dark_gray>(${PlayerUtils.getHealth(p)}<dark_gray>)<gray> took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%<gray> due to &fPvE<gray>."))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        }
        if (e.damager is Player) {
            val damager = e.damager as Player
            for (player in Bukkit.getOnlinePlayers()) {
                if (getSpecs().contains(player.name)) {
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                        val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} <dark_gray>(${PlayerUtils.getHealth(p)}<dark_gray>)<gray> took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%<gray> due to ${PlayerUtils.getPrefix(damager)}${damager.name} <dark_gray>(${PlayerUtils.getHealth(damager)}<dark_gray>)<gray>. <dark_gray>(&fPvP<dark_gray>)"))
                        comp.clickEvent = ClickEvent(
                            ClickEvent.Action.RUN_COMMAND,
                            "/tp ${p.name}"
                        )
                        player.spigot().sendMessage(comp)
                    }, 1L)
                }
            }
        } else if (e.damager is Arrow) {
            val a = e.damager as Arrow
            if (a.shooter is Player) {
                val damager = a.shooter as Player
                for (player in Bukkit.getOnlinePlayers()) {
                    if (getSpecs().contains(player.name)) {
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                            val comp = TextComponent(Chat.colored("$prefix ${PlayerUtils.getPrefix(p)}${p.name} <dark_gray>(${PlayerUtils.getHealth(p)}<dark_gray>)<gray> took ${HealthChatColorer.returnHealth(percentage)}${percentage.toInt()}%<gray> due to ${PlayerUtils.getPrefix(damager)}${damager.name} <dark_gray>(${PlayerUtils.getHealth(damager)}<dark_gray>)<gray>. <dark_gray>(&fBow<dark_gray>)"))
                            comp.clickEvent = ClickEvent(
                                ClickEvent.Action.RUN_COMMAND,
                                "/tp ${p.name}"
                            )
                            player.spigot().sendMessage(comp)
                        }, 1L)
                    }
                }
            }
        }
    }

}
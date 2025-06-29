package pink.mino.kraftwerk.listeners

import com.google.gson.Gson
import com.mongodb.client.model.Filters
import me.lucko.helper.Schedulers
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.commands.WhitelistCommand
import pink.mino.kraftwerk.features.*
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.*
import redis.clients.jedis.Jedis
import java.util.*
import net.milkbowl.vault.chat.Chat as VaultChat

class PlayerJoinListener : Listener {

    private var vaultChat: VaultChat? = null

    init {
        vaultChat = Bukkit.getServer().servicesManager.load(VaultChat::class.java)
    }

    fun checkEvaders(player: OfflinePlayer) {
        val profile = Kraftwerk.instance.profileHandler.getProfile(player.uniqueId) ?: return

        val alts = profile.alts
        if (alts.isEmpty()) return

        // Only collect alts that have a punishment
        val punishedAltStatuses = alts.mapNotNull { altUuid ->
            val alt = Bukkit.getOfflinePlayer(altUuid)
            val banned = PunishmentFeature.getActivePunishment(alt, PunishmentType.BAN)
            val muted = PunishmentFeature.getActivePunishment(alt, PunishmentType.MUTE)

            when {
                banned != null -> "${ChatColor.RED}${alt.name ?: "Unknown"}${ChatColor.GRAY} (banned)"
                muted != null -> "${ChatColor.YELLOW}${alt.name ?: "Unknown"}${ChatColor.GRAY} (muted)"
                else -> null // Skip alts with no punishment
            }
        }

        if (punishedAltStatuses.isEmpty()) return

        val message = "${Chat.prefix} ${Chat.secondaryColor}${player.name}'s alts:${Chat.secondaryColor} ${punishedAltStatuses.joinToString(", ")}"
        val coloredMessage = Chat.colored(message)

        Bukkit.getOnlinePlayers()
            .filter { it.hasPermission("uhc.staff") }
            .forEach { staff ->
                staff.sendMessage(coloredMessage)
            }
    }



    fun checkAndMergeAlts(player: Player) {
        val plugin = JavaPlugin.getPlugin(Kraftwerk::class.java)
        val profile = plugin.profileHandler.getProfile(player.uniqueId)!!
        val currentIp = player.address?.address?.hostAddress ?: return

        // Update current IP if needed
        if (profile.lastKnownIp != currentIp) {
            profile.lastKnownIp = currentIp
            plugin.profileHandler.saveProfile(profile)
        }

        val collection = plugin.dataSource.getCollection("players")

        with(collection) {
            val matching = find(
                Filters.and(
                    Filters.eq("lastKnownIp", currentIp),
                    Filters.ne("uuid", player.uniqueId) // use UUID object directly
                )
            ).toList()

            for (doc in matching) {
                val otherUuid = try {
                    doc.get("uuid", UUID::class.java)
                } catch (_: Exception) {
                    continue
                } ?: continue

                val otherProfile = plugin.profileHandler.getProfile(otherUuid) ?: continue

                var updated = false
                if (!profile.alts.contains(otherUuid)) {
                    profile.alts.add(otherUuid)
                    updated = true
                }
                if (!otherProfile.alts.contains(player.uniqueId)) {
                    otherProfile.alts.add(player.uniqueId)
                    plugin.profileHandler.saveProfile(otherProfile)
                }
                if (updated) {
                    plugin.profileHandler.saveProfile(profile)
                }
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        Scoreboard.setScore(Chat.colored("${Chat.dash} <gray>Playing..."), PlayerUtils.getPlayingPlayers().size)

        for (online in Bukkit.getOnlinePlayers()) {
            if (online != player) {
                online.showPlayer(player)
                player.showPlayer(online)
            }
        }

        val group: String = vaultChat!!.getPrimaryGroup(player)
        val prefix: String = if (vaultChat!!.getGroupPrefix(player.world, group) != "<gray>") Chat.colored(vaultChat!!.getGroupPrefix(player.world, group)) else Chat.colored("<green>")
        e.joinMessage = ChatColor.translateAlternateColorCodes('&', "<dark_gray>(&2+<dark_gray>)&r ${prefix}${player.displayName} <dark_gray>[&2${Bukkit.getOnlinePlayers().size}<dark_gray>/&2${Bukkit.getServer().maxPlayers}<dark_gray>]")
        /*Schedulers.sync().runLater({
            Chat.sendMessage(player, "<dark_gray>➡ <gray>Please consider donating to the server to keep it up for another month! The store link is <yellow>https://applejuice.tebex.io<gray> or just use <red>/buy<gray>!")
        }, 1L)*/
        if (GameState.currentState == GameState.LOBBY) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                SpawnFeature.instance.send(player)
                if (PerkChecker.checkPerks(player).contains(Perk.SPAWN_FLY)) {
                    player.allowFlight = true
                    player.isFlying = true
                }
                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                    SpecFeature.instance.spec(player)
                }
            }, 1L)
        } else {
            val scatter = JavaPlugin.getPlugin(Kraftwerk::class.java).scatterLocs
            if (scatter.contains(player.name.lowercase())) {
                if (JavaPlugin.getPlugin(Kraftwerk::class.java).scattering) {
                    return
                }
                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                    SpecFeature.instance.joinSpec(player)
                    return
                }

                if (GameState.currentState == GameState.WAITING || UHCFeature().scattering) {
                    UHCFeature().freeze()
                }
                player.teleport(scatter[player.name.lowercase()]!!)
                scatter.remove(player.name.lowercase())
                if (TeamsFeature.manager.getTeam(player) != null) {
                    for (entry in TeamsFeature.manager.getTeam(player)!!.entries) {
                        val tm = Bukkit.getPlayer(entry) ?: continue
                        tm.hidePlayer(player)
                        object: BukkitRunnable() {
                            override fun run() {
                                tm.showPlayer(player)
                            }
                        }.runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), 3L)
                    }
                }

                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Automatically late scattered ${Chat.primaryColor}${player.name}<gray>."))
                player.playSound(player.location, Sound.BLOCK_LEVER_CLICK, 10F, 1F)
                player.maxHealth = 20.0
                player.health = player.maxHealth
                player.isFlying = false
                player.allowFlight = false
                player.foodLevel = 20
                player.saturation = 20F
                player.gameMode = GameMode.SURVIVAL
                player.inventory.clear()
                player.inventory.helmet = ItemStack(Material.AIR)
                player.inventory.chestplate = ItemStack(Material.AIR)
                player.inventory.leggings = ItemStack(Material.AIR)
                player.inventory.boots = ItemStack(Material.AIR)
                player.inventory.setItemInOffHand(ItemStack(Material.AIR))
                player.enderChest.clear()
                player.setItemOnCursor(ItemStack(Material.AIR))
                val openInventory = player.openInventory
                if (openInventory.type == InventoryType.CRAFTING) {
                    openInventory.topInventory.clear()
                }
                val effects = player.activePotionEffects
                for (effect in effects) {
                    player.removePotionEffect(effect.type)
                }
                player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 300, 1000, true, false))
                player.inventory.setItem(0, ItemStack(Material.COOKED_BEEF, ConfigFeature.instance.data!!.getInt("game.starterfood")))
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(player)!!.gamesPlayed++
                for (scenario in ScenarioHandler.getActiveScenarios()) {
                    scenario.givePlayer(player)
                }
                WhitelistCommand().addWhitelist(player.name)
                var list = ConfigFeature.instance.data!!.getStringList("game.list")
                if (list == null) list = ArrayList<String>()
                if (!list.contains(player.name)) {
                    list.add(player.name)
                }
                ConfigFeature.instance.data!!.set("game.list", list)
                ConfigFeature.instance.saveData()
                return
            }
            if (GameState.currentState == GameState.WAITING) {
                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                    SpecFeature.instance.joinSpec(player)
                    return
                }
                return
            } else {
                if (SpecFeature.instance.getSpecs().contains(player.name)) {
                    SpecFeature.instance.joinSpec(player)
                    return
                }
                if (!ConfigFeature.instance.data!!.getStringList("game.list").contains(player.name.lowercase()) && !ConfigFeature.instance.data!!.getStringList("game.list").contains(player.name)) {
                    if (SpecFeature.instance.getSpecs().contains(player.name)) {
                        return
                    }
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                        SpawnFeature.instance.send(player)
                    }, 1L)
                    SpecFeature.instance.specChat("${Chat.secondaryColor}${player.name}<gray> hasn't been late-scattered, sending them to spawn.")
                    val comp = TextComponent(Chat.colored("${Chat.dash} &d&lLatescatter player?"))
                    val comp2 = TextComponent(Chat.colored("${Chat.dash} ${Chat.primaryColor}&lInsert latescatter command?"))
                    comp.clickEvent = ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        "/ls ${player.name}"
                    )
                    comp2.clickEvent = ClickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND,
                        "/ls ${player.name} "
                    )
                    SpecFeature.instance.getSpecs().forEach {
                        val p = Bukkit.getOfflinePlayer(it)
                        if (p.isOnline) {
                            (p as Player).spigot().sendMessage(comp)
                            p.spigot().sendMessage(comp2)
                        }
                    }
                }
            }
        }
        if (GameState.currentState != GameState.WAITING && player.hasPotionEffect(PotionEffectType.JUMP_BOOST) &&
            player.hasPotionEffect(PotionEffectType.BLINDNESS) &&
            player.hasPotionEffect(PotionEffectType.RESISTANCE) &&
            player.hasPotionEffect(PotionEffectType.MINING_FATIGUE) &&
            player.hasPotionEffect(PotionEffectType.SLOWNESS) &&
            player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
            player.removePotionEffect(PotionEffectType.JUMP_BOOST)
            player.removePotionEffect(PotionEffectType.BLINDNESS)
            player.removePotionEffect(PotionEffectType.RESISTANCE)
            player.removePotionEffect(PotionEffectType.MINING_FATIGUE)
            player.removePotionEffect(PotionEffectType.SLOWNESS)
            player.removePotionEffect(PotionEffectType.INVISIBILITY)
        }
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
            if (JavaPlugin.getPlugin(Kraftwerk::class.java).fullbright.contains(e.player.name.lowercase())) {
                player.addPotionEffect(PotionEffect(PotionEffectType.NIGHT_VISION, 1028391820, 0, false, false))
            }
        }, 5L)
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
            checkAndMergeAlts(player)
            if (!(player as Player).hasPermission("uhc.staff")) {
                checkEvaders(player)
            }
        }, 40L)

//        if (Apollo.getPlayerManager().hasSupport(player.uniqueId)) {
//            val apolloPlayer = Apollo.getPlayerManager().getPlayer(player.uniqueId).get()
//            val richPresenceModule = Apollo.getModuleManager().getModule(RichPresenceModule::class.java)
//            val richPresence = ServerRichPresence.builder()
//                .gameName("UHC")
//            richPresenceModule.overrideServerRichPresence(apolloPlayer, )
//        }

        if (
            ConfigFeature.instance.config!!.getBoolean("database.redis.enabled")
            && Kraftwerk.instance.redisManager != null
            && ConfigFeature.instance.config!!.getString("chat.serverName") != null
        ) {
            Schedulers.async().run {
                Kraftwerk.instance.redisManager.executeCommand { redis: Jedis ->
                    redis.publish(
                        "players",
                        Gson().toJson(
                            PlayerJoinMessage(
                                player.uniqueId,
                                Kraftwerk.instance.sessionId
                            )
                        )
                    )
                    null
                }
            }
        }
    }
}
package pink.mino.kraftwerk.features

import com.mongodb.client.model.Filters
import me.lucko.helper.Schedulers
import me.lucko.helper.utils.Log
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minecraft.server.level.ServerPlayer
//import net.minecraft.server.v1_8_R3.EntityLiving
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.entity.CraftPlayer
//import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.player.PlayerBucketEmptyEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.*
import java.util.*
import kotlin.math.floor

// TODO: Add 1.8 support

class ArenaFeature : Listener {
    companion object {
        val instance = ArenaFeature()
    }
    val prefix = "<dark_gray>[${Chat.primaryColor}Arena<dark_gray>]<gray>"

    fun unbreakableItem(material: Material): ItemStack {
        val item = ItemStack(material)
        val meta = item.itemMeta
        meta.isUnbreakable = true
        item.itemMeta = meta
        return item
    }

    fun alias(value: String?, player: Player): ItemStack {
        when (value) {
            "SWORD" -> {
                return unbreakableItem(Material.DIAMOND_SWORD)
            }
            "BOW" -> {
                return unbreakableItem(Material.BOW)
            }
            "ROD" -> {
                return unbreakableItem(Material.FISHING_ROD)
            }
            "BLOCKS" -> {
                return ItemStack(Material.valueOf(Kraftwerk.instance.profileHandler.getProfile(player.uniqueId)!!.arenaBlock!!), 64)
            }
            "WATER" -> {
                return ItemStack(Material.WATER_BUCKET)
            }
            "LAVA" -> {
                return ItemStack(Material.LAVA_BUCKET)
            }
            "FOOD" -> {
                return ItemStack(Material.GOLDEN_CARROT, 64)
            }
            "GAPPLES" -> {
                return ItemStack(Material.GOLDEN_APPLE, 5)
            }
            "HEADS" -> {
                val goldenHeads = ItemStack(Material.GOLDEN_APPLE, 3)
                val meta = goldenHeads.itemMeta
                meta.displayName(MiniMessage.miniMessage().deserialize("<gold>Golden Head"))
                goldenHeads.itemMeta = meta
                return goldenHeads
            }
            else -> {
                return ItemStack(Material.AIR)
            }
        }
    }

    val seeds = hashMapOf<UUID, Int>()

    fun send(p: Player) {
        if (SpecFeature.instance.getSpecs().contains(p.name)) {
            SpecFeature.instance.unspec(p)
        }
        p.health = 20.0
        p.fireTicks = 0
        p.foodLevel = 20
        p.saturation = 20F
        p.exp = 0F
        p.level = 0
        seeds[p.uniqueId] = 0
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
        p.gameMode = GameMode.SURVIVAL

        Schedulers.async().run {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("kits")) {
                try {
                    val document = find(Filters.eq("uuid", p.uniqueId)).first()!!
                    Schedulers.sync().run {
                        p.inventory.setItem(0, alias(document.getEmbedded(listOf("kit", "slot1"), String::class.java), p))
                        p.inventory.setItem(1, alias(document.getEmbedded(listOf("kit", "slot2"), String::class.java), p))
                        p.inventory.setItem(2, alias(document.getEmbedded(listOf("kit", "slot3"), String::class.java), p))
                        p.inventory.setItem(3, alias(document.getEmbedded(listOf("kit", "slot4"), String::class.java), p))
                        p.inventory.setItem(4, alias(document.getEmbedded(listOf("kit", "slot5"), String::class.java), p))
                        p.inventory.setItem(5, alias(document.getEmbedded(listOf("kit", "slot6"), String::class.java), p))
                        p.inventory.setItem(6, alias(document.getEmbedded(listOf("kit", "slot7"), String::class.java), p))
                        p.inventory.setItem(7, alias(document.getEmbedded(listOf("kit", "slot8"), String::class.java), p))
                        p.inventory.setItem(8, alias(document.getEmbedded(listOf("kit", "slot9"), String::class.java), p))
                        p.inventory.setItem(9, ItemStack(Material.ARROW, 64))

                        p.inventory.helmet = unbreakableItem(Material.IRON_HELMET)
                        p.inventory.chestplate = unbreakableItem(Material.IRON_CHESTPLATE)
                        p.inventory.leggings = unbreakableItem(Material.IRON_LEGGINGS)
                        p.inventory.boots = unbreakableItem(Material.DIAMOND_BOOTS)
                    }
                } catch (e: NullPointerException) {
                    Schedulers.sync().run {
                        p.inventory.setItem(0, unbreakableItem(Material.DIAMOND_SWORD))
                        p.inventory.setItem(1, unbreakableItem(Material.FISHING_ROD))
                        p.inventory.setItem(2, unbreakableItem(Material.BOW))
                        p.inventory.setItem(3, ItemStack(Material.valueOf(Kraftwerk.instance.profileHandler.getProfile(p.uniqueId)!!.arenaBlock!!), 64))
                        p.inventory.setItem(4, ItemStack(Material.WATER_BUCKET))
                        p.inventory.setItem(5, ItemStack(Material.LAVA_BUCKET))
                        p.inventory.setItem(6, ItemStack(Material.GOLDEN_CARROT, 64))
                        p.inventory.setItem(7, ItemStack(Material.GOLDEN_APPLE, 5))
                        val goldenHeads = ItemStack(Material.GOLDEN_APPLE, 3)
                        val meta = goldenHeads.itemMeta
                        meta.displayName(MiniMessage.miniMessage().deserialize("<gold>Golden Head"))
                        goldenHeads.itemMeta = meta
                        p.inventory.setItem(8, goldenHeads)
                        p.inventory.setItem(9, ItemStack(Material.ARROW, 64))

                        p.inventory.helmet = unbreakableItem(Material.IRON_HELMET)
                        p.inventory.chestplate = unbreakableItem(Material.IRON_CHESTPLATE)
                        p.inventory.leggings = unbreakableItem(Material.IRON_LEGGINGS)
                        p.inventory.boots = unbreakableItem(Material.DIAMOND_BOOTS)
                    }
                }
            }
        }
        ScatterFeature.scatterSolo(p, Bukkit.getWorld("Arena")!!, 100)
        p.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 60, 100, true, false))
        p.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, 60, 100, true, false))
    }

    @EventHandler
    fun onPlayerPickup(e: PlayerPickupItemEvent) {
        if (e.player.world.name != "Arena") return
        if (e.item.itemStack.type == Material.WHEAT_SEEDS) {
            if (seeds[e.player.uniqueId] == null) seeds[e.player.uniqueId] = 0
            seeds[e.player.uniqueId] = seeds[e.player.uniqueId]!! + 1
            if (seeds[e.player.uniqueId]!! >= 10) {
                for ((index, item) in e.player.inventory.contents.withIndex()) {
                    if (item == null || item.type == Material.AIR) continue
                    if (item.type == Material.DIAMOND_SWORD) {
                        val sword = ItemBuilder(Material.DIAMOND_SWORD)
                            .name("&5&lSeedly Sword")
                            .addEnchantment(Enchantment.SHARPNESS, 1)
                            .addEnchantment(Enchantment.FIRE_ASPECT, 1)
                            .make()
                        e.player.inventory.setItem(index, sword)
                    }
                    if (item.type == Material.BOW) {
                        val bow = ItemBuilder(Material.BOW)
                            .name("&5&lSeedly Bow")
                            .addEnchantment(Enchantment.POWER, 1)
                            .addEnchantment(Enchantment.FLAME, 1)
                            .make()
                        e.player.inventory.setItem(index, bow)
                    }
                }
                Chat.sendMessage(e.player, "<dark_gray>[&d&lSecret<dark_gray>]<gray>&o You get the &5&oSeedly<gray> buff!")
                seeds[e.player.uniqueId] = 0
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (e.entity.world.name != "Arena") return
        if (e.entityType == EntityType.PLAYER) {
            if (e.finalDamage >= (e.entity as Player).health) {
                e.damage = 0.0
                e.isCancelled = true
                e.entity.world.strikeLightningEffect(e.entity.location)
                this.send((e.entity as Player))
                if(e is EntityDamageByEntityEvent) {
                    if (e.entityType === EntityType.PLAYER && e.damager != null && e.damager.type === EntityType.ARROW && (e.damager as Arrow).shooter === e.entity) {
                        e.isCancelled = true
                    }
                    if (e.damager.type == EntityType.PLAYER) {
                        if ((e.damager as Player).hasPotionEffect(PotionEffectType.RESISTANCE)) {
                            (e.damager as Player).removePotionEffect(PotionEffectType.RESISTANCE)
                        }
                    }
                    if (e.damager.type == EntityType.PLAYER) {
                        val killer = e.damager as Player
                        val victim = e.entity as Player
                        killer.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 2, true, true))
                        val goldenHeads = ItemStack(Material.GOLDEN_APPLE, 1)
                        val meta = goldenHeads.itemMeta
                        meta.displayName(MiniMessage.miniMessage().deserialize("<gold>Golden Head"))
                        goldenHeads.itemMeta = meta
                        killer.inventory.addItem(goldenHeads)
                        killer.inventory.addItem(ItemStack(Material.ARROW, 8))
                        val el: ServerPlayer = (killer as CraftPlayer).handle
                        val health = floor(killer.health / 2 * 10 + el.absorptionAmount / 2 * 10)
                        val color = HealthChatColorer.returnHealth(health)
                        killer.sendMessage(Chat.colored("$prefix <gray>You killed ${Chat.secondaryColor}${victim.name}<gray>!"))
                        victim.sendMessage(Chat.colored("$prefix <gray>You were killed by ${Chat.secondaryColor}${killer.name} <dark_gray>(${color}${health}❤<dark_gray>)"))
                        Killstreak.addKillstreak(killer)
                        if (Killstreak.getKillstreak(killer) > JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(killer)!!.highestArenaKs) {
                            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(killer)!!.highestArenaKs = Killstreak.getKillstreak(killer)
                        }
                        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(killer)!!.arenaKills++
                        JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(victim)!!.arenaDeaths++
                        Log.info("${killer.name} now has a killstreak of ${Killstreak.getKillstreak(killer)}.")
                        if (Killstreak.getKillstreak(victim) >= 5) {
                            sendToPlayers("${prefix}${Chat.secondaryColor} ${victim.name}<gray> lost their killstreak of ${Chat.secondaryColor}${
                                Killstreak.getKillstreak(
                                    victim
                                )
                            } kills<gray> to ${Chat.secondaryColor}${killer.name}<gray>!")
                        }
                        if (Killstreak.getKillstreak(killer) > 3) {
                            sendToPlayers(Chat.colored("$prefix ${Chat.secondaryColor}${killer.name}<gray> now has a killstreak of ${Chat.secondaryColor}${
                                Killstreak.getKillstreak(
                                    killer
                                )
                            } kills<gray>!"))
                            killer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 2, false, true))
                        }
                        Killstreak.resetKillstreak(victim)
                    } else if (e.damager.type === EntityType.ARROW && (e.damager as Arrow).shooter as Player != e.entity as Player) {
                        val killer = (e.damager as Arrow).shooter as Player
                        val victim = e.entity as Player
                        killer.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 3, true, true))
                        killer.addPotionEffect(PotionEffect(PotionEffectType.STRENGTH, 20 * 5, 0, true, true))

                        val goldenHeads = ItemStack(Material.GOLDEN_APPLE, 1)
                        val meta = goldenHeads.itemMeta
                        meta.displayName(MiniMessage.miniMessage().deserialize("<gold>Golden Head"))
                        goldenHeads.itemMeta = meta
                        killer.inventory.addItem(goldenHeads)
                        killer.inventory.addItem(ItemStack(Material.ARROW, 8))
                        val el: ServerPlayer = (killer as CraftPlayer).handle
                        val health = floor(killer.health / 2 * 10 + el.absorptionAmount / 2 * 10)
                        val color = HealthChatColorer.returnHealth(health)
                        killer.sendMessage(Chat.colored("$prefix <gray>You killed ${Chat.secondaryColor}${victim.name}<gray>!"))
                        victim.sendMessage(Chat.colored("$prefix <gray>You were killed by ${Chat.secondaryColor}${killer.name} <dark_gray>(${color}${health}❤<dark_gray>)"))
                        Killstreak.addKillstreak(killer)
                        print("${killer.name} now has a killstreak of ${Killstreak.getKillstreak(killer)}.")
                        if (Killstreak.getKillstreak(victim) >= 5) {
                            sendToPlayers("${prefix}${Chat.secondaryColor} ${victim.name}<gray> lost their killstreak of ${Chat.secondaryColor}${
                                Killstreak.getKillstreak(
                                    victim
                                )
                            } kills<gray> to ${Chat.secondaryColor}${killer.name}<gray>!")
                        }
                        if (Killstreak.getKillstreak(killer) > 3) {
                            sendToPlayers(Chat.colored("$prefix ${Chat.secondaryColor}${killer.name}<gray> now has a killstreak of ${Chat.secondaryColor}${
                                Killstreak.getKillstreak(
                                    killer
                                )
                            } kills<gray>!"))
                            killer.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 200, 2, false, true))
                        }
                        Killstreak.resetKillstreak(victim)
                    }
                } else {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(e.entity as Player)!!.arenaDeaths++
                    Chat.sendMessage((e.entity as Player), "$prefix You died!")
                    if (Killstreak.getKillstreak((e.entity as Player)) >= 5) {
                        sendToPlayers("${prefix}${Chat.secondaryColor} ${(e.entity as Player).name}<gray> lost their killstreak of ${Chat.secondaryColor}${
                            Killstreak.getKillstreak(
                                (e.entity as Player)
                            )
                        } kills<gray>!")
                    }
                    Killstreak.resetKillstreak((e.entity as Player))
                }
            }
        }
    }

    fun getPlayers(): List<Player> {
        val players = ArrayList<Player>()
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.world.name == "Arena" && !SpecFeature.instance.isSpec(player)) {
                players.add(player)
            }
        }
        return players
    }
    fun sendToPlayers(message: String) {
        for (player in Bukkit.getOnlinePlayers()) {
            if (player.world.name == "Arena") {
                Chat.sendMessage(player, message)
            }
        }
    }

    @EventHandler
    fun onAnimalSpawn(e: EntitySpawnEvent) {
        if (e.location.world.name != "Arena") {
            return
        }
        when (e.entityType) {
            EntityType.CHICKEN -> {
                e.isCancelled = true
            }
            EntityType.HORSE -> {
                e.isCancelled = true
            }
            EntityType.COW -> {
                e.isCancelled = true
            }
            EntityType.SHEEP -> {
                e.isCancelled = true
            }
            EntityType.OCELOT -> {
                e.isCancelled = true
            }
            EntityType.PIG -> {
                e.isCancelled = true
            }
            EntityType.WOLF -> {
                e.isCancelled = true
            }
            EntityType.MOOSHROOM -> {
                e.isCancelled = true
            }
            EntityType.CREEPER -> {
                e.isCancelled = true
            }
            EntityType.SKELETON -> {
                e.isCancelled = true
            }
            EntityType.ZOMBIE -> {
                e.isCancelled = true
            }
            EntityType.ENDERMAN -> {
                e.isCancelled = true
            }
            EntityType.RABBIT -> {
                e.isCancelled = true
            }
            EntityType.WITCH -> {
                e.isCancelled = true
            }
            EntityType.SPIDER -> {
                e.isCancelled = true
            }
            EntityType.SQUID -> {
                e.isCancelled = true
            }
            EntityType.SLIME -> {
                e.isCancelled = true
            }
            else -> {}
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.block.world.name != "Arena") return
        e.isCancelled = !(
            e.block.type.name.contains("LEAVES") ||
            e.block.type.name.contains("FLOWER") ||
            e.block.type.name.contains("MUSHROOM") ||
            e.block.type.name.contains("GRASS") ||
            e.block.type.name.contains("SAPLING") ||
            e.block.type.name.contains("BUSH") ||
            e.block.type.name.contains("FERN") ||
            e.block.type.name.contains("VINE")
        )
    }

    @EventHandler
    fun onPlayerDrop(e: PlayerDropItemEvent) {
        if (e.player.world.name != "Arena") return
        e.isCancelled = true
    }

    @EventHandler
    fun onBucketEmpty(e: PlayerBucketEmptyEvent) {
        if (e.player.world.name != "Arena") return
        if (e.bucket == Material.LAVA || e.bucket == Material.LAVA_BUCKET || e.bucket == Material.LAVA) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                e.blockClicked.getRelative(e.blockFace).type = Material.AIR
            }, 100L)
        }
        if (e.bucket == Material.WATER || e.bucket == Material.WATER_BUCKET || e.bucket == Material.WATER) {
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                e.blockClicked.getRelative(e.blockFace).type = Material.AIR
            }, 100L)
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (e.block.world.name != "Arena") return
        if (e.block.location.y > 100.0) {
            Chat.sendMessage(e.player, "$prefix You can't place blocks above <red>Y: 100<gray>.")
            e.isCancelled = true
        }
        when (e.block.type) {
            Material.valueOf(Kraftwerk.instance.profileHandler.getProfile(e.player.uniqueId)!!.arenaBlock!!) -> {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                    e.player.inventory.addItem(ItemStack(Material.valueOf(Kraftwerk.instance.profileHandler.getProfile(e.player.uniqueId)!!.arenaBlock!!)))
                }, 1L)
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                    BlockAnimation().blockCrackAnimation(e.player, e.block, 1)
                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                        BlockAnimation().blockCrackAnimation(e.player, e.block, 2)
                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                            BlockAnimation().blockCrackAnimation(e.player, e.block, 3)
                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                                BlockAnimation().blockCrackAnimation(e.player, e.block, 4)
                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                                    BlockAnimation().blockCrackAnimation(e.player, e.block, 5)
                                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                                        BlockAnimation().blockCrackAnimation(e.player, e.block, 6)
                                        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                                            BlockAnimation().blockCrackAnimation(e.player, e.block, 7)
                                            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                                                BlockAnimation().blockCrackAnimation(e.player, e.block, 8)
                                                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                                                    BlockAnimation().blockCrackAnimation(e.player, e.block, 9)
                                                    Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                                                        BlockAnimation().blockBreakAnimation(null, e.block)
                                                        e.block.type = Material.AIR
                                                    }, 20L)
                                                }, 20L)
                                            }, 20L)
                                        }, 20L)
                                    }, 20L)
                                }, 20L)
                            }, 20L)
                        }, 20L)
                    }, 20L)
                }, 20L)
            }
            Material.LAVA-> {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                    e.block.type = Material.AIR
                }, 100L)
            }
            Material.WATER -> {
                Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                    e.block.type = Material.AIR
                }, 100L)
            }
            else -> {}
        }
    }
}
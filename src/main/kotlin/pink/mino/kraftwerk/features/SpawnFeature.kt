package pink.mino.kraftwerk.features

//import com.gmail.filoghost.holographicdisplays.api.HologramsAPI
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import me.lucko.helper.Schedulers
import me.lucko.helper.promise.Promise
import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.npc.MemoryNPCDataStore
import net.citizensnpcs.api.npc.NPC
import org.bson.Document
import org.bukkit.*
import org.bukkit.block.Sign
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.*
import java.sql.Timestamp
import java.util.*


class SpawnFeature : Listener {
    val spawnLocation = Location(Bukkit.getWorld("Spawn"), -221.5, 95.0, -140.5)
    val editorList = ArrayList<UUID>()

    val prefix = "<dark_gray>[${Chat.primaryColor}Server<dark_gray>]<gray>"
    companion object {
        val instance = SpawnFeature()
    }

    class PlayerPurchase(
        val id: Int,
        val name: String,
        val uuid: String
    )

    class Purchase(
        val id: Number,
        val amount: Double,
        val email: String,
        val date: String,
        val gateway: Any,
        val status: String,
        val currency: Any,
        val player: PlayerPurchase,
        val packages: Any,
        val notes: Any,
        val creatorCode: Any
    )

    init {
        val registry = CitizensAPI.createAnonymousNPCRegistry(MemoryNPCDataStore())
        var highestNpc: NPC? = null
        var recentNpc: NPC? = null

//        if (ConfigFeature.instance.config!!.getString("thing.recent_purchase_holo.world") != null && ConfigFeature.instance.config!!.getString("buycraft.token") != "") {
//            val recent = HologramsAPI.createHologram(JavaPlugin.getPlugin(Kraftwerk::class.java), Location(
//                Bukkit.getWorld(ConfigFeature.instance.config!!.getString("thing.recent_purchase_holo.world")),
//                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.x"),
//                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.y"),
//                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.z"),
//                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.yaw").toFloat(),
//                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.pitch").toFloat()
//            )
//            )
//            val highestHolo = HologramsAPI.createHologram(JavaPlugin.getPlugin(Kraftwerk::class.java), Location(
//                Bukkit.getWorld(ConfigFeature.instance.config!!.getString("thing.highest_purchase_holo.world")),
//                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.x"),
//                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.y"),
//                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.z"),
//                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.yaw").toFloat(),
//                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.pitch").toFloat()
//            ))
//            if (ConfigFeature.instance.config!!.getBoolean("buycraft.scan")) {
//                Schedulers.sync().runRepeating(Runnable {
//                    Log.info("Updating Buycraft purchases...")
//                    highestNpc?.destroy()
//                    recentNpc?.destroy()
//
//                    with(URL("https://plugin.tebex.io/payments").openConnection() as HttpURLConnection) {
//                        requestMethod = "GET"
//                        setRequestProperty("User-Agent", "Mozilla/5.0")
//                        setRequestProperty(
//                            "X-Tebex-Secret",
//                            ConfigFeature.instance.config!!.getString("buycraft.token")
//                        )
//                        BufferedReader(InputStreamReader(inputStream)).use {
//                            val response = StringBuffer()
//                            var inputLine = it.readLine()
//                            while (inputLine != null) {
//                                response.append(inputLine)
//                                inputLine = it.readLine()
//                            }
//                            it.close()
//                            val type = object : TypeToken<List<Purchase>>() {}.type
//                            var purchases: List<Purchase> = Gson().fromJson(response.toString(), type)
//                            recent.clearLines()
//                            highestHolo.clearLines()
//                            recent.appendTextLine(Chat.colored("${Chat.primaryColor}&lRecent Purchase"))
//                            highestHolo.appendTextLine(Chat.colored("${Chat.primaryColor}&lHighest Purchase"))
//                            val loc1 = Location(
//                                Bukkit.getWorld(ConfigFeature.instance.config!!.getString("thing.recent_purchase_holo.world")),
//                                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.x"),
//                                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.y"),
//                                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.z"),
//                                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.yaw").toFloat(),
//                                ConfigFeature.instance.config!!.getDouble("thing.recent_purchase_holo.pitch").toFloat()
//                            )
//                            val loc2 = Location(
//                                Bukkit.getWorld(ConfigFeature.instance.config!!.getString("thing.highest_purchase_holo.world")),
//                                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.x"),
//                                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.y"),
//                                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.z"),
//                                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.yaw").toFloat(),
//                                ConfigFeature.instance.config!!.getDouble("thing.highest_purchase_holo.pitch").toFloat()
//                            )
//                            for ((index, purchase) in purchases.withIndex()) {
//                                if (index == 0) {
//                                    val npc = registry.createNPC(EntityType.PLAYER, purchase.player.name)
//                                    npc.setAlwaysUseNameHologram(true)
//                                    npc.name = purchase.player.name
//                                    npc.spawn(loc1)
//                                    recentNpc = npc
//                                    recent.appendTextLine(Chat.colored("<green>$${DecimalFormat("0.00").format(purchase.amount)}"))
//                                }
//                            }
//
//                            var highest: Purchase? = null
//                            for (purchase in purchases) {
//                                val amount = highest?.amount ?: 0.0
//                                if (purchase.amount > amount) highest = purchase
//                            }
//                            if (highest != null) {
//                                val npc = registry.createNPC(EntityType.PLAYER, highest.player.name)
//                                npc.setAlwaysUseNameHologram(true)
//                                npc.name = highest.player.name
//                                highestHolo.appendTextLine(Chat.colored("<green>$${DecimalFormat("0.00").format(highest.amount)}"))
//                                npc.spawn(loc2)
//                                highestNpc = npc
//                            }
//                        }
//                    }
//                }, 0L, 5 * 60 * 20)
//            }
//        }
    }

    fun sendEditor(p: Player) {
        editorList.add(p.uniqueId)
        p.teleport(Location(Bukkit.getWorld("Spawn"), -733.5,134.5, 254.0))
        p.inventory.clear()
        p.inventory.helmet = ItemStack(Material.AIR)
        p.inventory.chestplate = ItemStack(Material.AIR)
        p.inventory.leggings = ItemStack(Material.AIR)
        p.inventory.boots = ItemStack(Material.AIR)
        p.inventory.setItemInOffHand(ItemStack(Material.AIR))


        val sword = ItemBuilder(Material.DIAMOND_SWORD).name(Chat.colored("<green>Sword")).make()
        val fishingRod = ItemBuilder(Material.FISHING_ROD).name(Chat.colored("<green>Rod")).make()
        val bow = ItemBuilder(Material.BOW).name(Chat.colored("<green>Bow")).make()
        val cobblestone = ItemBuilder(Material.COBBLESTONE).name(Chat.colored("<green>Blocks")).make()
        val waterBucket = ItemBuilder(Material.WATER_BUCKET).name(Chat.colored("<green>Water")).make()
        val lavaBucket = ItemBuilder(Material.LAVA_BUCKET).name(Chat.colored("<green>Lava")).make()
        val goldenCarrot = ItemBuilder(Material.GOLDEN_CARROT).name(Chat.colored("<green>Food")).make()
        val goldenApples = ItemBuilder(Material.GOLDEN_APPLE).name(Chat.colored("<green>Gapples")).make()
        val gHeads = ItemBuilder(Material.GOLDEN_APPLE).name(Chat.colored("<green>Heads")).make()

        p.inventory.setItem(0, sword)
        p.inventory.setItem(1, fishingRod)
        p.inventory.setItem(2, bow)
        p.inventory.setItem(3, cobblestone)
        p.inventory.setItem(4, waterBucket)
        p.inventory.setItem(5, lavaBucket)
        p.inventory.setItem(6, goldenCarrot)
        p.inventory.setItem(7, goldenApples)
        p.inventory.setItem(8, gHeads)

        Chat.sendMessage(p, "${Chat.dash} Entered the arena kit editor, right click the signs in front of you for more actions.")
        Chat.sendMessage(p, "<red>Warning: Try not to move items outside of your hotbar, it will not be placed in your inventory when you enter the arena.")
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_BLOCK) return
        if (e.clickedBlock != null && e.clickedBlock!!.type.name.contains("SIGN")) {
            val sign = e.clickedBlock!!.state as Sign
            if (sign.getLine(1).toString() == "[Exit]") {
                exitEditor(e.player)
            }
            if (sign.getLine(1).toString() == "[Save Kit]") {
                Chat.sendMessage(e.player, "${prefix} Saving your kit...")
                saveKit(e.player)
            }
        }
    }

    fun saveKit(p: Player) {
        Schedulers.async().run {
            try {
                with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("kits")) {
                    val filter = Filters.eq("uuid", p.uniqueId)
                    val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId).get()

                    val slot1 = if (p.inventory.getItem(0) != null) ChatColor.stripColor(p.inventory.getItem(0)!!.itemMeta.displayName)!!.uppercase() else "NONE"
                    val slot2 = if (p.inventory.getItem(1) != null) ChatColor.stripColor(p.inventory.getItem(1)!!.itemMeta.displayName)!!.uppercase() else "NONE"
                    val slot3 = if (p.inventory.getItem(2) != null) ChatColor.stripColor(p.inventory.getItem(2)!!.itemMeta.displayName)!!.uppercase() else "NONE"
                    val slot4 = if (p.inventory.getItem(3) != null) ChatColor.stripColor(p.inventory.getItem(3)!!.itemMeta.displayName)!!.uppercase() else "NONE"
                    val slot5 = if (p.inventory.getItem(4) != null) ChatColor.stripColor(p.inventory.getItem(4)!!.itemMeta.displayName)!!.uppercase() else "NONE"
                    val slot6 = if (p.inventory.getItem(5) != null) ChatColor.stripColor(p.inventory.getItem(5)!!.itemMeta.displayName)!!.uppercase() else "NONE"
                    val slot7 = if (p.inventory.getItem(6) != null) ChatColor.stripColor(p.inventory.getItem(6)!!.itemMeta.displayName)!!.uppercase() else "NONE"
                    val slot8 = if (p.inventory.getItem(7) != null) ChatColor.stripColor(p.inventory.getItem(7)!!.itemMeta.displayName)!!.uppercase() else "NONE"
                    val slot9 = if (p.inventory.getItem(8) != null) ChatColor.stripColor(p.inventory.getItem(8)!!.itemMeta.displayName)!!.uppercase() else "NONE"

                    val document = Document("uuid", profile.uniqueId)
                        .append("name", profile.name)
                        .append("lastLogin", Timestamp(profile.timestamp))
                        .append("kit", Document("slot1", (slot1))
                            .append("slot2", (slot2))
                            .append("slot3", (slot3))
                            .append("slot4", (slot4))
                            .append("slot5", (slot5))
                            .append("slot6", (slot6))
                            .append("slot7", (slot7))
                            .append("slot8", (slot8))
                            .append("slot9", (slot9))
                        )
                    this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
                    Schedulers.sync().run {
                        exitEditor(p)
                    }
                    Chat.sendMessage(p, "${prefix} Successfully saved your kit.")
                    return@run
                }
            } catch (e: MongoException) {
                Chat.sendMessage(p, "${prefix} Failed to save your kit.")
                exitEditor(p)
                e.printStackTrace()
            }
            return@run
        }
    }

    fun exitEditor(p: Player) {
        send(p)
        Chat.sendMessage(p, "${prefix} Exited the arena kit editor.")
    }

    fun send(p: Player) {
        p.maxHealth = 20.0
        p.health = 20.0
        p.allowFlight = false
        p.isFlying = false
        Schedulers.sync().runLater(Runnable@ {
            if (p.world.name == Bukkit.getWorld(ConfigFeature.instance.config!!.getString("spawn.world")!!)!!.name) {
                if (GameState.currentState == GameState.LOBBY) {
                    if (PerkChecker.checkPerks(p).contains(Perk.SPAWN_FLY)) {
                        p.allowFlight = true
                        p.isFlying = true
                    }
                    Promise.start()
                        .thenApplyAsync {
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(p.uniqueId)
                        }
                        .thenAcceptSync {
                            val xp = (it.get().xp as Double?) ?: 0.0
                            val xpNeeded = (it.get().xpNeeded as Double?) ?: 0.0
                            val level = (it.get().level as Int?) ?: 1
                            p.level = level
                            p.exp = (xp / xpNeeded).toFloat()
                        }
                }
            }
        },  20L)
        p.foodLevel = 20
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
        p.gameMode = GameMode.ADVENTURE
        p.exp = 0F
        p.level = 0

        val stats = ItemBuilder(Material.WRITTEN_BOOK)
            .name("${Chat.primaryColor}View Stats <gray>(Right Click)")
            .addLore("<gray>Right-click to view your stats.")
            .make()
        p.inventory.setItem(5, stats)

        val config = ItemBuilder(Material.GOLDEN_APPLE)
            .name("${Chat.primaryColor}UHC Configuration <gray>(Right Click)")
            .addLore("<gray>Right-click to view the UHC Configuration.")
            .make()

        val scenarios = ItemBuilder(Material.CHEST)
            .name("${Chat.primaryColor}Active Scenarios <gray>(Right Click)")
            .addLore("<gray>Right-click to view active scenarios.")
            .make()
        p.inventory.setItem(8, scenarios)
        val championsKit = ItemBuilder(Material.NETHER_STAR)
            .name("${Chat.primaryColor}Champions Kit <gray>(Right Click)")
            .addLore("<gray>Right-click to view the Champions kit selector.")
            .make()
        val arenaSword = ItemBuilder(Material.IRON_SWORD)
            .name("${Chat.primaryColor}FFA Arena <gray>(Right Click)")
            .addLore("<gray>Right-click to join the FFA Arena.")
            .make()
        p.inventory.setItem(4, arenaSword)
        val editKit = ItemBuilder(Material.ENDER_CHEST)
            .name("${Chat.primaryColor}Edit Arena Kit <gray>(Right Click)")
            .addLore("<gray>Right-click to edit your kit for the FFA Arena.")
            .make()
        val profile = ItemBuilder(Material.PLAYER_HEAD)
            .toSkull()
            .name("${Chat.primaryColor}Your Profile <gray>(Right Click)")
            .addLore("<gray>Right-click to view your profile.")
            .setOwner(p.name)
            .make()
        val donator = ItemBuilder(Material.EMERALD)
            .name("&2&lDonator <gray>(Right Click)")
            .addLore("<gray>Right-click to view your donator perks.")
            .make()
        p.inventory.setItem(3, profile)
        p.inventory.setItem(1, donator)
        p.inventory.setItem(7, editKit)
        if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("auction"))) {
            p.inventory.clear()
        }
        if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("champions"))) {
            p.inventory.setItem(0, championsKit)
            p.inventory.setItem(1, config)
        } else {
            p.inventory.setItem(0, config)
        }
        var location = if (ConfigFeature.instance.config!!.getDouble("spawn.x") == null) {
            Location(Bukkit.getWorlds().random(), 0.5, 95.0, 0.5)
        } else {
            Location(
                Bukkit.getWorld(ConfigFeature.instance.config!!.getString("spawn.world")!!),
                ConfigFeature.instance.config!!.getDouble("spawn.x"),
                ConfigFeature.instance.config!!.getDouble("spawn.y"),
                ConfigFeature.instance.config!!.getDouble("spawn.z"),
                ConfigFeature.instance.config!!.getDouble("spawn.yaw").toFloat(),
                ConfigFeature.instance.config!!.getDouble("spawn.pitch").toFloat()
            )
        }
        if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("auction"))) {
            location = Location(Bukkit.getWorld(ConfigFeature.instance.config!!.getString("spawn.world")!!), -278.0, 96.5, 7.0)
        }
        editorList.remove(p.uniqueId)
        p.teleport(location)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (
            ConfigFeature.instance.config!!.getBoolean("void.enabled") &&
            event.player.location.blockY < ConfigFeature.instance.config!!.getDouble("void.level") &&
            event.player.world.name == ConfigFeature.instance.config!!.getString("spawn.world") &&
            GameState.currentState == GameState.LOBBY
        ) {
            send(event.player)
        }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        editorList.remove(e.player.uniqueId)
    }

    @EventHandler
    fun onBucketEmpty(e: PlayerBucketEmptyEvent) {
        if (e.player.world.name == "Spawn" && (Kraftwerk.instance.buildMode[e.player.uniqueId] == false || Kraftwerk.instance.buildMode[e.player.uniqueId] == null)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerConsume(e: PlayerItemConsumeEvent) {
        if (e.player.world.name == "Spawn" && (Kraftwerk.instance.buildMode[e.player.uniqueId] == false || Kraftwerk.instance.buildMode[e.player.uniqueId] == null)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onRightClick(e: PlayerInteractEvent) {
        if (e.player.world.name == "Spawn" && (Kraftwerk.instance.buildMode[e.player.uniqueId] == false || Kraftwerk.instance.buildMode[e.player.uniqueId] == null)) {
            if (e.item !== null) {
                when (e.item!!.itemMeta.displayName) {
                    Chat.colored("${Chat.primaryColor}View Stats <gray>(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "stats")
                    }
                    Chat.colored("&2&lDonator <gray>(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "donator")
                    }
                    Chat.colored("${Chat.primaryColor}UHC Configuration <gray>(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "uhc")
                    }
                    Chat.colored("${Chat.primaryColor}Active Scenarios <gray>(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "scen")
                    }
                    Chat.colored("${Chat.primaryColor}FFA Arena <gray>(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "a")
                    }
                    Chat.colored("${Chat.primaryColor}Donator Menu <gray>(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "donator")
                    }
                    Chat.colored("${Chat.primaryColor}Champions Kit <gray>(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "ckit")
                    }
                    Chat.colored("${Chat.primaryColor}Your Profile <gray>(Right Click)") -> {
                        e.isCancelled = true
                        Bukkit.dispatchCommand(e.player, "profile")
                    }
                    Chat.colored("${Chat.primaryColor}Edit Arena Kit <gray>(Right Click)") -> {
                        e.isCancelled = true
                        sendEditor(e.player)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.whoClicked.world.name == "Spawn" && (Kraftwerk.instance.buildMode[e.whoClicked.uniqueId] == false || Kraftwerk.instance.buildMode[e.whoClicked.uniqueId] == null)) {
            if (!editorList.contains(e.whoClicked.uniqueId)) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onItemDrop(e: PlayerDropItemEvent) {
        if (e.player.world.name == "Spawn" && (Kraftwerk.instance.buildMode[e.player.uniqueId] == false || Kraftwerk.instance.buildMode[e.player.uniqueId] == null)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (e.player.world.name == "Spawn" && (Kraftwerk.instance.buildMode[e.player.uniqueId] == false || Kraftwerk.instance.buildMode[e.player.uniqueId] == null)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(e: BlockPlaceEvent) {
        if (e.player.world.name == "Spawn" && (Kraftwerk.instance.buildMode[e.player.uniqueId] == false || Kraftwerk.instance.buildMode[e.player.uniqueId] == null)) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (e.entity.type == EntityType.PLAYER) {
            if (e.entity.world.name == "Spawn" && (Kraftwerk.instance.buildMode[e.entity.uniqueId] == false || Kraftwerk.instance.buildMode[e.entity.uniqueId] == null)) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerFoodChange(e: FoodLevelChangeEvent) {
        if (e.entity.type == EntityType.PLAYER) {
            if (e.entity.world.name == "Spawn" && (Kraftwerk.instance.buildMode[e.entity.uniqueId] == false || Kraftwerk.instance.buildMode[e.entity.uniqueId] == null)) {
                e.isCancelled = true
            }
        }
    }
}
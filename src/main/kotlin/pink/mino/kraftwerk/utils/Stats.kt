package pink.mino.kraftwerk.utils

import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import com.mongodb.client.model.Sorts.descending
import eu.decentsoftware.holograms.api.DHAPI
import eu.decentsoftware.holograms.api.holograms.Hologram
import me.lucko.helper.Events
import me.lucko.helper.Schedulers
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Cache
import me.lucko.helper.profiles.plugin.external.caffeine.cache.Caffeine
import me.lucko.helper.utils.Log
import org.bson.BsonBinary
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import java.util.*
import java.util.concurrent.TimeUnit

class StatsPlayer(val player: OfflinePlayer) : Listener {
    var diamondsMined = 0
    var ironMined = 0
    var goldMined = 0

    var gamesPlayed = 0
    var kills = 0
    var arenaKills = 0
    var arenaDeaths = 0
    var highestArenaKs = 0
    var wins = 0
    var deaths = 0

    var damageDealt = 0.0
    var damageTaken = 0.0

    var bowShots = 0
    var bowHits = 0
    var bowMisses = 0
    var meleeHits = 0

    var gapplesCrafted = 0
    var gapplesEaten = 0
    var timesCrafted = 0
    var timesEnchanted = 0
    var timesNether = 0

    var timeSpectated: Long = 0
    var thankYous: Int = 0
}

class Leaderboards : BukkitRunnable() {
    var timer = 1

    companion object {
        val storedHolograms = hashMapOf<String, Hologram>()
        val plugin = JavaPlugin.getPlugin(Kraftwerk::class.java)
        val gamesPlayed = DHAPI.createHologram("gamesPlayed", generateThingLocationFromConfig("games_played_holo"))
        val wins =
            DHAPI.createHologram("wins", generateThingLocationFromConfig("wins_holo"))
        val kills =
            DHAPI.createHologram("kills", generateThingLocationFromConfig("kills_holo"))
        val diamondsMined =
            DHAPI.createHologram("diamondsMined",  generateThingLocationFromConfig("diamonds_mined_holo"))
        val goldMined = DHAPI.createHologram("goldMined", generateThingLocationFromConfig("gold_mined_holo")
        )
        val gapplesEaten = DHAPI.createHologram("gapplesEaten", generateThingLocationFromConfig("gapples_eaten_holo")
        )
        val highestLevel = DHAPI.createHologram("highestLevel",
            generateThingLocationFromConfig("highest_level_holo")
        )
        val latestMatch = DHAPI.createHologram("latestMatch",
            generateThingLocationFromConfig("latest_match_holo")
        )

        fun generateThingLocationFromConfig(value: String): Location {
            if (ConfigFeature.instance.config!!.getDouble("thing.${value}.x") == null || ConfigFeature.instance.config!!.getDouble(
                    "thing.${value}.y"
                ) == null || ConfigFeature.instance.config!!.getDouble("thing.${value}.z") == null || ConfigFeature.instance.config!!.getString(
                    "thing.${value}.world"
                ) == null
            ) {
                return Location(Bukkit.getWorld("Spawn"), 0.0, 0.0, 0.0)
            } else {
                return Location(
                    Bukkit.getWorld(ConfigFeature.instance.config!!.getString("thing.${value}.world")!!),
                    ConfigFeature.instance.config!!.getDouble("thing.${value}.x"),
                    ConfigFeature.instance.config!!.getDouble("thing.${value}.y"),
                    ConfigFeature.instance.config!!.getDouble("thing.${value}.z")
                )
            }
        }

        fun updateLeaderboards() {
            try {
                // Clear all holograms
                DHAPI.setHologramLines(gamesPlayed, 0, listOf())
                DHAPI.setHologramLines(wins, 0, listOf())
                DHAPI.setHologramLines(kills, 0, listOf())
                DHAPI.setHologramLines(diamondsMined, 0, listOf())
                DHAPI.setHologramLines(goldMined, 0, listOf())
                DHAPI.setHologramLines(gapplesEaten, 0, listOf())
                DHAPI.setHologramLines(highestLevel, 0, listOf())
                DHAPI.setHologramLines(latestMatch, 0, listOf())

                // Headers
                DHAPI.addHologramLine(gamesPlayed, "${Chat.primaryColor}<bold>Games Played")
                DHAPI.addHologramLine(gamesPlayed, Chat.guiLine)
                DHAPI.addHologramLine(wins, "${Chat.primaryColor}<bold>Wins")
                DHAPI.addHologramLine(wins, Chat.guiLine)
                DHAPI.addHologramLine(kills, "${Chat.primaryColor}<bold>Kills")
                DHAPI.addHologramLine(kills, Chat.guiLine)
                DHAPI.addHologramLine(diamondsMined, "${Chat.primaryColor}<bold>Diamonds Mined")
                DHAPI.addHologramLine(diamondsMined, Chat.guiLine)
                DHAPI.addHologramLine(goldMined, "${Chat.primaryColor}<bold>Gold Mined")
                DHAPI.addHologramLine(goldMined, Chat.guiLine)
                DHAPI.addHologramLine(gapplesEaten, "${Chat.primaryColor}<bold>Gapples Eaten")
                DHAPI.addHologramLine(gapplesEaten, Chat.guiLine)
                DHAPI.addHologramLine(highestLevel, "${Chat.primaryColor}<bold>Highest Level")
                DHAPI.addHologramLine(highestLevel, Chat.guiLine)
                DHAPI.addHologramLine(latestMatch, "${Chat.primaryColor}<bold>Latest Match")
                DHAPI.addHologramLine(latestMatch, Chat.guiLine)

                with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("stats")) {
                    val gp = this.find().sort(descending("gamesPlayed")).limit(10)
                    for ((index, document) in gp.withIndex()) {
                        val profile =
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(BsonBinary(document["uuid"] as UUID))
                                .get()
                        if (document["gamesPlayed"] as Int != 0) DHAPI.addHologramLine(
                            gamesPlayed,
                            "<yellow>${index + 1}. ${Chat.secondaryColor}${profile.name} <dark_gray>- <aqua>${document["gamesPlayed"] as Int}"
                        )
                    }
                    if (DHAPI.getHologram("gamesPlayed")!!.size() == 2) DHAPI.addHologramLine(
                        gamesPlayed,
                        "<gray>No data yet x_x"
                    )

                    val w = this.find().sort(descending("wins")).limit(10)
                    for ((index, document) in w.withIndex()) {
                        val profile =
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(BsonBinary(document["uuid"] as UUID))
                                .get()
                        if (document["wins"] as Int != 0) DHAPI.addHologramLine(
                            wins,
                            "<yellow>${index + 1}. ${Chat.secondaryColor}${profile.name} <dark_gray>- <aqua>${document["wins"] as Int}"
                        )
                    }
                    if (DHAPI.getHologram("wins")!!.size() == 2) DHAPI.addHologramLine(wins, "<gray>No data yet x_x")

                    val k = this.find().sort(descending("kills")).limit(10)
                    for ((index, document) in k.withIndex()) {
                        val profile =
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(BsonBinary(document["uuid"] as UUID))
                                .get()
                        if (document["kills"] as Int != 0) DHAPI.addHologramLine(
                            kills,
                            "<yellow>${index + 1}. ${Chat.secondaryColor}${profile.name} <dark_gray>- <aqua>${document["kills"] as Int}"
                        )
                    }
                    if (DHAPI.getHologram("kills")!!.size() == 2) DHAPI.addHologramLine(kills, "<gray>No data yet x_x")

                    val d = this.find().sort(descending("diamondsMined")).limit(10)
                    for ((index, document) in d.withIndex()) {
                        val profile =
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(BsonBinary(document["uuid"] as UUID))
                                .get()
                        if (document["diamondsMined"] as Int != 0) DHAPI.addHologramLine(
                            diamondsMined,
                            "<yellow>${index + 1}. ${Chat.secondaryColor}${profile.name} <dark_gray>- <aqua>${document["diamondsMined"] as Int}"
                        )
                    }
                    if (DHAPI.getHologram("diamondsMined")!!.size() == 2) DHAPI.addHologramLine(
                        diamondsMined,
                        "<gray>No data yet x_x"
                    )

                    val g = this.find().sort(descending("gapplesEaten")).limit(10)
                    for ((index, document) in g.withIndex()) {
                        val profile =
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(BsonBinary(document["uuid"] as UUID))
                                .get()
                        if (document["gapplesEaten"] as Int != 0) DHAPI.addHologramLine(
                            gapplesEaten,
                            "<yellow>${index + 1}. ${Chat.secondaryColor}${profile.name} <dark_gray>- <aqua>${document["gapplesEaten"] as Int}"
                        )
                    }
                    if (DHAPI.getHologram("gapplesEaten")!!.size() == 2) DHAPI.addHologramLine(
                        gapplesEaten,
                        "<gray>No data yet x_x"
                    )

                    val gm = this.find().sort(descending("goldMined")).limit(10)
                    for ((index, document) in gm.withIndex()) {
                        val profile =
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(BsonBinary(document["uuid"] as UUID))
                                .get()
                        if (document["goldMined"] as Int != 0) DHAPI.addHologramLine(
                            goldMined,
                            "<yellow>${index + 1}. ${Chat.secondaryColor}${profile.name} <dark_gray>- <aqua>${document["goldMined"] as Int}"
                        )
                    }
                    if (DHAPI.getHologram("goldMined")!!.size() == 2) DHAPI.addHologramLine(
                        goldMined,
                        "<gray>No data yet x_x"
                    )
                }

                with(JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("players")) {
                    val t = this.find().sort(descending("level")).limit(10)
                    for ((index, document) in t.withIndex()) {
                        val profile =
                            JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.lookupProfile(BsonBinary(document["uuid"] as UUID))
                                .get()
                        if (document["level"] as Int != 0) DHAPI.addHologramLine(
                            highestLevel,
                            "<yellow>${index + 1}. ${Chat.secondaryColor}${profile.name} <dark_gray>- <green>Level ${profile.level}"
                        )
                    }
                    if (DHAPI.getHologram("highestLevel")!!.size() == 2) DHAPI.addHologramLine(
                        highestLevel,
                        "<gray>No data yet x_x"
                    )
                }

                // Footers
                DHAPI.addHologramLine(gamesPlayed, Chat.guiLine)
                DHAPI.addHologramLine(wins, Chat.guiLine)
                DHAPI.addHologramLine(kills, Chat.guiLine)
                DHAPI.addHologramLine(diamondsMined, Chat.guiLine)
                DHAPI.addHologramLine(goldMined, Chat.guiLine)
                DHAPI.addHologramLine(gapplesEaten, Chat.guiLine)
                DHAPI.addHologramLine(highestLevel, Chat.guiLine)

            } catch (e: MongoException) {
                e.printStackTrace()
            }

            val matchesCollection: MongoCollection<Document> = JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("matches")
            with(matchesCollection) {
                val matches = this.find().sort(descending("startTime")).limit(1)
                for (match in matches) {
                    DHAPI.addHologramLine(latestMatch, "<yellow>${match.getString("title")}")
                    DHAPI.addHologramLine(latestMatch, " ")
                    DHAPI.addHologramLine(
                        latestMatch,
                        "<gray>Teamsize ${Chat.dash} ${Chat.secondaryColor}${match.getString("teams")}"
                    )
                    DHAPI.addHologramLine(
                        latestMatch,
                        "<gray>Scenarios (${(match["scenarios"] as List<*>).size}) ${Chat.dash}"
                    )
                    for (scenario in match["scenarios"] as List<*>) {
                        DHAPI.addHologramLine(latestMatch, " <dark_gray>●<gray> <white>${scenario}<dark_gray>")
                    }
                    DHAPI.addHologramLine(latestMatch, " ")
                    var kills = 0
                    val killsMap = match["winnerKills"] as Map<String, Int>
                    for (kill in (match["winnerKills"] as Map<*, *>).values) {
                        kills += kill as Int
                    }
                    DHAPI.addHologramLine(
                        latestMatch,
                        "<gray>Winner(s) ${Chat.dash} <dark_gray>(<gray>Kills: <yellow>${kills}<dark_gray>)"
                    )
                    val winnerMap = mutableMapOf<String, Int>()
                    for (winner in match["winners"] as List<*>) {
                        try {
                            plugin.profileHandler.lookupProfile(BsonBinary(UUID.fromString(winner as String)))
                                .thenApplySync { profile ->
                                    winnerMap[profile.name!!] = killsMap[winner] as Int
                                }
                        } catch (_: Error) {
                        }
                    }
                    var winnerLimited = 10
                    for (entry in winnerMap) {
                        if (winnerLimited == 0) {
                            DHAPI.addHologramLine(latestMatch, "<gray><italic>(${winnerMap.size - 10} more winners<white><italic>)")
                            break
                        }
                        DHAPI.addHologramLine(
                            latestMatch,
                            " ${Chat.dot} ${Chat.secondaryColor}${entry.key} <dark_gray>(<yellow>${entry.value}<dark_gray>)"
                        )
                        winnerLimited--
                    }
                }
                DHAPI.addHologramLine(latestMatch, Chat.guiLine)
                return@with null
            }
        }
    }

    override fun run() {
        timer -= 1
        if (timer == 0) {
            updateLeaderboards()
            timer = 300
        }
    }
}
class StatsHandler : Listener {
    private val statsPlayerMap: Cache<UUID, StatsPlayer> = Caffeine.newBuilder()
        .maximumSize(10_000)
        .expireAfterAccess(6, TimeUnit.HOURS)
        .build()
    init {
        Events.subscribe(PlayerLoginEvent::class.java, EventPriority.MONITOR)
            .filter { it.result == PlayerLoginEvent.Result.ALLOWED }
            .handler { event ->
                val statsPlayer = StatsPlayer(event.player)
                updateCache(statsPlayer)
                Schedulers.async().run { loadPlayerData(statsPlayer.player.uniqueId, statsPlayer) }
            }
        Events.subscribe(PlayerQuitEvent::class.java, EventPriority.MONITOR)
            .handler { event ->
                val statsPlayer = statsPlayerMap.getIfPresent(event.player.uniqueId)
                if (statsPlayer != null) {
                    Schedulers.async().run { savePlayerData(statsPlayer) }
                }
            }
    }

    fun savePlayerData(statsPlayer: StatsPlayer) {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("stats")) {
                val filter = Filters.eq("uuid", BsonBinary(statsPlayer.player.uniqueId))
                val document = Document("uuid", BsonBinary(statsPlayer.player.uniqueId))
                    .append("diamondsMined", statsPlayer.diamondsMined)
                    .append("ironMined", statsPlayer.ironMined)
                    .append("goldMined", statsPlayer.goldMined)

                    .append("gamesPlayed", statsPlayer.gamesPlayed)
                    .append("kills", statsPlayer.kills)
                    .append("wins", statsPlayer.wins)
                    .append("deaths", statsPlayer.deaths)

                    .append("arenaDeaths", statsPlayer.arenaDeaths)
                    .append("arenaKills", statsPlayer.arenaKills)
                    .append("highestArenaKs", statsPlayer.highestArenaKs)

                    .append("damageDealt", statsPlayer.damageDealt)
                    .append("damageTaken", statsPlayer.damageTaken)

                    .append("bowShots", statsPlayer.bowShots)
                    .append("bowHits", statsPlayer.bowHits)
                    .append("meleeHits", statsPlayer.meleeHits)
                    .append("bowMisses", statsPlayer.bowMisses)

                    .append("gapplesCrafted", statsPlayer.gapplesCrafted)
                    .append("gapplesEaten", statsPlayer.gapplesEaten)
                    .append("timesCrafted", statsPlayer.timesCrafted)
                    .append("timesEnchanted", statsPlayer.timesEnchanted)
                    .append("timesNether", statsPlayer.timesNether)
                    .append("timeSpectated", statsPlayer.timeSpectated)
                    .append("thankYous", statsPlayer.thankYous)
                this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
                Log.info("Saved stats for ${statsPlayer.player.name}")
            }
        } catch (e: MongoException) {
            Log.severe("Error saving player data for ${statsPlayer.player.name}", e)
        }
    }

    fun getStatsPlayer(player: Player): StatsPlayer? {
        Objects.requireNonNull(player, "player")
        return statsPlayerMap.getIfPresent(player.uniqueId)
    }

    fun lookupStatsPlayer(player: OfflinePlayer): StatsPlayer {
        Objects.requireNonNull(player, "player")
        if (statsPlayerMap.getIfPresent(player.uniqueId) != null) {
            return statsPlayerMap.getIfPresent(player.uniqueId)!!
        } else {
            val sPlayer = StatsPlayer(player)
            loadPlayerData(player.uniqueId, sPlayer)
            updateCache(sPlayer)
            return sPlayer
        }
    }

    fun loadPlayerData(uuid: UUID, statsPlayer: StatsPlayer) {
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("stats")) {
                val playerData = find(Filters.eq("uuid", BsonBinary(uuid))).first()
                if (playerData != null) {
                    statsPlayer.diamondsMined = playerData.getInteger("diamondsMined") ?: 0
                    statsPlayer.ironMined = playerData.getInteger("ironMined") ?: 0
                    statsPlayer.goldMined = playerData.getInteger("goldMined") ?: 0
                    statsPlayer.deaths = playerData.getInteger("deaths") ?: 0
                    statsPlayer.kills = playerData.getInteger("kills") ?: 0
                    statsPlayer.gamesPlayed = playerData.getInteger("gamesPlayed") ?: 0
                    statsPlayer.arenaKills = playerData.getInteger("arenaKills") ?: 0
                    statsPlayer.arenaDeaths = playerData.getInteger("arenaDeaths") ?: 0
                    statsPlayer.damageDealt = playerData.getDouble("damageDealt") ?: 0.0
                    statsPlayer.damageTaken = playerData.getDouble("damageTaken") ?: 0.0
                    statsPlayer.bowShots = playerData.getInteger("bowShots") ?: 0
                    statsPlayer.bowMisses = playerData.getInteger("bowMisses") ?: 0
                    statsPlayer.bowHits = playerData.getInteger("bowHits") ?: 0
                    statsPlayer.meleeHits = playerData.getInteger("meleeHits") ?: 0
                    statsPlayer.wins = playerData.getInteger("wins") ?: 0
                    statsPlayer.gapplesCrafted = playerData.getInteger("gapplesCrafted") ?: 0
                    statsPlayer.gapplesEaten = playerData.getInteger("gapplesEaten") ?: 0
                    statsPlayer.timesCrafted = playerData.getInteger("timesCrafted") ?: 0
                    statsPlayer.timesEnchanted = playerData.getInteger("timesEnchanted") ?: 0
                    statsPlayer.timesNether = playerData.getInteger("timesNether") ?: 0
                    statsPlayer.timeSpectated = playerData.getLong("timeSpectated") ?: 0L
                    statsPlayer.thankYous = playerData.getInteger("thankYous") ?: 0
                    Log.info("Loaded stats for ${statsPlayer.player.name}.")
                } else {
                    Log.info("Could not load stats for ${statsPlayer.player.name}.")
                }
            }
        } catch (e: MongoException) {
            e.printStackTrace()
        }
    }

    private fun updateCache(statsPlayer: StatsPlayer) {
        this.statsPlayerMap.put(statsPlayer.player.uniqueId, statsPlayer)
    }
}
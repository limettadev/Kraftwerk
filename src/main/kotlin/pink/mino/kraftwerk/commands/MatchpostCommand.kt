package pink.mino.kraftwerk.commands

import com.google.gson.Gson
import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.ActionBar
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.MiscUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.floor

class ScheduleBroadcast(private val opening: String) : BukkitRunnable() {
    fun getTime(): String {
        with(URL("https://hosts.uhc.gg/api/sync").openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0")
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                return "${response.toString()[12]}${response.toString()[13]}:${response.toString()[15]}${response.toString()[16]}"
            }
        }
    }

    private fun removeFifteenMinutes(time: String): String {
        val makeSureIsValid: List<String> = time.split(":")
        var hours: Any = makeSureIsValid[0].toInt()
        var minutes: Any
        if (makeSureIsValid[1] == "15") {
            minutes = 0
        } else if (makeSureIsValid[1] == "30") {
            minutes = 15
        } else if (makeSureIsValid[1] == "45") {
            minutes = 30
        } else {
            minutes = 45
            hours = if (makeSureIsValid[0].toInt() == 0) {
                23
            } else {
                makeSureIsValid[0].toInt() - 1
            }
        }
        if (hours.toString().length == 1) {
            hours = "0${hours}"
        }
        if (minutes.toString().length == 1) {
            minutes = "00"
        }
        return "${hours}:${minutes}"
    }

    override fun run() {
        print("Checking if the time corresponds with the broadcast time... ${removeFifteenMinutes(opening)} & ${getTime()}")
        if (ConfigFeature.instance.data!!.getString("matchpost.opens") == null) {
            cancel()
        }
        if (ConfigFeature.instance.data!!.getBoolean("matchpost.cancelled") == true) {
            cancel()
        }
        if (getTime() == removeFifteenMinutes(opening)) {
            cancel()
            val host = Bukkit.getOfflinePlayer(ConfigFeature.instance.data!!.getString("game.host"))
            var embed = EmbedBuilder()
            embed.setColor(MiscUtils.hexToColor(ConfigFeature.instance.config!!.getString("discord.embed-color")))
            embed.setTitle(ConfigFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            val scenarios = ConfigFeature.instance.data!!.getStringList("matchpost.scenarios")
            val fr = (System.currentTimeMillis() / 1000L) + (900000L) / 1000L
            embed.addField("Teams", ConfigFeature.instance.data!!.getString("matchpost.team"), false)
            embed.addField("Scenarios", scenarios.joinToString(", "), false)
            var flag = ":flag_${ConfigFeature.instance.config!!.getString("discord.flag")}:"
            embed.addField("IP", "$flag `${if (ConfigFeature.instance.config!!.getString("chat.serverIp") != null) ConfigFeature.instance.config!!.getString("chat.serverIp") else "no server ip setup in config tough tits"}` (1.8.x)", false)
            embed.addField("Opening", "<t:${fr}:t> (<t:${fr}:R>)", false)
            embed.addField("Matchpost", "[uhc.gg](https://hosts.uhc.gg/m/${ConfigFeature.instance.data!!.getInt("matchpost.id")})", false)
            if (Kraftwerk.instance.gameAlertsChannelId != null) {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Matchpost posted on discord!"))
                if (!ConfigFeature.instance.data!!.getBoolean("matchpost.fake")) {
                    if (Kraftwerk.instance.alertsRoleId != null) {
                        Discord.instance!!
                            .getTextChannelById(Kraftwerk.instance.gameAlertsChannelId!!)!!
                            .sendMessage("<@&${Kraftwerk.instance.alertsRoleId!!}>")
                            .queue { message ->
                                message.crosspost().queue()
                            }
                    } else {
                        Discord.instance!!
                            .getTextChannelById(Kraftwerk.instance.gameAlertsChannelId!!)!!
                            .sendMessage("@everyone")
                            .queue { message ->
                                message.crosspost().queue()
                            }
                    }
                }
                Discord.instance!!
                    .getTextChannelById(Kraftwerk.instance.gameAlertsChannelId!!)!!
                    .sendMessageEmbeds(embed.build())
                    .setActionRow(
                        Button.primary("toggle_alerts", "Click here to toggle alerts.")
                    )
                    .queue { message ->
                        message.crosspost().queue()
                    }

                val guild = Discord.instance!!.getGuildById(ConfigFeature.instance.config!!.getString("discord.guildId")!!) ?: return

                val teamName = ConfigFeature.instance.data!!.getString("matchpost.team") ?: "Unknown Mode"
                val scenarios = scenarios.joinToString(", ")
                val serverIp = ConfigFeature.instance.config!!.getString("chat.serverIp") ?: "example.net"

                val startTime = OffsetDateTime.now().plusMinutes(15)
                val endTime = startTime.plusHours(1)

                guild.createScheduledEvent(
                    "$teamName UHC",
                    serverIp,
                    startTime,
                    endTime
                )
                    .setDescription("Scenarios: $scenarios")
                    .queue()

            } else {
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} A matchpost is coming in ${Chat.secondaryColor}15 minutes&7 but there's no configured game alerts channel!"))
            }
            if (ConfigFeature.instance.config!!.getBoolean("options.whitelist-after-restart")) {
                embed = EmbedBuilder()
                embed.setColor(MiscUtils.hexToColor(ConfigFeature.instance.config!!.getString("discord.embed-color")))
                embed.setTitle(ConfigFeature.instance.data!!.getString("matchpost.host"))
                embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
                embed.addField("Pre-whitelists are on!", "You are now allowed to use the command `/wl` to request to pre-whitelist yourself in the server!", false)
                if (Kraftwerk.instance.preWhitelistChannelId != null) {
                    Discord.instance!!.getTextChannelById(Kraftwerk.instance.preWhitelistChannelId!!)!!.sendMessageEmbeds(embed.build()).queue()
                }
            }
            ConfigFeature.instance.data!!.set("whitelist.requests", true)
            ConfigFeature.instance.data!!.set("matchpost.posted", true)
            ConfigFeature.instance.saveData()
        }
    }
}

class Opening(var closing: Long) : BukkitRunnable() {
    var timer = 0

    private fun timeToString(ticks: Long): String {
        var t = ticks
        val hours = floor(t / 3600.toDouble()).toInt()
        t -= hours * 3600
        val minutes = floor(t / 60.toDouble()).toInt()
        t -= minutes * 60
        val seconds = t.toInt()
        val output = StringBuilder()
        if (hours > 0) {
            output.append(hours).append('h')
            if (minutes == 0) {
                output.append(minutes).append('m')
            }
        }
        if (minutes > 0) {
            output.append(minutes).append('m')
        }
        output.append(seconds).append('s')
        return output.toString()
    }

    private fun displayTimer(player: Player) {
        ActionBar.sendActionBarMessage(player, "${Chat.primaryColor}Whitelist is enabled in ${Chat.dash} ${Chat.secondaryColor}${timeToString(closing - timer.toLong())}")
    }

    override fun run() {
        if (timer == closing.toInt()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl on")
            cancel()
        }
        timer++
        for (player in Bukkit.getOnlinePlayers()) {
            displayTimer(player)
        }
    }
}

class ScheduleOpening(private val opening: String) : BukkitRunnable() {
    fun getTime(): String {
        with(URL("https://hosts.uhc.gg/api/sync").openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0")
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                return "${response.toString()[12]}${response.toString()[13]}:${response.toString()[15]}${response.toString()[16]}"
            }
        }
    }

    override fun run() {
        print("Checking if the time corresponds with the opening...")
        if (ConfigFeature.instance.data!!.getString("matchpost.opens") == null) {
            cancel()
        }
        if (ConfigFeature.instance.data!!.getBoolean("matchpost.cancelled") == true) {
            cancel()
        }
        if (getTime() == opening) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl off")
            val time: Long
            if (ConfigFeature.instance.data!!.getBoolean("matchpost.teamsGame")) {
                time = (ConfigFeature.instance.config!!.getLong("options.whitelist-timer-teams") * 60)
            } else {
                time = (ConfigFeature.instance.config!!.getLong("options.whitelist-timer-ffa") * 60)
            }
            val host = Bukkit.getOfflinePlayer(ConfigFeature.instance.data!!.getString("game.host"))
            val embed = EmbedBuilder()
            embed.addField("Matchpost", "https://hosts.uhc.gg/m/${ConfigFeature.instance.data!!.getInt("matchpost.id")}", false)
            embed.setColor(MiscUtils.hexToColor(ConfigFeature.instance.config!!.getString("discord.embed-color")))
            embed.setTitle(ConfigFeature.instance.data!!.getString("matchpost.host"))
            embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
            embed.addField("Game Open!", "The game is now open at `${if (ConfigFeature.instance.config!!.getString("chat.serverIp") != null) ConfigFeature.instance.config!!.getString("chat.serverIp") else "no server ip setup in config tough tits"}`.", false)
            if (Kraftwerk.instance.gameAlertsChannelId != null) {
                Discord.instance!!
                    .getTextChannelById(Kraftwerk.instance.gameAlertsChannelId!!)!!
                    .sendMessageEmbeds(embed.build())
                    .queue {
                        message -> message.crosspost().queue()
                    }

            } else {
                Chat.broadcast("${Chat.dash} Couldn't post game opening in discord because there's no game alerts channel ID configured.")
            }
            Bukkit.broadcastMessage(Chat.colored("${Chat.dash} The whitelist has been turned off automatically @ ${Chat.primaryColor}${opening}&7."))
            cancel()
            Kraftwerk.instance.opening = Opening(time)
            Kraftwerk.instance.opening!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)

            ConfigFeature.instance.data!!.set("whitelist.requests", false)
            ConfigFeature.instance.data!!.set("matchpost.opens", null)
            ConfigFeature.instance.saveData()
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("opened_matches")) {
                val filter = Filters.eq("id", ConfigFeature.instance.data!!.getInt("matchpost.id"))
                val document = Document("id", ConfigFeature.instance.data!!.getInt("matchpost.id"))
                    .append("server", ConfigFeature.instance.data!!.getString("matchpost.server"))
                    .append("title", ConfigFeature.instance.data!!.getString("matchpost.host"))
                    .append("teams", ConfigFeature.instance.data!!.getString("matchpost.team"))
                    .append("scenarios", ConfigFeature.instance.data!!.get("matchpost.scenarios") as List<*>)
                    .append("whitelist", ConfigFeature.instance.data!!.getBoolean("whitelist.enabled"))
                    .append("pvp", false)
                    .append("needsDelete", false)

                this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
            }
        }
    }
}

class MatchpostCommand : CommandExecutor {
    fun getFakeTime(): String {
        with(URL("https://hosts.uhc.gg/api/sync").openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0")
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                return "${response.toString()[12]}${response.toString()[13]}:${response.toString()[15]}${(response.toString()[16].toInt() + 16)}"
            }
        }
    }

    override fun onCommand(
        sender: CommandSender,
        command: Command?,
        label: String?,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.matchpost")) {
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        if (args.isEmpty()) {
            if (sender.hasPermission("uhc.staff.matchpost")) {
                Chat.sendMessage(sender, "&cYou must provide a valid matchpost ID.")
                return false
            } else {
                Chat.sendMessage(sender, "${Chat.prefix} Matchpost: &chttps://hosts.uhc.gg/m/${ConfigFeature.instance.data!!.getInt("matchpost.id")}")
                return false
            }
        }
        if (args[0] == "fake") {
            Chat.sendMessage(sender, "${Chat.prefix} Okay, making a fake matchpost, this will post in a minute and won't ping anyone on Discord.")

            Kraftwerk.instance.scheduledOpening = ScheduleOpening(getFakeTime())
            Kraftwerk.instance.scheduledOpening!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, (5 * 20).toLong())
            Kraftwerk.instance.scheduledBroadcast = ScheduleBroadcast(getFakeTime())
            Kraftwerk.instance.scheduledBroadcast!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 300L)

            ConfigFeature.instance.data!!.set("matchpost.team", "Chosen To2")
            ConfigFeature.instance.data!!.set("matchpost.teamsGame", true)
            ConfigFeature.instance.data!!.set("matchpost.host", "minota")
            ConfigFeature.instance.data!!.set("matchpost.id", "000000".toInt())
            ConfigFeature.instance.data!!.set("matchpost.scenarios", listOf("CutClean", "HasteyBoys", "Timber"))
            ConfigFeature.instance.data!!.set("matchpost.opens", getFakeTime())
            ConfigFeature.instance.data!!.set("matchpost.server", "uhc")
            ConfigFeature.instance.data!!.set("matchpost.fake", true)
            ConfigFeature.instance.saveData()
            return false
        }

        if (args[0].toIntOrNull() == null) {
            Chat.sendMessage(sender, "&cYou must provide a &ovalid&c matchpost ID.")
            return false
        }
        val host: String
        val id: Double
        val opening: String
        val scenarios: Any
        var team: String? = null
        var teamsGame: Boolean = false
        val scenarioList = ArrayList<String>()
        var server: String? = null
        with(URL("https://hosts.uhc.gg/api/matches/${args[0]}").openConnection() as HttpURLConnection) {
            requestMethod = "GET"
            setRequestProperty("User-Agent", "Mozilla/5.0")

            if (responseCode == 404) {
                Chat.sendMessage(sender, "&cInvalid match.")
                return false
            }
            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()

                var inputLine = it.readLine()
                while (inputLine != null) {
                    response.append(inputLine)
                    inputLine = it.readLine()
                }
                it.close()
                var map: Map<String, Any> = HashMap()
                map = Gson().fromJson(response.toString(), map.javaClass)
                host = if (map["hostingName"] != null) {
                    "${map["hostingName"]}'s #${(map["count"] as Double).toInt()}"
                } else {
                    "${map["author"]}'s #${(map["count"] as Double).toInt()}"
                }
                if (map["teams"] as String == "ffa") {
                    teamsGame = false
                    team = "FFA"
                } else if (map["teams"] as String == "chosen") {
                    teamsGame = true
                    team = "Chosen To${(map["size"] as Double).toInt()}"
                } else if (map["teams"] as String == "rvb") {
                    teamsGame = true
                    team = "Red vs. Blue"
                } else if (map["teams"] as String == "random") {
                    teamsGame = true
                    team = "Random To${(map["size"] as Double).toInt()}"
                } else if (map["teams"] as String == "market") {
                    teamsGame = true
                    team = "Auctions"
                }
                id = map["id"] as Double
                scenarios = map["scenarios"] as List<*>
                opening = "${(map["opens"] as String)[11]}${(map["opens"] as String)[12]}:${(map["opens"] as String)[14]}${(map["opens"] as String)[15]}"
                server = if ((map["address"] as String) == "na2.applejuice.games") {
                    "uhc2"
                } else if ((map["address"] as String) == "na1.applejuice.games") {
                    "uhc1"
                } else {
                    "other"
                }
                try {
                    with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("upcoming_matches")) {
                        val filter = Filters.eq("id", id)
                        val document = Document("id", id)
                        document.append("host", host)
                        document.append("hostingName", if (map["hostingName"] != null) {
                            map["hostingName"] as String
                        } else {
                            map["author"] as String
                        })
                        document.append("teams", team)
                        document.append("scenarios", scenarios)
                        document.append("friendlyOpening", opening)
                        document.append("opening", Date(OffsetDateTime.parse(map["opens"] as String).toInstant().toEpochMilli()))
                        document.append("border", (map["mapSize"] as Double).toInt())
                        this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
                        Chat.sendMessage(sender, "${Chat.dash} Successfully submitted your matchpost to the upcoming matchpost database.")
                    }
                } catch (e: MongoException) {
                    e.printStackTrace()
                    Chat.sendMessage(sender, "${Chat.prefix} An error occurred while submitting your matchpost to the upcoming matchpost database.")
                }
                for (scenario in ScenarioHandler.getActiveScenarios()){
                    scenario.toggle()
                }
                for (scenario in scenarios) {
                    scenarioList.add((scenario as String).lowercase().replace(" ", ""))
                    try { ScenarioHandler.getScenario(scenario.lowercase().replace(" ", ""))!!.toggle()
                    } catch (_: Exception) {}
                }
            }
        }
        ConfigFeature.instance.data!!.set("matchpost.team", team)
        ConfigFeature.instance.data!!.set("matchpost.teamsGame", teamsGame)
        ConfigFeature.instance.data!!.set("matchpost.host", host)
        ConfigFeature.instance.data!!.set("matchpost.id", id.toInt())
        ConfigFeature.instance.data!!.set("matchpost.scenarioIds", scenarioList)
        ConfigFeature.instance.data!!.set("matchpost.scenarios", scenarios)
        ConfigFeature.instance.data!!.set("matchpost.opens", opening)
        ConfigFeature.instance.data!!.set("matchpost.server", server)
        if (Kraftwerk.instance.scheduledOpening != null) {
            Kraftwerk.instance.scheduledOpening!!.cancel()
        }
        if (Kraftwerk.instance.scheduledBroadcast != null) {
            Kraftwerk.instance.scheduledBroadcast!!.cancel()
            Chat.sendMessage(sender, "${Chat.prefix} Cancelled ongoing scheduled opening/broadcast.")
        }
        Kraftwerk.instance.scheduledOpening = ScheduleOpening(opening)
        Kraftwerk.instance.scheduledOpening!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, (5 * 20).toLong())
        Kraftwerk.instance.scheduledBroadcast = ScheduleBroadcast(opening)
        Kraftwerk.instance.scheduledBroadcast!!.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 300L)
        Chat.sendMessage(sender, "${Chat.prefix} Set the matchpost to ${Chat.secondaryColor}https://hosts.uhc.gg/m/${id.toInt()}")
        Chat.sendMessage(sender, "${Chat.prefix} The server will now begin to check when the matchpost opens.")
        ConfigFeature.instance.saveData()
        return true
    }

}
package pink.mino.kraftwerk.commands

import com.mongodb.MongoException
import com.mongodb.client.model.Filters
import com.mongodb.client.model.FindOneAndReplaceOptions
import me.lucko.helper.utils.Log
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.utils.FileUpload
import net.kyori.adventure.title.Title
import org.bson.Document
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.*
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.Leaderboards
import pink.mino.kraftwerk.utils.MiscUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class EndGameCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
        }
        GameState.currentState = GameState.LOBBY
        val winners = ConfigFeature.instance.data!!.getStringList("game.winners")
        if (winners.isEmpty()) {
            Chat.sendMessage(sender, "<red>You have no winners set! You need to set them using /winner <player>!")
            return false
        }
        val winnersList = arrayListOf<String>()
        val kills = hashMapOf<String, Int>()
        val host = Bukkit.getOfflinePlayer(ConfigFeature.instance.data!!.getString("game.host")!!)

        for (player in Bukkit.getOnlinePlayers()) {
            if (winners.contains(player.name)) {
                player.showTitle(Title.title(Chat.colored("&6&lVICTORY!"), Chat.colored("<gray>Congratulations, you won the game!")))
            } else {
                player.showTitle(Title.title(Chat.colored("${Chat.primaryColor}&lGAME OVER!"), Chat.colored("<gray>The game has concluded!")))
            }
        }

        for (winner in winners) {
            val player = Bukkit.getOfflinePlayer(winner)
            if (player == null) {
                Chat.sendMessage(sender, "<red>Invalid player '${winner}', not adding to winner's list.")
            } else {
                winnersList.add(player.uniqueId.toString())
                kills[player.uniqueId.toString()] = ConfigFeature.instance.data!!.getInt("game.kills." + player.name)
                if (!ConfigOptionHandler.getOption("statless")!!.enabled) XpFeature().add(player, 50.0)
                if (!ConfigOptionHandler.getOption("statless")!!.enabled) {
                    val pr = JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.lookupStatsPlayer(player)
                    pr.wins++
                    Kraftwerk.instance.statsHandler.savePlayerData(pr)
                }
            }
        }

        JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.winners = winnersList
        val gameTitle = ConfigFeature.instance.data!!.getString("matchpost.host")
        for (team in TeamsFeature.manager.getTeams()) {
            for (player in team.players) {
                team.removePlayer(player)
            }
        }
        val game = JavaPlugin.getPlugin(Kraftwerk::class.java).game!!
        game.endTime = Date().time
        if (!ConfigOptionHandler.getOption("statless")!!.enabled) {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("matches")) {
                val filter = Filters.eq("id", game.id)
                val document = Document("id", game.id)
                    .append("host", host.uniqueId.toString())
                    .append("title", gameTitle)
                    .append("winners", winnersList)
                    .append("winnerKills", kills)
                    .append("scenarios", game.scenarios)
                    .append("teams", game.team)
                    .append("endTime", game.endTime)
                    .append("startTime", game.startTime)
                    .append("fill", game.fill)
                    .append("milliseconds", game.endTime!! - game.startTime)
                    .append("pveDeaths", game.pve)

                this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
            }
        }
        try {
            with (JavaPlugin.getPlugin(Kraftwerk::class.java).dataSource.getCollection("opened_matches")) {
                val filter = Filters.eq("id", ConfigFeature.instance.data!!.getInt("matchpost.id"))
                val document = this.find(filter).first()
                if (document != null) {
                    document["needsDelete"] = true
                    this.findOneAndReplace(filter, document, FindOneAndReplaceOptions().upsert(true))
                }
            }
        } catch (e: MongoException) {
            Chat.sendMessage(sender, "${ChatColor.RED}An error occurred while ending the game and updating the matchpost (opened).")
            e.printStackTrace()
        }
        val embed = EmbedBuilder()
        embed.setColor(MiscUtils.hexToColor(ConfigFeature.instance.config!!.getString("discord.embed-color")!!))
        embed.setTitle(ConfigFeature.instance.data!!.getString("matchpost.host"))
        embed.setThumbnail("https://visage.surgeplay.com/bust/512/${host.uniqueId}")
        winners.joinToString(", ", "", "", -1, "...") {
            "$it [${
                ConfigFeature.instance.data!!.getInt(
                    "game.kills.${Bukkit.getOfflinePlayer(it).name}"
                )
            }]"
        } 
        embed.addField("Winners", winners.joinToString(", ", "", "", -1, "...") {
            "**$it** [${
                ConfigFeature.instance.data!!.getInt(
                    "game.kills.${Bukkit.getOfflinePlayer(it).name}"
                )
            }]"
        }, false)
        for (team in TeamsFeature.manager.getTeams()) {
            for (player in team.players) {
                team.removePlayer(player)
            }
        }
        embed.addField("Fill", "${game.fill}", false)
        embed.addField("Matchpost", "https://hosts.uhc.gg/m/${ConfigFeature.instance.data!!.getInt("matchpost.id")}", false)
        try {
            if (Kraftwerk.instance.winnersChannelId != null && !ConfigOptionHandler.getOption("statless")!!.enabled) {
                Discord.instance!!.getNewsChannelById(Kraftwerk.instance.winnersChannelId!!)!!.sendMessageEmbeds(embed.build()).queue()
            } else {
                Chat.broadcast("Couldn't post winners to Discord because there isn't a channel ID configured.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        ConfigFeature.instance.data!!.set("game.winners", ArrayList<String>())
        ConfigFeature.instance.data!!.set("game.list", ArrayList<String>())
        ConfigFeature.instance.data!!.set("game.kills", null)
        ConfigFeature.instance.data!!.set("game.nether", false)
        ConfigFeature.instance.data!!.set("game.nether.nether", false)
        ConfigFeature.instance.data!!.set("whitelist.requests", false)
        ConfigFeature.instance.data!!.set("whitelist.list", ArrayList<String>())
        ConfigFeature.instance.data!!.set("matchpost.opens", null)
        ConfigFeature.instance.data!!.set("matchpost.host", null)
        ConfigFeature.instance.data!!.set("matchpost.posted", null)
        ConfigFeature.instance.saveData()
        Bukkit.broadcast(Chat.colored(Chat.line))
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.isSpec(player)) SpecFeature.instance.unspec(player)
            SpawnFeature.instance.send(player)
            Chat.sendCenteredMessage(player, "${Chat.primaryColor}&lGAME OVER!")
            Chat.sendMessage(player, " ")
            Chat.sendCenteredMessage(player, "<gray>Congratulations to the winners: ${Chat.secondaryColor}${winners.joinToString(", ")}<gray>!")
            Chat.sendCenteredMessage(player, "<gray>The server will restart in ${Chat.secondaryColor}45 seconds<gray>.")
        }
        for (world in Bukkit.getWorlds()) {
            world.pvp = true
        }
        val world = Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")!!)!!
        Bukkit.getServer().unloadWorld(world.name, true)
        for (file in Bukkit.getServer().worldContainer.listFiles()!!) {
            if (file.name.lowercase() == world.name.lowercase()) {
                Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach { it.delete() }
                file.delete()
                Log.info("Deleted world file for ${world.name}.")
            }
        }
        ConfigFeature.instance.data!!.set("pregen.world", null)
        ConfigFeature.instance.saveData()
        ConfigFeature.instance.worlds!!.set(world.name, null)
        ConfigFeature.instance.saveWorlds()
        Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
            for (player in Bukkit.getOnlinePlayers()) {
                player.kick(Chat.colored(if (ConfigFeature.instance.config!!.getString("chat.deathKick") != null) ConfigFeature.instance.config!!.getString("chat.deathKick")!! else "no death kick message sent but... thanks for playing :3"))
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart")
        }, 900L)
        Bukkit.broadcast(Chat.colored(Chat.line))
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl off")
        Leaderboards.updateLeaderboards()
        val log = File("./logs/latest.log")
        try {
            if (Kraftwerk.instance.gameLogsChannelId != null) {
                Discord.instance!!.getTextChannelById(Kraftwerk.instance.gameLogsChannelId!!)!!.sendMessage("**${gameTitle}**").queue()
                Discord.instance!!.getTextChannelById(Kraftwerk.instance.gameLogsChannelId!!)!!.sendFiles(FileUpload.fromData(log)).queue()
            } else {
                Log.info("Couldn't post logs to Discord because there's no game logs channel ID configured.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return true
    }

}
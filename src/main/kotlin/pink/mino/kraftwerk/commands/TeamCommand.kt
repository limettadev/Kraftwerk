package pink.mino.kraftwerk.commands

import com.lunarclient.apollo.Apollo
import com.lunarclient.apollo.common.location.ApolloLocation
import com.lunarclient.apollo.module.team.TeamMember
import com.lunarclient.apollo.module.team.TeamModule
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PerkChecker
import pink.mino.kraftwerk.utils.PlayerUtils
import java.awt.Color
import java.util.function.Consumer


class SendTeamView(val team: Team) : BukkitRunnable() {
    override fun run() {
        if (team == null) {
            cancel()
        }
        if (!TeamsFeature.manager.getTeams().contains(team)) {
            cancel()
        }

        try {
            if (team.size == 0) {
                cancel()
            }
            if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles"))) {
                cancel()
            }
            val list: ArrayList<Player> = arrayListOf()
            for (player in team.players) {
                if (player.isOnline) {
                    list.add(player as Player)
                }
            }
            for (player in team.players) {
                if (player.isOnline) {
                    if (Apollo.getPlayerManager().hasSupport(player.uniqueId)) {
                        TeamCommand().addTeamInfo(player as Player, list)
                    }
                }
            }
        } catch (e: Exception) {
            cancel()
        }
    }
}

class TeamCommand : CommandExecutor {

    private var invites = HashMap<Player, ArrayList<Player>>()
    private val settings: ConfigFeature = ConfigFeature.instance
    val colors = listOf(
        "black",
        "dark_blue",
        "dark_green",
        "dark_aqua",
        "dark_red",
        "dark_purple",
        "gold",
        "gray",
        "dark_gray",
        "blue",
        "green",
        "aqua",
        "red",
        "light_purple",
        "yellow",
        "white"
    )
    val modifiers = listOf(
        "bold",
        "underline",
        "italic"
    )

    private fun <T> splitList(list: ArrayList<T>, size: Int): MutableList<ArrayList<T>> {
        val iterator = list.iterator()
        val returnList: MutableList<ArrayList<T>> = ArrayList()
        while (iterator.hasNext()) {
            val tempList: MutableList<T> = ArrayList()
            for (i in 0 until size) {
                if (!iterator.hasNext()) break
                tempList.add(iterator.next())
            }
            returnList.add(tempList as ArrayList<T>)
        }
        return returnList
    }

    private fun getEmptyTeam(): Team? {
        val teams = TeamsFeature.manager.getTeams()
        for (team in teams) {
            if (team.size == 0) {
                return team
            }
        }
        return null
    }

    fun addTeamInfo(player: Player, playersToAdd: List<Player>) {
        val members: ArrayList<TeamMember> = arrayListOf()
        playersToAdd.forEach(Consumer { member: Player ->
            members.add(TeamMember.builder()
                .displayName(Component.text()
                    .content(member.name)
                    .color(NamedTextColor.WHITE)
                    .build())
                .playerUuid(member.uniqueId)
                .markerColor(Color.WHITE)
                .location(ApolloLocation.builder()
                    .world(member.world.name)
                    .x(member.location.x)
                    .y(member.location.y)
                    .z(member.location.z)
                    .build()
                )
                .build())
        })
        val teamModule = Apollo.getModuleManager().getModule(TeamModule::class.java)
        teamModule.updateTeamMembers(Apollo.getPlayerManager().getPlayer(player.uniqueId).get(), members.toList())
    }

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {

        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.team")) {
                if (ConfigFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                    Chat.sendMessage(
                        sender,
                        "${ChatColor.RED}You can't use this command at the moment. (It's an FFA game or Random teams)"
                    )
                    return false
                } else if (GameState.valueOf(ConfigFeature.instance.data!!.getString("game.state")) != GameState.LOBBY) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You can't use this command at the moment.")
                    return false
                }
            }
        }

        if (args.isEmpty()) {
            Chat.sendMessage(sender as Player, Chat.line)
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${Chat.secondaryColor}/team create ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Creates a team."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${Chat.secondaryColor}/team invite <player> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Invites a player to your team."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${Chat.secondaryColor}/team leave ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Leave your team."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${Chat.secondaryColor}/team accept <player> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Accept a player's team invite."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${Chat.secondaryColor}/team list ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Brings a list of teams and their members."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${Chat.secondaryColor}/pm <message> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Talk in team chat."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${Chat.secondaryColor}/team color <color> [bold] [italic] [underline] <dark_gray>(&2DONATOR<dark_gray>) ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Changes your team color."
            )
            Chat.sendMessage(
                sender,
                "${Chat.dash} ${Chat.secondaryColor}/pmc ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Send your coordinates."
            )
            Chat.sendMessage(sender, Chat.line)
            if (sender.hasPermission("uhc.staff.team")) {
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team reset ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Reset all teams."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team management <on/off> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Enable/disable team management."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team size <size> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Set the size of teams."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team set <player1> <player2> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Sets Player 1 to the Player 2's team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team bulk <list of players> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Adds a list of players to a team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team remove <player> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Removes a player from a team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team delete <team name> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Deletes the provided team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team friendlyfire <on/off> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Toggles friendly fire."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team kickunder <number> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Kicks all solos/teams under a certain threshold."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team randomize ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Randomizes all players that aren't Spectators into a team."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team setcolor <team> <color> ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Recolors the provided team to the one of your choosing."
                )
                Chat.sendMessage(
                    sender,
                    "${Chat.dash} ${Chat.secondaryColor}/team rvb ${ChatColor.DARK_GRAY}-${ChatColor.GRAY} Randomizes all players that aren't Spectators into a two teams together."
                )
                Chat.sendMessage(sender, Chat.line)
            }
        } else if (args[0] == "create") {
            val player = sender as Player
            if (player.scoreboard.getPlayerTeam(player) != null) {
                Chat.sendMessage(player, "<red>You're already on a team.")
                return true
            }
            if (ConfigFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                Chat.sendMessage(player, "<red>Team management is disabled at the moment.")
                return true
            }
            if (GameState.valueOf(ConfigFeature.instance.data!!.getString("game.state")) != GameState.LOBBY) {
                Chat.sendMessage(player, "<red>You can't manage teams while the game is running!")
                return true
            }

            val team = TeamsFeature.manager.createTeam(player)

            SendTeamView(team).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
            Chat.sendMessage(sender, "${Chat.prefix} Successfully created ${Chat.secondaryColor}${team.displayName}<gray>!")
        } else if (args[0] == "invite") {
            val player = sender as Player
            var team = TeamsFeature.manager.getTeam(player)
            if (ConfigFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                Chat.sendMessage(player, "${Chat.prefix} Team management is disabled at the moment.")
                return true
            }
            if (args.size == 1) {
                Chat.sendMessage(player, "${Chat.prefix} Usage: ${Chat.secondaryColor}/team invite <player>")
                return false
            }
            if (team == null) {
                team = TeamsFeature.manager.createTeam(player)
                SendTeamView(team).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
            }
            if (team.size >= ConfigFeature.instance.data!!.getString("game.teamSize").toInt()) {
                Chat.sendMessage(player, "<red>Your team is too full to invite anyone!")
                return true
            }
            val target = Bukkit.getServer().getPlayer(args[1])
            if (target == null) {
                Chat.sendMessage(player, "<red>That player is not online!")
                return false
            }
            val targetTeam = TeamsFeature.manager.getTeam(target)

            if (targetTeam != null) {
                Chat.sendMessage(player, "<red>That player is already on a team.")
                return true
            }
            if (target == player) {
                Chat.sendMessage(player, "<red>You can't send a invite request to yourself.")
                return true
            }

            for (players in team.players) {
                if (players is Player) {
                    Chat.sendMessage(
                        player,
                        "${Chat.prefix} ${ChatColor.WHITE}${target.name}${ChatColor.GRAY} was invited to your team."
                    )
                }
            }

            if (!invites.containsKey(player)) invites[player] = ArrayList()
            invites[player]!!.add(target)
            val text =
                TextComponent(Chat.colored("${Chat.prefix} §7To accept, type ${ChatColor.WHITE}/team accept ${player.name}${ChatColor.GRAY} or ${Chat.secondaryColor}&nclick here<gray>."))
            text.clickEvent = ClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                "/team accept ${player.name}"
            )
            Chat.sendMessage(target, Chat.line)
            Chat.sendMessage(
                target,
                "${Chat.prefix} You have been invited to ${ChatColor.WHITE}${player.name}${ChatColor.GRAY}'s team."
            )
            target.spigot().sendMessage(text)
            Chat.sendMessage(target, Chat.line)
        } else if (args[0] == "size") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size < 2) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You need to send a valid teamsize.")
                return false
            }
            if (args[1].toIntOrNull() == null) {
                Chat.sendMessage(sender as Player, "${ChatColor.RED}You need to send a valid teamsize.")
                return false
            }
            settings.data!!.set("game.teamSize", args[1].toInt())
            settings.saveData()
            Chat.sendMessage(
                sender,
                "${Chat.prefix} ${ChatColor.GRAY}The teamsize has been set to ${Chat.secondaryColor}${args[1]}${ChatColor.GRAY}."
            )
        } else if (args[0] == "accept") {
            val player = sender as Player
            if (args.size == 1) {
                player.sendMessage(Chat.colored("<red>Invalid usage: /team accept <player>"))
                return false
            }
            val target = Bukkit.getServer().getPlayer(args[1])
            val team = target.scoreboard.getPlayerTeam(target)
            if (ConfigFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                player.sendMessage("${ChatColor.RED}This is an FFA game.")
                return true
            }
            if (GameState.currentState != GameState.LOBBY) {
                player.sendMessage("${ChatColor.RED}You cannot do this command at the moment.")
                return true
            }
            if (target == null) {
                Chat.sendMessage(sender, "${ChatColor.RED}That player is not online.")
                return false
            }
            if (invites.containsKey(target) && invites[target]!!.contains(player)) {
                if (team.size >= ConfigFeature.instance.data!!.getString("game.teamSize").toInt()) {
                    player.sendMessage("${ChatColor.RED}That team is too full to join!")
                    return false
                }
                Chat.sendMessage(player, "${Chat.prefix} <gray>You have joined ${Chat.secondaryColor}${team.displayName}<gray>!")
                TeamsFeature.manager.joinTeam(team.name, player)
                for (players in team.players) {
                    if (players is Player && players != player) {
                        Chat.sendMessage(
                            players,
                            "${Chat.dash} ${ChatColor.WHITE}${player.name}${ChatColor.GRAY} joined your team."
                        )
                    }
                }

            } else {
                player.sendMessage("${ChatColor.RED}That player has not sent you a team invite.")
                return false
            }
        } else if (args[0] == "management") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(
                    sender as Player,
                    "${Chat.prefix} Invalid usage: ${ChatColor.WHITE}/team management on/off"
                )
                return false
            }
            if (args[1] != "on" && args[1] != "off") {
                Chat.sendMessage(
                    sender as Player,
                    "${Chat.prefix} Invalid usage: ${ChatColor.WHITE}/team management on/off"
                )
                return false
            }
            Chat.sendMessage(sender, Chat.line)
            if (args[1] == "on") {
                settings.data!!.set("game.ffa", false)
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${ChatColor.GRAY}Team management has been <green>enabled<gray>."))
            } else if (args[1] == "off") {
                settings.data!!.set("game.ffa", true)
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} ${ChatColor.GRAY}Team management has been <red>disabled<gray>."))
            }
            settings.saveData()
            Chat.sendMessage(sender, Chat.line)
        } else if (args[0] == "reset") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            val player = sender as Player
            TeamsFeature.manager.resetTeams()
            Chat.sendMessage(player, "${Chat.prefix} You've reset all teams.")
        } else if (args[0] == "leave") {
            val player = sender as Player
            val team = player.scoreboard.getPlayerTeam(player)
            if (ConfigFeature.instance.data!!.getString("game.ffa").toBoolean()) {
                player.sendMessage("${ChatColor.RED}You can't do this command at the moment.")
                return true
            }

            if (GameState.currentState != GameState.LOBBY) {
                player.sendMessage("${ChatColor.RED}You can't do this command at the moment.")
                return true
            }

            if (team == null) {
                player.sendMessage(ChatColor.RED.toString() + "You are not on a team.")
                return true
            }
            TeamsFeature.manager.leaveTeam(player)
            if (Apollo.getPlayerManager().hasSupport(player.uniqueId)) {
                val teamModule = Apollo.getModuleManager().getModule(TeamModule::class.java)
                teamModule.updateTeamMembers(Apollo.getPlayerManager().getPlayer(player.uniqueId).get(), listOf())
            }
            Chat.sendMessage(player, "${Chat.prefix} You left your team.")
            for (players in team.players) {
                if (players is Player) {
                    Chat.sendMessage(
                        players,
                        "${Chat.prefix}${ChatColor.WHITE}${player.name}${ChatColor.GRAY} left your team."
                    )
                }
            }
            if (team.players.size == 0) {
                TeamsFeature.manager.deleteTeam(team)
            }
        } else if (args[0] == "list") {
            Chat.sendMessage(sender, Chat.line)
            Chat.sendCenteredMessage(sender, "${Chat.primaryColor}&lTeams List")
            Chat.sendMessage(sender, " ")
            val teamList = ArrayList<Team>()
            if (ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles"))) {
                for ((_, team) in TeamsFeature.manager.getTeams().withIndex()) {
                    if (team.players.size != 0) {
                        teamList.add(team)
                        val list = ArrayList<String>()
                        for (player in team.players) {
                            list.add(player.name)
                        }
                        Chat.sendMessage(
                            sender,
                            "${team.displayName} <dark_gray>(${Chat.secondaryColor}${list.size}<dark_gray>) ${Chat.dash} ${Chat.secondaryColor}${list.joinToString(", ")}"
                        )
                    }
                }
                if (teamList.isEmpty()) {
                    Chat.sendCenteredMessage(sender, "<gray>&lThere are no teams right now!")
                }
                Chat.sendMessage(sender, Chat.line)
            } else {
                val keys = TeamsFeature.manager.teamMap.keys
                keys.forEach {
                    val team = TeamsFeature.manager.getTeam("UHC${it}")
                    if (team != null) {
                        val list = ArrayList<String>()
                        for (teammate in TeamsFeature.manager.teamMap[it]!!) {
                            if (teammate.isOnline) {
                                list.add("<green>${teammate.name}")
                            } else {
                                list.add("<red>${teammate.name}")
                            }
                        }
                        Chat.sendMessage(sender, "${team.displayName} <dark_gray>(${Chat.secondaryColor}${TeamsFeature.manager.teamMap[it]!!.size}<dark_gray>) ${Chat.dash} ${Chat.secondaryColor}${list.joinToString(", ")}")
                    }
                }
                if (keys.isEmpty()) {
                    Chat.sendCenteredMessage(sender, "<gray>&lThere are no teams right now!")
                }
                Chat.sendMessage(sender, Chat.line)
            }
        } else if (args[0] == "delete") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(sender, "<red>You need to provide a team to delete.")
                return false
            }
            var selectedTeam: Team? = null
            for (team in TeamsFeature.manager.getTeams()) {
                if (team.name == args[1]) {
                    selectedTeam = team
                }
            }
            if (selectedTeam == null) {
                Chat.sendMessage(
                    sender,
                    "<red>You need to provide a team to delete. (typically a team name has \"UHC\" and then the numerical ID next to it)"
                )
                return false
            }
            for (player in selectedTeam.players) {
                TeamsFeature.manager.leaveTeam(player)
                if (player.isOnline) {
                    if (Apollo.getPlayerManager().hasSupport(player.uniqueId)) {
                        val teamModule = Apollo.getModuleManager().getModule(TeamModule::class.java)
                        teamModule.updateTeamMembers(Apollo.getPlayerManager().getPlayer(player.uniqueId).get(), listOf())
                    }
                }
            }
            TeamsFeature.manager.deleteTeam(selectedTeam)
            Chat.sendMessage(
                sender,
                "${Chat.prefix} ${selectedTeam.prefix}${selectedTeam.name}<gray> has been deleted & all members kicked."
            )
        } else if (args[0] == "set") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size < 3) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team set <Player1> <Player2><gray>. <dark_gray>(${Chat.secondaryColor}Player 2 has to be the one with the team.<dark_gray>)"
                )
                return false
            }
            val target = Bukkit.getPlayer(args[1])
            val target2 = Bukkit.getPlayer(args[2])
            if (target2 == null || target == null) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid players: ${Chat.secondaryColor}/team set <Player1> <Player2><gray>. <dark_gray>(${Chat.secondaryColor}Player 2 has to be the one with the team.<dark_gray>)"
                )
                return false
            }
            val team = TeamsFeature.manager.getTeam(target2)
            if (team == null) {
                Chat.sendMessage(sender, "<red>That player is currently not in a team right now.")
                return false
            }
            val targetTeam = TeamsFeature.manager.getTeam(target2)
            TeamsFeature.manager.joinTeam(targetTeam!!.name, target)
            if (targetTeam != null) {
                if (targetTeam.size == 0) {
                    TeamsFeature.manager.deleteTeam(targetTeam)
                }
            }
            Chat.sendMessage(
                sender,
                "${Chat.prefix} Successfully added ${Chat.secondaryColor}${target.name}<gray> to ${Chat.secondaryColor}${target2.name}<gray>'s team"
            )
            Chat.sendMessage(target, "${Chat.prefix} You've been added to ${Chat.secondaryColor}${target2.name}<gray>'s team")
            for (player in team.players) {
                if (player.isOnline) {
                    Chat.sendMessage(player as Player, "${Chat.prefix} ${Chat.secondaryColor}${target.name}<gray> has been added to your team.")
                }
            }
        } else if (args[0] == "bulk" || args[0] == "ct") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size < 2) {
                Chat.sendMessage(sender, "${Chat.dash} Invalid usage: ${Chat.secondaryColor}/team bulk <list of players><gray>.")
                return false
            }
            val t: Team = TeamsFeature.manager.createTeam()
            for ((index, element) in args.withIndex()) {
                if (index == 0) continue
                val target = Bukkit.getPlayer(element)
                if (target == null) {
                    Chat.sendMessage(sender, "<red>${element} is not online.")
                    continue
                }
                val team = TeamsFeature.manager.getTeam(target)
                if (team != null) {
                    Chat.sendMessage(sender, "<red>${element} is already in a team.")
                    continue
                }
                TeamsFeature.manager.joinTeam(t.name, target)
            }
            Chat.sendMessage(sender, "${Chat.prefix} Successfully added all players to the team.")
        } else if (args[0] == "remove" || args[0] == "kick") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size < 2) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team remove <Player><gray>.")
                return false
            }
            val target = Bukkit.getPlayer(args[1])
            if (target == null) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid player: ${Chat.secondaryColor}${args[1]}<gray>.")
                return false
            }
            val team = TeamsFeature.manager.getTeam(target)
            if (team == null) {
                Chat.sendMessage(sender, "${Chat.prefix} That player is currently not in a team right now.")
                return false
            }
            TeamsFeature.manager.leaveTeam(target)
            Chat.sendMessage(
                sender,
                "${Chat.prefix} Successfully removed ${Chat.secondaryColor}${target.name}<gray> from ${Chat.secondaryColor}${team.name}<gray>'s team"
            )
            if (team.size == 0) {
                TeamsFeature.manager.deleteTeam(team)
            }
        } else if (args[0] == "friendlyfire") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team friendlyfire <on/off><gray>.")
                return false
            }
            if (args[1] != "on" && args[1] != "off") {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid arguments: ${Chat.secondaryColor}/team friendlyfire <on/off><gray>.")
                return false
            }
            if (args[1] == "on") {
                for (team in TeamsFeature.manager.getTeams()) {
                    team.setAllowFriendlyFire(true)
                    ConfigFeature.instance.data!!.set("game.friendlyFire", true)
                    ConfigFeature.instance.saveData()
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Friendly fire has been enabled by ${Chat.secondaryColor}${sender.name}<gray>."))
            }
            if (args[1] == "off") {
                for (team in TeamsFeature.manager.getTeams()) {
                    team.setAllowFriendlyFire(false)
                    ConfigFeature.instance.data!!.set("game.friendlyFire", false)
                    ConfigFeature.instance.saveData()
                }
                Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Friendly fire has been disabled by ${Chat.secondaryColor}${sender.name}<gray>."))
            }
        } else if (args[0] == "kickunder") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            if (args.size == 1) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team kickunder <number><gray>.")
                return false
            }
            if (args[1].toIntOrNull() == null) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid number: ${Chat.secondaryColor}/team kickunder <number><gray>.")
                return false
            }
            for (player in Bukkit.getOnlinePlayers()) {
                if (SpecFeature.instance.isSpec(player)) continue
                val team = TeamsFeature.manager.getTeam(player)
                if (team == null) {
                    player.kickPlayer(Chat.colored("<red>You've been kicked as you are not on a team."))
                } else {
                    if (team.players.size < args[1].toInt()) {
                        player.kickPlayer(Chat.colored("<red>You've been kicked as your team is undersized."))
                    }
                    //TeamsFeature.manager.deleteTeam(team)
                }
            }
        } else if (args[0] == "randomize") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            TeamsFeature.manager.resetTeams()
            Bukkit.broadcastMessage(
                Chat.colored(
                    "${Chat.dash} Randomizing all players into teams of ${Chat.primaryColor}${
                        ConfigFeature.instance.data!!.getInt(
                            "game.teamSize"
                        )
                    }<gray>."
                )
            )
            val valid: ArrayList<Player> = ArrayList()
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                    valid.add(player)
                }
            }
            valid.shuffle()
            val teams = splitList(valid, ConfigFeature.instance.data!!.getInt("game.teamSize"))
            var templist: ArrayList<Player>
            for (list in teams) {
                templist = ArrayList()
                for (player in list) {
                    templist.add(player)
                }
                val team: Team = TeamsFeature.manager.createTeam()
                for (player in templist) {
                    Chat.sendMessage(
                        player,
                        "${Chat.prefix} You've been added to ${team.prefix}${team.name}<gray>, check ${Chat.secondaryColor}/team list<gray> for the members of your team."
                    )
                    TeamsFeature.manager.joinTeam(team.name, player)
                }
            }
        } else if (args[0] == "rvb") {
            if (sender is Player) {
                if (!sender.hasPermission("uhc.staff.team")) {
                    Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                    return false
                }
            }
            TeamsFeature.manager.resetTeams()
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Randomizing all players into <red>Red<gray> vs &9Blue<gray>."))
            val valid: ArrayList<Player> = ArrayList()
            for (player in Bukkit.getOnlinePlayers()) {
                if (!SpecFeature.instance.getSpecs().contains(player.name)) {
                    valid.add(player)
                }
            }
            valid.shuffle()
            val red = TeamsFeature.manager.createTeam()
            val blue = TeamsFeature.manager.createTeam()
            red.prefix = "${ChatColor.RED}"
            blue.prefix = "${ChatColor.BLUE}"
            for ((index, player) in valid.withIndex()) {
                if (index % 2 == 0) {
                    TeamsFeature.manager.joinTeam(red.name, player)
                } else {
                    TeamsFeature.manager.joinTeam(blue.name, player)
                }
            }
            Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} <red>Red<gray> vs &9Blue<gray> teams have been randomized."))
        } else if (args[0] == "color") {
            if (!PerkChecker.checkPerk(sender as Player, "uhc.donator.teamColors")) {
                Chat.sendMessage(sender, "<red>Buy a &2Donator<red> rank to use this perk. <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config tough tits"}}")
                return false
            }
            if (TeamsFeature.manager.getTeam(sender) == null) {
                Chat.sendMessage(sender, "${Chat.prefix} You are not on a team.")
                return false
            }
            if (args.size == 1) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team color <color> [bold] [italic] [underline]<gray>."
                )
                return false
            }
            if (args.size > 5) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team color <color> [bold] [italic] [underline]<gray>."
                )
                return false
            }
            val colors = arrayListOf<ChatColor>()
            for ((index, arg) in args.withIndex()) {
                if (index == 0) continue
                try {
                    colors.add(ChatColor.valueOf(arg.uppercase()))
                } catch (e: Exception) {
                    Chat.sendMessage(sender, "${Chat.prefix} Invalid color: ${Chat.secondaryColor}$arg<gray>.")
                    Chat.sendMessage(
                        sender,
                        "${Chat.prefix} Valid colors: &0black<gray>, &1dark_blue<gray>, &2dark_green<gray>, &3dark_aqua<gray>, &4dark_red<gray>, &5dark_purple<gray>, &6gold<gray>, <gray>gray<gray>, <dark_gray>dark_gray<gray>, &9blue<gray>, <green>green<gray>, &baqua<gray>, <red>red<gray>, &dlight_purple<gray>, <yellow>yellow<gray>, &fwhite<gray>."
                    )
                    return false
                }
                colors.add(ChatColor.valueOf(arg.uppercase()))
            }
            if (colors.size == 0) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team color <color> [bold] [italic] [underline]<gray>."
                )
                return false
            }
            var selectedColor = ""
            if (colors.contains(ChatColor.BOLD)) {
                selectedColor += ChatColor.BOLD.toString()
                colors.removeAll(listOf(ChatColor.BOLD))
            }
            if (colors.contains(ChatColor.ITALIC)) {
                selectedColor += ChatColor.ITALIC.toString()
                colors.removeAll(listOf(ChatColor.ITALIC))
            }
            if (colors.contains(ChatColor.UNDERLINE)) {
                selectedColor += ChatColor.UNDERLINE.toString()
                colors.removeAll(listOf(ChatColor.UNDERLINE))
            }
            if (colors.isEmpty()) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team color <color> [bold] [italic] [underline]<gray>."
                )
                return false
            }
            selectedColor = colors[0].toString() + selectedColor
            if (!TeamsFeature.manager.colors.contains(selectedColor)) {
                Chat.sendMessage(sender, "${Chat.prefix} This color is already in use.")
                return false
            } else {
                TeamsFeature.manager.colors.add(TeamsFeature.manager.getTeam(sender)!!.prefix)
                TeamsFeature.manager.colors.remove(selectedColor)
                TeamsFeature.manager.getTeam(sender)!!.prefix = selectedColor
                Bukkit.broadcastMessage(Chat.colored("<dark_gray>[&2$$$<dark_gray>] ${Chat.secondaryColor}${PlayerUtils.getPrefix(sender)}${sender.name} <gray>has selected ${Chat.secondaryColor}$selectedColor${args[1]}<gray> as their team color."))
            }
        } else if (args[0] == "recolor") {
            if (!sender.hasPermission("uhc.staff.team")) {
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
            if (args.size == 2) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team recolor <team> <color> [bold] [italic] [underline]<gray>."
                )
                return false
            }
            if (args.size > 6) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team recolor <team> <color> [bold] [italic] [underline]<gray>."
                )
                return false
            }
            val colors = arrayListOf<ChatColor>()
            for ((index, arg) in args.withIndex()) {
                if (index == 0 || index == 1) continue
                try {
                    colors.add(ChatColor.valueOf(arg.uppercase()))
                } catch (e: Exception) {
                    Chat.sendMessage(sender, "${Chat.prefix} Invalid color: ${Chat.secondaryColor}$arg<gray>.")
                    Chat.sendMessage(
                        sender,
                        "${Chat.prefix} Valid colors: &0black<gray>, &1dark_blue<gray>, &2dark_green<gray>, &3dark_aqua<gray>, &4dark_red<gray>, &5dark_purple<gray>, &6gold<gray>, <gray>gray<gray>, <dark_gray>dark_gray<gray>, &9blue<gray>, <green>green<gray>, &baqua<gray>, <red>red<gray>, &dlight_purple<gray>, <yellow>yellow<gray>, &fwhite<gray>."
                    )
                    return false
                }
                colors.add(ChatColor.valueOf(arg.uppercase()))
            }
            if (colors.size == 0) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team recolor <team> <color> [bold] [italic] [underline]<gray>."
                )
                return false
            }
            var selectedColor = ""
            if (colors.contains(ChatColor.BOLD)) {
                selectedColor += ChatColor.BOLD.toString()
                colors.removeAll(listOf(ChatColor.BOLD))
            }
            if (colors.contains(ChatColor.ITALIC)) {
                selectedColor += ChatColor.ITALIC.toString()
                colors.removeAll(listOf(ChatColor.ITALIC))
            }
            if (colors.contains(ChatColor.UNDERLINE)) {
                selectedColor += ChatColor.UNDERLINE.toString()
                colors.removeAll(listOf(ChatColor.UNDERLINE))
            }
            if (colors.isEmpty()) {
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/team recolor <team> <color> [bold] [italic] [underline]<gray>."
                )
                return false
            }

            val team = TeamsFeature.manager.getTeam(args[1])
            if (team == null) {
                Chat.sendMessage(sender, "${Chat.prefix} Invalid team: ${Chat.secondaryColor}${args[1]}<gray>.")
                return false
            }

            selectedColor = colors[0].toString() + selectedColor
            if (!TeamsFeature.manager.colors.contains(selectedColor)) {
                Chat.sendMessage(sender, "${Chat.dash} This color is already in use.")
                return false
            } else {
                TeamsFeature.manager.colors.add(team.prefix)
                TeamsFeature.manager.colors.remove(selectedColor)
                team.prefix = selectedColor
                Chat.sendMessage(
                    sender,
                    "${Chat.prefix} You have changed the team color of ${team.prefix}${team.name}<gray> to ${Chat.secondaryColor}$selectedColor${args[2]}<gray>."
                )
                for (entry in team.entries) {
                    val player = Bukkit.getPlayer(entry)
                    if (player != null) {
                        Chat.sendMessage(
                            player,
                            "${Chat.prefix} Your team color has been changed to ${Chat.secondaryColor}$selectedColor${args[2]}<gray>."
                        )
                    }
                }
            }
        }
        return true
    }
}
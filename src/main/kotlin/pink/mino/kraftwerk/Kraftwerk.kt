package pink.mino.kraftwerk

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.ProtocolManager
import com.google.gson.Gson
import com.mongodb.MongoClient
import com.mongodb.MongoClientException
import com.mongodb.MongoClientURI
import com.mongodb.client.MongoDatabase
import me.lucko.helper.Schedulers
import me.lucko.helper.plugin.ExtendedJavaPlugin
import me.lucko.helper.utils.Log
import me.lucko.spark.api.Spark
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.Activity
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.meta.ItemMeta
import pink.mino.kraftwerk.commands.*
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.discord.Discord
import pink.mino.kraftwerk.features.*
import pink.mino.kraftwerk.listeners.*
import pink.mino.kraftwerk.listeners.donator.CowboyFeature
import pink.mino.kraftwerk.listeners.donator.MobEggsListener
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.*
import pink.mino.kraftwerk.utils.recipes.RecipeCommand
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPubSub
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import javax.security.auth.login.LoginException


/*
Dear weird person:
Only I and God know how this plugin works.

not anymore :)))))))))
 */

class Kraftwerk : ExtendedJavaPlugin() {

    private var protocolManager: ProtocolManager? = null
    var vote: Vote? = null
    var game: UHCTask? = null
    var opening: Opening? = null
    var scheduledOpening: ScheduleOpening? = null
    var scheduledBroadcast: ScheduleBroadcast? = null
    var database: Boolean = false
    var discord: Boolean = false
    var arena: Boolean = true
    var buildMode: HashMap<UUID, Boolean> = hashMapOf()

    val fullbright: MutableSet<String> = mutableSetOf()

    var scatterLocs: HashMap<String, Location> = HashMap()
    var scattering = false

    lateinit var discordInstance: JDA
    lateinit var statsHandler: StatsHandler
    lateinit var dataSource: MongoDatabase
    lateinit var redisManager: RedisManager
    lateinit var spark: Spark
    lateinit var profileHandler: ProfileService

    val sessionId = UUID.randomUUID()

    var welcomeChannelId: Long? = null
    var alertsRoleId: Long? = null
    var gameAlertsChannelId: Long? = null
    var winnersChannelId: Long? = null
    var preWhitelistChannelId: Long? = null
    var gameLogsChannelId: Long? = null
    var punishmentChannelId: Long? = null

    companion object {
        lateinit var instance: Kraftwerk
    }

    override fun load() {
        protocolManager = ProtocolLibrary.getProtocolManager()
    }

    override fun enable() {
        instance = this
        ConfigFeature.instance.setup(this)

        /* Registering listeners */
        Bukkit.getServer().pluginManager.registerEvents(ServerListPingListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerJoinListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerQuitListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerDeathListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerRespawnListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(CommandListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(ChatListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(WorldInitializeListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(WeatherChangeListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(SaturationFixer(), this)
        Bukkit.getServer().pluginManager.registerEvents(EntityHealthRegainListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerConnectListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PlayerConsumeListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(SpawnFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(UHCFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(RatesFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(CombatLogFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(SpecFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(ShootListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PortalListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(WorldSwitchListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(StatsFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(RespawnFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(PickupFeature.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(ChunkPopulateListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(OreLimiterListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(PregenListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(CanePopulatorFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(TeamsFeature.manager, this)
        Bukkit.getServer().pluginManager.registerEvents(OpenedMatchesListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(MobEggsListener(), this)
        Bukkit.getServer().pluginManager.registerEvents(CowboyFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(ArenaFeature(), this)
        Bukkit.getServer().pluginManager.registerEvents(OrganizedFights.instance, this)
        Bukkit.getServer().pluginManager.registerEvents(XpFeature(), this)
        //Bukkit.getServer().pluginManager.registerEvents(MLGFeature(), this)

        /* Registering commands */
        getCommand("clear")!!.setExecutor(ClearInventoryCommand())
        getCommand("cleareffects")!!.setExecutor(ClearPotionEffectsCommand())
        getCommand("feed")!!.setExecutor(FeedCommand())
        getCommand("heal")!!.setExecutor(HealCommand())
        getCommand("fly")!!.setExecutor(FlyCommand())
        getCommand("pregen")!!.setExecutor(PregenCommand())
        getCommand("config")!!.setExecutor(ConfigCommand())
        getCommand("editconfig")!!.setExecutor(EditConfigCommand())
        getCommand("world")!!.setExecutor(WorldCommand())
        getCommand("clearchat")!!.setExecutor(ClearChatCommand())
        getCommand("whitelist")!!.setExecutor(WhitelistCommand())
        getCommand("regenarena")!!.setExecutor(RegenArenaCommand())
        getCommand("start")!!.setExecutor(StartCommand())
        getCommand("border")!!.setExecutor(BorderCommand())
        getCommand("end")!!.setExecutor(EndGameCommand())
        getCommand("winner")!!.setExecutor(WinnerCommand())
        getCommand("latescatter")!!.setExecutor(LatescatterCommand())
        getCommand("matchpost")!!.setExecutor(MatchpostCommand())
        getCommand("scenariomanager")!!.setExecutor(ScenarioManagerCommand())
        getCommand("spectate")!!.setExecutor(SpectateCommand())
        getCommand("specchat")!!.setExecutor(SpecChatCommand())
        getCommand("helpop")!!.setExecutor(HelpOpCommand())
        getCommand("helpopreply")!!.setExecutor(HelpOpReplyCommand())
        getCommand("tppos")!!.setExecutor(TeleportPositionCommand())
        getCommand("tp")!!.setExecutor(TeleportCommand())
        getCommand("cancel")!!.setExecutor(CancelCommand())
        getCommand("near")!!.setExecutor(NearbyCommand())
        getCommand("startvote")!!.setExecutor(StartVoteCommand())
        getCommand("force")!!.setExecutor(ForceCommand())
        getCommand("respawn")!!.setExecutor(RespawnCommand())
        getCommand("editpregen")!!.setExecutor(EditPregenCommand())
        getCommand("generate")!!.setExecutor(GenerateCommand())
        getCommand("giveitems")!!.setExecutor(GiveItemsCommand())
        getCommand("invsee")!!.setExecutor(InvseeCommand())
        getCommand("helpoplist")!!.setExecutor(HelpopListCommand())
        getCommand("game")!!.setExecutor(GameCommand())
        getCommand("setspawn")!!.setExecutor(SetSpawnCommand())
        getCommand("orgs")!!.setExecutor(OrganizedFightsCommand())

        getCommand("gm")!!.setExecutor(GamemodeCommand())
        getCommand("gamemode")!!.setExecutor(GamemodeCommand())
        getCommand("gma")!!.setExecutor(GamemodeCommand())
        getCommand("gms")!!.setExecutor(GamemodeCommand())
        getCommand("gmsp")!!.setExecutor(GamemodeCommand())
        getCommand("gmc")!!.setExecutor(GamemodeCommand())

        getCommand("msg")!!.setExecutor(MessageCommand())
        getCommand("reply")!!.setExecutor(ReplyCommand())
        getCommand("team")!!.setExecutor(TeamCommand())
        getCommand("health")!!.setExecutor(HealthCommand())
        getCommand("pm")!!.setExecutor(PMCommand())
        getCommand("pmc")!!.setExecutor(PMCCommand())
        getCommand("pmores")!!.setExecutor(PMOresCommand())
        getCommand("pmminedores")!!.setExecutor(PMMOresCommand())
        getCommand("spawn")!!.setExecutor(SpawnCommand())
        getCommand("killtop")!!.setExecutor(KillTopCommand())
        getCommand("scenarios")!!.setExecutor(ScenarioCommand())
        getCommand("discord")!!.setExecutor(DiscordCommand())
        getCommand("apply")!!.setExecutor(ApplyCommand())
        getCommand("teaminventory")!!.setExecutor(TeamInventoryCommand())
        getCommand("moles")!!.setExecutor(MolesCommand())
        getCommand("molekit")!!.setExecutor(MoleKitCommand())
        getCommand("molechat")!!.setExecutor(MoleChatCommand())
        getCommand("moleloc")!!.setExecutor(MoleLocationCommand())
        getCommand("molelist")!!.setExecutor(MolesListCommand())
        getCommand("store")!!.setExecutor(StoreCommand())
        getCommand("rules")!!.setExecutor(RulesCommand())
        getCommand("statistics")!!.setExecutor(StatsCommand())
        getCommand("ping")!!.setExecutor(PingCommand())
        getCommand("voteyes")!!.setExecutor(VoteYesCommand())
        getCommand("voteno")!!.setExecutor(VoteNoCommand())
        getCommand("donator")!!.setExecutor(DonatorCommand())
        getCommand("fullbright")!!.setExecutor(FullbrightCommand())
        getCommand("timers")!!.setExecutor(TimersCommand())
        getCommand("arena")!!.setExecutor(ArenaCommand())
        getCommand("deathloc")!!.setExecutor(DeathLocCommand())
        getCommand("media")!!.setExecutor(MediaCommand())
        getCommand("enemyrecon")!!.setExecutor(EnemyReconCommand())
        getCommand("ignore")!!.setExecutor(IgnoreCommand())
        getCommand("profile")!!.setExecutor(ProfileCommand())
        getCommand("portalloc")!!.setExecutor(PortalPosCommand())
        getCommand("chat")!!.setExecutor(ChatCommand())
        getCommand("staffchat")!!.setExecutor(StaffChatCommand())
        getCommand("emotes")!!.setExecutor(EmotesCommand())
        getCommand("redstone")!!.setExecutor(RedstoneCommand())
        getCommand("lapis")!!.setExecutor(LapisCommand())
        getCommand("granttag")!!.setExecutor(GrantTagCommand())
        getCommand("thanks")!!.setExecutor(ThanksCommand())
        getCommand("fight")!!.setExecutor(FightCommand())
        getCommand("resethealth")!!.setExecutor(ResetHealthCommand())
        getCommand("buildmode")!!.setExecutor(BuildModeCommand())
        getCommand("setthing")!!.setExecutor(SetThingCommand())
        getCommand("alts")!!.setExecutor(AltsCommand())
        getCommand("ban")!!.setExecutor(BanCommand())
        getCommand("kick")!!.setExecutor(KickCommand())
        getCommand("mute")!!.setExecutor(MuteCommand())
        getCommand("helpopmute")!!.setExecutor(HelpopMuteCommand())
        getCommand("disqualify")!!.setExecutor(DisqualifyCommand())
        getCommand("warn")!!.setExecutor(WarnCommand())
        getCommand("unban")!!.setExecutor(UnbanCommand())
        getCommand("unhelpopmute")!!.setExecutor(UnHelpopMuteCommand())
        getCommand("unmute")!!.setExecutor(UnmuteCommand())
        getCommand("plugins")!!.setExecutor(PluginsCommand())
        getCommand("recipes")!!.setExecutor(RecipeCommand())
        getCommand("extend")!!.setExecutor(ExtendCommand())
        //getCommand("hotbar")!!.setExecutor(HotbarCommand()

        /* ProtocolLib stuff */
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            println("You need ProtocolLib in order to use this plugin.")
            Bukkit.getPluginManager().disablePlugin(this)
            return
        }

        /* This just enables Hardcore Hearts */
        protocolManager?.addPacketListener(HardcoreHeartsFeature())
        protocolManager!!.addPacketListener(SpecClickFeature())
        CustomPayloadFixerFeature(this)

        /* Sets up misc features */
        setupDataSource()
        TeamsFeature.manager.setupColors()
        Scoreboard.setup()
        if (Scoreboard.sb.getObjective("killboard") != null) {
            Scoreboard.kills!!.unregister()
            Scoreboard.setup()
        }
        ConfigOptionHandler.setup()
        ScenarioHandler.setup()
        addRecipes()

        statsHandler = StatsHandler()
        profileHandler = ProfileService()

        val provider = Bukkit.getServicesManager().getRegistration(
            Spark::class.java
        )
        if (provider != null) {
            spark = provider.provider
        }

        try {
            if (ConfigFeature.instance.config!!.getBoolean("database.redis.enabled")) {
                redisManager = RedisManager()
                if (redisManager != null && ConfigFeature.instance.config!!.getString("chat.serverName") != null) {
                    val playerPubSub = object : JedisPubSub() {
                        override fun onMessage(channel: String?, message: String?) {
                            val playerJoinMessage = Gson().fromJson(message!!, PlayerJoinMessage::class.java)
                            if (playerJoinMessage.sessionId != sessionId) {
                                Schedulers.sync().run {
                                    val player = Bukkit.getPlayer(playerJoinMessage.playerUniqueId)
                                    if (player != null) {
                                        player.kick(Chat.colored("<red>You've logged into a new location."))
                                    }
                                }
                            }
                        }
                    }
                    Schedulers.async().run {
                        redisManager.executeCommand { redis: Jedis ->
                            redis.subscribe(playerPubSub, "players")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /* Discord */
        try {
            Discord.main()
            if (!ConfigFeature.instance.data!!.getBoolean("matchpost.posted")) ConfigFeature.instance.data!!.set("whitelist.requests", false)
            ConfigFeature.instance.saveData()
            if (!ConfigFeature.instance.data!!.getBoolean("matchpost.cancelled")) {
                if (ConfigFeature.instance.data!!.getString("matchpost.opens") != null) {
                    scheduledBroadcast = ScheduleBroadcast(ConfigFeature.instance.data!!.getString("matchpost.opens")!!)
                    scheduledBroadcast!!.runTaskTimer(this, 0L, 300L)
                    scheduledOpening = ScheduleOpening(ConfigFeature.instance.data!!.getString("matchpost.opens")!!)
                    scheduledOpening!!.runTaskTimer(this, 0L, 300L)

                }
                if (ConfigFeature.instance.data!!.getString("matchpost.host") == null) {
                    Discord.instance!!.presence.activity = Activity.playing(if (ConfigFeature.instance.config!!.getString("chat.serverIp") != null) ConfigFeature.instance.config!!.getString("chat.serverIp")!! else "no server ip setup in config tough tits")
                }
                else Discord.instance!!.presence.activity = Activity.playing(ConfigFeature.instance.data!!.getString("matchpost.host")!!)
            } else {
                Discord.instance!!.presence.activity = Activity.playing(if (ConfigFeature.instance.config!!.getString("chat.serverIp") != null) ConfigFeature.instance.config!!.getString("chat.serverIp")!! else "no server ip setup in config tough tits")
                ConfigFeature.instance.data!!.set("matchpost.cancelled", null)
                ConfigFeature.instance.saveData()
            }
        } catch (e: LoginException) {
            Log.severe("Failed to login to discord: " + e.message)
        }

        if (ConfigFeature.instance.config!!.getLong("discord.welcomeChannelId") != null) {
            welcomeChannelId = ConfigFeature.instance.config!!.getLong("discord.welcomeChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.punishmentChannelId") != null) {
            punishmentChannelId = ConfigFeature.instance.config!!.getLong("discord.punishmentChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.alertsRoleId") != null) {
            alertsRoleId = ConfigFeature.instance.config!!.getLong("discord.alertsRoleId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.gameAlertsChannelId") != null) {
            gameAlertsChannelId = ConfigFeature.instance.config!!.getLong("discord.gameAlertsChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.winnersChannelId") != null) {
            winnersChannelId = ConfigFeature.instance.config!!.getLong("discord.winnersChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.preWhitelistChannelId") != null) {
            preWhitelistChannelId = ConfigFeature.instance.config!!.getLong("discord.preWhitelistChannelId")
        }
        if (ConfigFeature.instance.config!!.getLong("discord.gameLogsChannelId") != null) {
            gameLogsChannelId = ConfigFeature.instance.config!!.getLong("discord.gameLogsChannelId")
        }
        //twitterInstance.updateStatus("test")


        GameState.setState(GameState.LOBBY)
        Log.info("Game state set to Lobby.")
        for (world in Bukkit.getWorldContainer().list()!!) {
            if (world == "Spawn" || world == "Arena") {
                server.createWorld(WorldCreator(world))
            } else {
                val wc = WorldCreator(world)
                if (ConfigFeature.instance.worlds!!.getString("${world}.type")!!.lowercase() == "normal") {
                    wc.environment(World.Environment.NORMAL)
                } else if (ConfigFeature.instance.worlds!!.getString("${world}.type")!!.lowercase() == "nether") {
                    wc.environment(World.Environment.NETHER)
                } else if (ConfigFeature.instance.worlds!!.getString("${world}.type")!!.lowercase() == "end") {
                    wc.environment(World.Environment.THE_END)
                } else {
                    wc.environment(World.Environment.NORMAL)
                }
                server.createWorld(wc)
            }

            Log.info("World $world loaded.")
        }
        for (world in Bukkit.getWorlds()) {
            world.pvp = true
        }

        //Discord.instance!!.getTextChannelById(756953696038027425)!!.sendMessage("test")
        //UpdateLeaderboards().runTaskTimer(this, 0L, 20L)
        InfoFeature().runTaskTimerAsynchronously(this, 0L, 6000L)
        TabFeature().runTaskTimer(this, 0L, 20L)

        ConfigFeature.instance.data!!.set("whitelist.enabled", ConfigFeature.instance.config!!.getBoolean("options.whitelist-after-restart"))
        ConfigFeature.instance.data!!.set("game.list", listOf<Any>())
        ConfigFeature.instance.saveData()
        if (Bukkit.getPluginManager().getPlugin("HolographicDisplays") != null) {
            Leaderboards().runTaskTimer(this, 0L, 20L)
        }
        Bukkit.getLogger().info("Kraftwerk enabled.")
    }

    fun setupDataSource() {
        val uri = ConfigFeature.instance.config!!.getString("database.mongodb.uri")
        if (uri == null || uri == "") {
            Log.severe("No database URI set. Please set it in the config.")
            return
        }
        var client: MongoDatabase? = null
        try {
            val connectionString = MongoClientURI(uri)
            client = MongoClient(connectionString).getDatabase(connectionString.database!!)
        } catch (e: MongoClientException) {
            e.printStackTrace()
        }

        if (client != null) {
            this.dataSource = client
        }
    }

    override fun disable() {
        ConfigFeature.instance.data!!.set("game.winners", ArrayList<String>())
        ConfigFeature.instance.data!!.set("game.list", ArrayList<String>())
        ConfigFeature.instance.data!!.set("game.kills", null)
        ConfigFeature.instance.saveData()
        Bukkit.getWorldContainer().listFiles()!!.forEach { file ->
            if (file.name == "Spawn") {
                Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach {
                    if (it.isDirectory) {
                        if (it.name == "stats" || it.name == "playerdata") {
                            it.listFiles()?.forEach { file ->
                                file.delete()
                            }
                        }
                    }
                }
            }
        }
        for (team in TeamsFeature.manager.sb.teams) {
            team.unregister()
        }
        Bukkit.getLogger().info("Kraftwerk disabled.")
        Bukkit.getLogger().info("we don't work operation weed gang is a go")
        Bukkit.getServer().shutdown()
    }

    // TODO: ADD 1.8 SUPPORT
    fun addRecipes() {
        val mater = Material.PLAYER_HEAD
        val head = ItemStack(Material.GOLDEN_APPLE)
        val meta: ItemMeta = head.itemMeta
        meta.displayName(MiniMessage.miniMessage().deserialize("<gold>Golden Head"))
        meta.lore = listOf(ChatColor.DARK_PURPLE.toString() + "Some say consuming the head of a", ChatColor.DARK_PURPLE.toString() + "fallen foe strengthens the blood.")
        head.itemMeta = meta
        val goldenHead: ShapedRecipe = ShapedRecipe(head).shape("@@@", "@*@", "@@@").setIngredient('@', Material.GOLD_INGOT).setIngredient('*', mater)
        Bukkit.getServer().addRecipe(goldenHead)

        val woolMaterials = listOf(
            Material.WHITE_WOOL,
            Material.ORANGE_WOOL,
            Material.MAGENTA_WOOL,
            Material.LIGHT_BLUE_WOOL,
            Material.YELLOW_WOOL,
            Material.LIME_WOOL,
            Material.PINK_WOOL,
            Material.GRAY_WOOL,
            Material.LIGHT_GRAY_WOOL,
            Material.CYAN_WOOL,
            Material.PURPLE_WOOL,
            Material.BLUE_WOOL,
            Material.BROWN_WOOL,
            Material.GREEN_WOOL,
            Material.RED_WOOL,
            Material.BLACK_WOOL
        )

        for (woolMaterial in woolMaterials) {
            val recipe = ShapedRecipe(NamespacedKey.randomKey(), ItemStack(Material.STRING))
                .shape("AA", "AA")
                .setIngredient('A', woolMaterial)
            Bukkit.addRecipe(recipe)
        }
    }

}
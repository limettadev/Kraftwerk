package pink.mino.kraftwerk.listeners

import me.lucko.helper.Schedulers
import net.milkbowl.vault.chat.Chat
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.*
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.MolesScenario
import pink.mino.kraftwerk.utils.Perk
import pink.mino.kraftwerk.utils.PerkChecker
import pink.mino.kraftwerk.utils.PlayerUtils
import pink.mino.kraftwerk.utils.Tags
import java.util.*

class ChatListener : Listener {

    private var vaultChat: Chat? = null

    init {
        vaultChat = Bukkit.getServer().servicesManager.load(Chat::class.java)
    }

    val slurs = arrayListOf(
        "tranny",
        "troon",
        "nigger",
        "faggot",
        "retard",
        "kys",
        "nigga",
        "nga",
        "negro"
    )

    val cooldowns = hashMapOf<UUID, Long>()
    val cooldownTime: Int = 1

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        cooldowns[e.player.uniqueId] = System.currentTimeMillis() + 1000
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        val player = e.player
        val group: String? = vaultChat?.getPrimaryGroup(player)
        val prefix: String = ChatColor.translateAlternateColorCodes('&', vaultChat?.getGroupPrefix(player.world, group)!!)
        if (PerkChecker.checkPerks(player).contains(Perk.EMOTES)) {
            if (e.message.contains(":shrug:", true)) {
                e.message = e.message.replace(":shrug:", pink.mino.kraftwerk.utils.Chat.colored("<yellow>¯\\_(ツ)_/¯&r"))
            }
            if (e.message.contains(":yes:", true)) {
                e.message = e.message.replace(":yes:", pink.mino.kraftwerk.utils.Chat.colored("&l<green>✔&r"))
            }
            if (e.message.contains(":no:", true)) {
                e.message = e.message.replace(":no:", pink.mino.kraftwerk.utils.Chat.colored("&l<red>✖&r"))
            }
            if (e.message.contains("123", true)) {
                e.message = e.message.replace("123", pink.mino.kraftwerk.utils.Chat.colored("<green>1<yellow>2<red>3&r"))
            }
            if (e.message.contains("<3", true)) {
                e.message = e.message.replace("<3", pink.mino.kraftwerk.utils.Chat.colored("<red>❤&r"))
            }
            if (e.message.contains("o/", true)) {
                e.message = e.message.replace("o/", pink.mino.kraftwerk.utils.Chat.colored("&d(・∀・)ノ&r"))
            }
            if (e.message.contains(":star:", true)) {
                e.message = e.message.replace(":star:", pink.mino.kraftwerk.utils.Chat.colored("<yellow>✰&r"))
            }
            if (e.message.contains(":100:", true)) {
                e.message = e.message.replace(":100:", pink.mino.kraftwerk.utils.Chat.colored("<red>&o&l&n100&r"))
            }
            if (e.message.contains("o7", true)) {
                e.message = e.message.replace("o7", pink.mino.kraftwerk.utils.Chat.colored("<yellow>(｀-´)>&r"))
            }
            if (e.message.contains(":blush:", true)) {
                e.message = e.message.replace(":blush:", pink.mino.kraftwerk.utils.Chat.colored("&d(◡‿◡✿)&r"))
            }
        }
        var preference = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode
        if (preference == "MOLES") {
            e.isCancelled = true
            if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles")) || MolesScenario.instance.moles[player.uniqueId] == null) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "<red>Moles is not enabled. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    Bukkit.dispatchCommand(player, "mcc ${e.message}")
                }
            }
        }
        if (preference == "STAFF") {
            e.isCancelled = true
            if (!player.hasPermission("uhc.staff")) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "<red>You aren't a Staff member. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    Bukkit.dispatchCommand(player, "ac ${e.message}")
                }
            }
        }
        if (preference == "SPEC") {
            e.isCancelled = true
            if (!SpecFeature.instance.isSpec(player)) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "<red>You aren't a Staff member. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    Bukkit.dispatchCommand(player, "sc ${e.message}")
                }
            }
        }
        if (preference == "TEAM") {
            e.isCancelled = true
            if (TeamsFeature.manager.getTeam(player) == null) {
                pink.mino.kraftwerk.utils.Chat.sendMessage(player, "<red>You aren't on a Team. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    Bukkit.dispatchCommand(player, "pm ${e.message}")
                }
            }
        }
        if (preference == "PUBLIC") {
            if (!PerkChecker.checkPerks(e.player).contains(Perk.NO_CHAT_DELAY)) {
                val secondsLeft: Long = cooldowns[e.player.uniqueId]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                if (secondsLeft > 0) {
                    e.isCancelled = true
                    pink.mino.kraftwerk.utils.Chat.sendMessage(player, "<red>You are currently on cooldown for ${secondsLeft}s. Skip the cooldown by purchasing a rank at <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config soft titties"}<red>.")
                    return
                }
            }

            e.isCancelled = false
            if (!PerkChecker.checkPerks(e.player).contains(Perk.WHITE_CHAT)) {
                val words = e.message.split(" ")
                for (word in words) {
                    if (slurs.contains(word.lowercase())) {
                        e.isCancelled = true
                        pink.mino.kraftwerk.utils.Chat.sendMessage(e.player, "$prefix ${PlayerUtils.getPrefix(player)}${player.name} <dark_gray>»<gray> ${e.message}")
                        Schedulers.sync().runLater({
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute ${player.name} -s 1d Inappropiate Language (auto)")
                        }, (5 * 20).toLong())
                    }
                }
            }
            val color = if (PerkChecker.checkPerks(player).contains(Perk.WHITE_CHAT)) "&f" else "<gray>"
            val tag = Kraftwerk.instance.profileHandler.getProfile(player.uniqueId)!!.selectedTag
            var display = ""
            if (tag != null) {
                display = " ${Tags.valueOf(tag.uppercase()).display}"
            }

            val mutePunishment = PunishmentFeature.getActivePunishment(player, PunishmentType.MUTE)
            if (mutePunishment != null && !player.hasPermission("uhc.staff")) {
                val remaining = mutePunishment.expiresAt - System.currentTimeMillis()
                if (remaining > 0) {
                    e.isCancelled = true
                    val timeLeft = PunishmentFeature.timeToString(remaining)
                    player.sendMessage(pink.mino.kraftwerk.utils.Chat.colored("<red>You are muted for another $timeLeft. Reason: ${mutePunishment.reason}"))
                }
            }
            e.format = prefix + pink.mino.kraftwerk.utils.Chat.colored("${PlayerUtils.getPrefix(player)}%s") + pink.mino.kraftwerk.utils.Chat.colored(display) + ChatColor.DARK_GRAY + " » " + pink.mino.kraftwerk.utils.Chat.colored(color) + "%s"
            cooldowns[player.uniqueId] = System.currentTimeMillis()
        }
    }

}
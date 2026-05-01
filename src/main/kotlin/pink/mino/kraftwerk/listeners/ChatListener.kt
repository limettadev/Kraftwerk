package pink.mino.kraftwerk.listeners

import io.papermc.paper.event.player.AsyncChatEvent
import me.lucko.helper.Schedulers
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.*
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.MolesScenario
import pink.mino.kraftwerk.utils.*
import java.util.*

class ChatListener : Listener {

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
    fun onPlayerChat(e: AsyncChatEvent) {
        val player = e.player
        val message = e.message()
        val user = Kraftwerk.instance.luckPerms.getPlayerAdapter(Player::class.java).getUser(player)
        val prefix: String = user.cachedData.metaData.prefix!!
        if (PerkChecker.checkPerks(player).contains(Perk.EMOTES)) {
            var msg = (e.message() as TextComponent).content()

            if (msg.contains(":shrug:", true))
                msg = msg.replace(":shrug:", "<yellow>¯\\_(ツ)_/¯</yellow>")
            if (msg.contains(":yes:", true))
                msg = msg.replace(":yes:", "<bold><green>✔</green></bold>")
            if (msg.contains(":no:", true))
                msg = msg.replace(":no:", "<bold><red>✖</red></bold>")
            if (msg.contains("123", true))
                msg = msg.replace("123", "<green>1</green><yellow>2</yellow><red>3</red>")
            if (msg.contains("<3", true))
                msg = msg.replace("<3", "<red>❤</red>")
            if (msg.contains("o/", true))
                msg = msg.replace("o/", "<light_purple>(・∀・)ノ</light_purple>")
            if (msg.contains(":star:", true))
                msg = msg.replace(":star:", "<yellow>✰</yellow>")
            if (msg.contains(":100:", true))
                msg = msg.replace(":100:", "<red><bold><italic><underlined>100</underlined></italic></bold></red>")
            if (msg.contains("o7", true))
                msg = msg.replace("o7", "<yellow>(｀-´)></yellow>")
            if (msg.contains(":blush:", true))
                msg = msg.replace(":blush:", "<light_purple>(◡‿◡✿)</light_purple>")

            e.message(Component.text(msg))
        }
        var preference = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode
        if (preference == "MOLES") {
            e.isCancelled = true
            if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles")) || MolesScenario.instance.moles[player.uniqueId] == null) {
                Chat.sendMessage(player, "<red>Moles is not enabled. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    player.performCommand("mcc ${(e.message() as TextComponent).content()}")
                }
            }
        }
        if (preference == "STAFF") {
            e.isCancelled = true
            if (!player.hasPermission("uhc.staff")) {
                Chat.sendMessage(player, "<red>You aren't a Staff member. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    player.performCommand("ac ${(e.message() as TextComponent).content()}")
                }
            }
        }
        if (preference == "SPEC") {
            e.isCancelled = true
            if (!SpecFeature.instance.isSpec(player)) {
                Chat.sendMessage(player, "<red>You aren't a Staff member. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    player.performCommand("sc ${(e.message() as TextComponent).content()}")
                }
            }
        }
        if (preference == "TEAM") {
            e.isCancelled = true
            if (TeamsFeature.manager.getTeam(player) == null) {
                Chat.sendMessage(player, "<red>You aren't on a Team. Setting your chat mode back to PUBLIC.")
                preference = "PUBLIC"
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.chatMode = "PUBLIC"
            } else {
                Schedulers.sync().run {
                    player.performCommand("pm ${(e.message() as TextComponent).content()}")
                }
            }
        }
        if (preference == "PUBLIC") {
            if (!PerkChecker.checkPerks(e.player).contains(Perk.NO_CHAT_DELAY)) {
                val secondsLeft: Long = cooldowns[e.player.uniqueId]!! / 1000 + cooldownTime - System.currentTimeMillis() / 1000
                if (secondsLeft > 0) {
                    e.isCancelled = true
                    Chat.sendMessage(player, "<red>You are currently on cooldown for ${secondsLeft}s. Skip the cooldown by purchasing a rank at <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config soft titties"}<red>.")
                    return
                }
            }

            e.isCancelled = false
            if (!PerkChecker.checkPerks(e.player).contains(Perk.WHITE_CHAT)) {
                val words = (e.message() as TextComponent).content().split(" ")
                for (word in words) {
                    if (slurs.contains(word.lowercase())) {
                        e.isCancelled = true
                        Chat.sendMessage(e.player, "$prefix ${PlayerUtils.getPrefix(player)}${player.name} <dark_gray>»<gray> ${e.message()}")
                        Schedulers.sync().runLater({
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "mute ${player.name} -s 1d Inappropiate Language (auto)")
                        }, (5 * 20).toLong())
                    }
                }
            }
            val color = if (PerkChecker.checkPerks(player).contains(Perk.WHITE_CHAT)) "<white>" else "<gray>"
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
                    player.sendMessage(Chat.colored("<red>You are muted for another $timeLeft. Reason: ${mutePunishment.reason}"))
                }
            }
            e.renderer { player, sourceDisplayName, message, audience ->
                Chat.colored("$prefix ${PlayerUtils.getPrefix(player)}${player.name} <dark_gray>» $color${(e.message() as TextComponent).content()}")
            }
            cooldowns[player.uniqueId] = System.currentTimeMillis()
        }
    }

}
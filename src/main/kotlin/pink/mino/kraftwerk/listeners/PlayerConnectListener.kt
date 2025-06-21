package pink.mino.kraftwerk.listeners

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.Events
import pink.mino.kraftwerk.features.PunishmentFeature
import pink.mino.kraftwerk.features.PunishmentType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState

class PlayerConnectListener : Listener {
    @EventHandler
    fun onPlayerConnect(e: PlayerLoginEvent) {
        val ban = PunishmentFeature.getActivePunishment(e.player, PunishmentType.BAN)
        if (ban != null && !e.player.hasPermission("uhc.staff")) {
            val punisher = Bukkit.getOfflinePlayer(ban.punisherUuid)
            val timeLeft = PunishmentFeature.timeToString(ban.expiresAt - System.currentTimeMillis())
            e.disallow(
                PlayerLoginEvent.Result.KICK_BANNED,
                Chat.colored(
                    "${Chat.primaryColor}${Chat.scoreboardTitle}\n${Chat.line}\n\n" +
                            "<gray>You've been banned from the server by ${Chat.secondaryColor}${punisher.name}<gray>.\n" +
                            "<gray>Your ban expires in ${Chat.secondaryColor}$timeLeft<gray>.\n" +
                            "<gray>Reason: ${Chat.secondaryColor}${ban.reason}\n\n${Chat.line}"
                )
            )
        }


        val player = e.player
        if (ConfigFeature.instance.data!!.getBoolean("whitelist.enabled")) {
            if (!ConfigFeature.instance.data!!.getList("whitelist.list")!!.contains(player.name.lowercase())) {
                if (!player.hasPermission("uhc.staff")) {
                    if (GameState.currentState == GameState.INGAME) {
                        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game == null) {
                            e.disallow(
                                PlayerLoginEvent.Result.KICK_WHITELIST,
                                Chat.colored("<red>You are not allowed to join while the whitelist is on!\n<red>There is currently no game happening on this server.\n\n<gray>Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}<gray>!")
                            )
                            return
                        }
                        if (JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.currentEvent == Events.PVP || JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.currentEvent == Events.MEETUP) {
                            e.disallow(
                                PlayerLoginEvent.Result.KICK_WHITELIST,
                                Chat.colored("<red>You are not allowed to join while the whitelist is on!\n<red>PvP is currently enabled and no more players will be able to join late!\n\n<gray>Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}<gray>!")
                            )
                        } else {
                            if (ConfigFeature.instance.data!!.getString("matchpost.team") != null || ConfigFeature.instance.data!!.getString("matchpost.team") == "Auctions" || ConfigFeature.instance.data!!.getString("matchpost.team")!!.contains("Random")) {
                                e.disallow(
                                    PlayerLoginEvent.Result.KICK_WHITELIST,
                                    Chat.colored("<red>You are not allowed to join while the whitelist is on!\n<red>You cannot late scatter in an Auction or a Random teams game!\n\n<gray>Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}<gray>!")
                                )
                                return
                            }
                            e.disallow(
                                PlayerLoginEvent.Result.KICK_WHITELIST,
                                Chat.colored("<red>You are not allowed to join while the whitelist is on!\n<red>Please wait until the host accepts late scatters!\n\n<gray>Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}<gray>!")
                            )
                        }
                    } else {
                        e.disallow(
                            PlayerLoginEvent.Result.KICK_WHITELIST,
                            Chat.colored("<red>You are not allowed to join while the whitelist is on!\n<red>There is currently no game happening on this server.\n\n<gray>Get pre-whitelisted on the discord @ ${Chat.primaryColor}${if (ConfigFeature.instance.config!!.getString("chat.staffAppUrl") != null) ConfigFeature.instance.config!!.getString("chat.discordUrl") else "no discord url set in config tough tits"}<gray>!")
                        )
                    }
                } else {
                    return
                }
            } else {
                return
            }
        } else {
            return
        }
    }
}
package pink.mino.kraftwerk.listeners
import net.citizensnpcs.api.CitizensAPI
import net.kyori.adventure.title.Title
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.features.CombatLogFeature
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils
import pink.mino.kraftwerk.utils.Scoreboard


class PlayerDeathListener : Listener {
    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!e.entity.hasMetadata("NPC")) {
            val player = e.entity as Player
            val old = e.deathMessage
            if (ConfigOptionHandler.getOption("deathlightning")!!.enabled) player.world.strikeLightningEffect(player.location)
            e.deathMessage = ChatColor.translateAlternateColorCodes('&', "<dark_gray>»<white> $old")
            if (player.world.name == "Arena") {
                e.deathMessage = null
            }
            if (GameState.currentState == GameState.INGAME) {
                val killer = e.entity.killer
                if (killer != null) {
                    val o = ConfigFeature.instance.data!!.getInt("game.kills.${killer.name}")
                    ConfigFeature.instance.data!!.set("game.kills.${killer.name}", o + 1)
                    val color: String = if (TeamsFeature.manager.getTeam(killer) != null) {
                        TeamsFeature.manager.getTeam(killer)!!.prefix
                    } else {
                        "<white>"
                    }
                    Scoreboard.setScore(" ${color}${killer.name}", o + 1)
                } else {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.pve++
                    Scoreboard.setScore("${Chat.dash} <green>PvE", JavaPlugin.getPlugin(Kraftwerk::class.java).game!!.pve)
                }
                val list = ConfigFeature.instance.data!!.getStringList("game.list")
                list.remove(player.name)
                ConfigFeature.instance.data!!.set("game.list", list)
                ConfigFeature.instance.saveData()
                val kills = ConfigFeature.instance.data!!.getInt("game.kills.${player.name}")
                val color: String = if (TeamsFeature.manager.getTeam(player) != null) {
                    TeamsFeature.manager.getTeam(player)!!.prefix
                } else {
                    "<white>"
                }
                Scoreboard.deleteScore(" ${color}${player.name}")
                if (kills > 0) Scoreboard.setScore(" ${color}<strikethrough>${player.name}", kills)
                Scoreboard.setScore("${Chat.dash} <gray>Playing...", Math.max(PlayerUtils.getPlayingPlayers().size - 1, 0))
                CombatLogFeature.instance.removeCombatLog(player.name)
                if (!player.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wl remove ${player.name}")
                }
                e.droppedExp = e.droppedExp * 2
                if (TeamsFeature.manager.getTeam(player) != null) TeamsFeature.manager.getTeam(player)!!.removePlayer(player)
                val preference = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.deathMessageOnScreen
                if (preference) {
                    player.showTitle(Title.title(Chat.colored("<dark_red><bold>YOU DIED!"), Chat.colored("<gray>${e.deathMessage}")))
                }
            }
            Bukkit.getScheduler().runTaskLater(JavaPlugin.getPlugin(Kraftwerk::class.java), Runnable {
                player.spigot().respawn()
            }, 20L)
        } else {
            if (GameState.currentState == GameState.INGAME) {
                e.entity.world.strikeLightningEffect(e.entity.location)
                val player = e.entity
                val killer = e.entity.killer
                val npc = CitizensAPI.getNPCRegistry().getNPC(e.entity)
                e.deathMessage =
                    ChatColor.translateAlternateColorCodes('&', "<dark_gray>»<white> ${killer!!.name} has killed ${npc.name}")
                if (killer != null) {
                    val o = ConfigFeature.instance.data!!.getInt("game.kills.${killer.name}")
                    ConfigFeature.instance.data!!.set("game.kills.${killer.name}", o + 1)
                    val color: String = if (TeamsFeature.manager.getTeam(killer) != null) {
                        TeamsFeature.manager.getTeam(killer)!!.prefix
                    } else {
                        "<white>"
                    }
                    Scoreboard.setScore(" ${color}${killer.name}", o + 1)
                }
                val list = ConfigFeature.instance.data!!.getStringList("game.list")
                list.remove(player.name)
                ConfigFeature.instance.data!!.set("game.list", list)
                ConfigFeature.instance.saveData()
            }
        }
    }
}
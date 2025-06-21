package pink.mino.kraftwerk.commands

import me.lucko.helper.promise.Promise
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.*
import kotlin.math.floor
import kotlin.math.round

class StatsCommand : CommandExecutor {
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

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        var gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Stats")).owner(sender)
        val target: OfflinePlayer = if (args.isEmpty()) {
            sender
        } else {
            Bukkit.getOfflinePlayer(args[0])
        }
        Promise.start()
            .thenRunSync runnable@ {
                Chat.sendMessage(sender, "${Chat.prefix} <gray>Loading stats for ${Chat.secondaryColor}${target.name}<gray>...")
            }
            .thenApplyAsync {
                JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.lookupStatsPlayer(target)
            }
            .thenAcceptSync { statsPlayer ->
                val ores = ItemBuilder(Material.DIAMOND_ORE)
                    .name(" ${Chat.primaryColor}&lOres")
                    .addLore(" ")
                    .addLore(" <gray>Diamonds Mined ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.diamondsMined}")
                    .addLore(" <gray>Gold Mined ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.goldMined}")
                    .addLore(" <gray>Iron Mined ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.ironMined}")
                    .addLore(" ")
                    .make()
                val general = ItemBuilder(Material.DIAMOND)
                    .name(" ${Chat.primaryColor}&lGeneral")
                    .addLore(" ")
                    .addLore(" <gray>Kills ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.kills}")
                    .addLore(" <gray>Deaths ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.deaths}")
                    .addLore(" ")
                    .addLore(" <gray>Wins ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.wins}")
                    .addLore(" <gray>KDR ${Chat.dash} ${Chat.secondaryColor}${round((statsPlayer.kills.toDouble() / statsPlayer.deaths.toDouble()))}")
                    .addLore(" <gray>Games Played ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.gamesPlayed}")
                    .addLore(" ")
                    .noAttributes()
                    .make()
                val pvp = ItemBuilder(Material.DIAMOND_SWORD)
                    .noAttributes()
                    .name(" ${Chat.primaryColor}&lPvP")
                    .addLore(" ")
                    .addLore(" <gray>Damage Dealt ${Chat.dash } ${Chat.secondaryColor}${round(statsPlayer.damageDealt)}❤")
                    .addLore(" <gray>Damage Taken ${Chat.dash } ${Chat.secondaryColor}${round(statsPlayer.damageTaken)}❤")
                    .addLore(" ")
                    .addLore(" <gray>Bow Shots ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.bowShots}")
                    .addLore(" <dark_gray>&o(${statsPlayer.bowMisses} misses, ${statsPlayer.bowHits} hits)")
                    .addLore(" ")
                    .addLore(" <gray>Melee Hits ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.meleeHits}")
                    .addLore(" ")
                    .make()
                val arena = ItemBuilder(Material.IRON_SWORD)
                    .noAttributes()
                    .name(" ${Chat.primaryColor}&lArena")
                    .addLore(" ")
                    .addLore(" <gray>Kills ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.arenaKills}")
                    .addLore(" <gray>Deaths ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.arenaDeaths}")
                    .addLore(" <gray>Highest Killstreak ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.highestArenaKs}")
                    .addLore(" ")
                    .make()
                val misc = ItemBuilder(Material.WORKBENCH)
                    .name(" ${Chat.primaryColor}&lMisc.")
                    .addLore(" ")
                    .addLore(" <gray>Times Enchanted ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.timesEnchanted}")
                    .addLore(" <gray>Times Crafted  ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.timesCrafted}")
                    .addLore(" <gray>Nether Travels  ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.timesNether}")

                    .addLore(" <gray>Gapples Eaten ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.gapplesEaten}")
                    .addLore(" <gray>Gapples Crafted ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.gapplesEaten}")
                    .addLore(" ")
                    .make()

                val skull = ItemBuilder(Material.SKULL_ITEM)
                    .name("${Chat.secondaryColor}${statsPlayer.player.name}")
                    .addLore("<gray>The statistics for ${Chat.secondaryColor}${statsPlayer.player.name}<gray>.")
                    .toSkull()
                    .setOwner(statsPlayer.player.name)
                    .make()

                val staff = ItemBuilder(Material.IRON_BLOCK)
                    .name(" ${Chat.primaryColor}&lStaff")
                    .addLore(" ")
                    .addLore(" <gray>Time Spectated ${Chat.dash} ${Chat.secondaryColor}${timeToString(round(statsPlayer.timeSpectated.toDouble() / 1000).toLong())} ")
                    .addLore(" <gray>Thank Yous ${Chat.dash} ${Chat.secondaryColor}${statsPlayer.thankYous}")
                    .addLore(" ")
                    .make()

                val resetStats = ItemBuilder(Material.BARRIER)
                    .name(" &4&lReset Stats <gray>(Donator Perk)")
                    .addLore(" ")
                    .addLore(" <gray>Clicking this will allow you to reset your statistics. ")
                    .addLore(" <red>&lWARNING:<red> Doing this is a dangerous action! ")
                    .addLore(" ")
                    .make()

                gui.item(0, skull).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(2, general).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(3, misc).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(4, ores).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(5, pvp).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(6, arena).onClick runnable@ {
                    it.isCancelled = true
                }
                gui.item(7, staff).onClick runnable@ {
                    it.isCancelled = true
                }
                if (target == sender) {
                    gui.item(8, resetStats).onClick runnable@ {
                        if (PerkChecker.checkPerks(sender as Player).contains(Perk.STATS_RESET)) {
                            gui = GuiBuilder().rows(1).name(Chat.colored("Reset your stats?")).owner(sender)

                            val accept = ItemBuilder(Material.WOOL)
                                .setDurability(5)
                                .name("<green>Accept")
                                .addLore("<gray>Accept and reset your stats.")
                            val decline = ItemBuilder(Material.WOOL)
                                .setDurability(14)
                                .name("<red>Decline")
                                .addLore("<gray>Decline & go back to your stats page.")
                                .make()

                            gui.item(3, accept.make()).onClick runnable@ {
                                statsPlayer.diamondsMined = 0
                                statsPlayer.ironMined = 0
                                statsPlayer.goldMined = 0
                                statsPlayer.gamesPlayed = 0
                                statsPlayer.kills = 0
                                statsPlayer.arenaKills = 0
                                statsPlayer.arenaDeaths = 0
                                statsPlayer.highestArenaKs = 0
                                statsPlayer.wins = 0
                                statsPlayer.deaths = 0
                                statsPlayer.damageDealt = 0.0
                                statsPlayer.damageTaken = 0.0
                                statsPlayer.bowShots = 0
                                statsPlayer.bowMisses = 0
                                statsPlayer.bowHits = 0
                                statsPlayer.meleeHits = 0
                                statsPlayer.gapplesCrafted = 0
                                statsPlayer.gapplesEaten = 0
                                statsPlayer.timesCrafted = 0
                                statsPlayer.timesEnchanted = 0
                                statsPlayer.timesNether = 0
                                Kraftwerk.instance.statsHandler.savePlayerData(statsPlayer)

                                sender.closeInventory()
                                sender.sendTitle(Chat.colored("&4RESET STATS!"), Chat.colored("<gray>Your statistics have been reset!"))
                                Chat.sendMessage(sender, "${Chat.prefix} Your stats have been reset!")
                                sender.playSound(sender.location, Sound.ANVIL_LAND, 1f, 1f)
                            }
                            gui.item(5, decline).onClick runnable@ {
                                Bukkit.dispatchCommand(sender, "stats")
                            }
                            sender.openInventory(gui.make())
                        } else {
                            Chat.sendMessage(sender, "<red>&2Donator <red>ranks can reset their stats. Buy it at the store <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config tough tits"}")
                        }
                    }
                }
                sender.openInventory(gui.make())
            }

        return true
    }

}
package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder


class ConfigCommand : CommandExecutor {

    private fun getOption(option: String): String {
        val op = ConfigFeature.instance.data!!.getString("game.options.${option}").toBoolean()
        return if (op) {
            "<green>Enabled"
        } else {
            "<red>Disabled"
        }
    }

    private fun getFireWeapons(): String {
        val op = ConfigFeature.instance.data!!.getString("game.options.fireweapons").toBoolean()
        return if (op) {
            "<green>Enabled"
        } else {
            "<yellow>From Books Only"
        }
    }

    private fun getNether(option: String): String {
        val op = ConfigFeature.instance.data!!.getString("game.nether.${option}").toBoolean()
        return if (op) {
            "<green>Enabled"
        } else {
            "<red>Disabled"
        }
    }

    private fun getSpecials(option: String): String {
        val op = ConfigFeature.instance.data!!.getString("game.specials.${option}").toBoolean()
        return if (op) {
            "<green>Enabled"
        } else {
            "<red>Disabled"
        }
    }

    private fun getEventTime(event: String): Int {
        return ConfigFeature.instance.data!!.getInt("game.events.${event}")
    }

    private fun getRule(rule: String): String {
        val op = ConfigFeature.instance.data!!.getString("game.rules.${rule}").toBoolean()
        return if (op) {
            "<green>Allowed"
        } else {
            "<red>Not Allowed"
        }
    }

    private fun getFinalBorder(): Int {
        val op = ScenarioHandler.getScenario("bigcrack")!!.enabled
        return if (op) {
            75
        } else {
            25
        }
    }

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        val gui = GuiBuilder().rows(3).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}&lGame Configuration")).owner(sender)

        sender.sendMessage(Chat.colored("${Chat.prefix} Opening the UHC configuration..."))
        val options = ItemBuilder(Material.LAVA_BUCKET)
            .name(" ${Chat.primaryColor}&lGeneral Settings ")
            .addLore(" ")
            .addLore(" <gray>Horses ${Chat.dash} ${Chat.secondaryColor}${getOption("horses")} ")
            .addLore(" <gray>Starter Food ${Chat.dash} ${Chat.secondaryColor}${ConfigFeature.instance.data!!.getInt("game.starterfood")} ")
            .addLore(" <gray>Permaday ${Chat.dash} ${Chat.secondaryColor}${getOption("permaday")} ")
            .addLore(" ")
            .addLore(" <gray>Statless ${Chat.dash} ${Chat.secondaryColor}${getOption("statless")} ")
            .addLore(" <gray>Double Arrows ${Chat.dash} ${Chat.secondaryColor}${getOption("doublearrows")}")
            .addLore(" <gray>Death Lightning ${Chat.dash} ${Chat.secondaryColor}${getOption("deathlightning")}")
            .addLore(" ")
            .addLore(" <gray>Pearl Damage ${Chat.dash} ${Chat.secondaryColor}${getOption("pearldamage")} ")
            .addLore(" <gray>Pearl Cooldown ${Chat.dash} ${Chat.secondaryColor}${getOption("pearlcooldown")} ")
            .addLore(" ")
            .make()
        gui.item(3, options).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig options")
            }
        }
        val ign = if (ConfigFeature.instance.data!!.getString("game.host") == null) {
            "minota"
        } else {
            ConfigFeature.instance.data!!.getString("game.host")
        }
        val host = ItemBuilder(Material.PLAYER_HEAD)
            .toSkull()
            .setOwner(ign)
            .name(" ${Chat.primaryColor}&lHost ")
            .addLore(" ")
            .addLore(" <gray>Host ${Chat.dash} ${Chat.secondaryColor}${ConfigFeature.instance.data!!.getString("game.host")} ")
            .addLore(" ")
            .addLore(" <gray>Matchpost ${Chat.dash} ${Chat.secondaryColor}https://hosts.uhc.gg/m/${ConfigFeature.instance.data!!.getInt("matchpost.id")} ")
            .addLore(" <gray>Game ${Chat.dash} ${Chat.secondaryColor}${ConfigFeature.instance.data!!.getString("matchpost.host")} ")
            .addLore(" ")
            .make()
        gui.item(4, host).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig host")
            }
            Chat.sendMessage(sender, "${Chat.prefix} Matchpost: ${Chat.secondaryColor}https://hosts.uhc.gg/m/${ConfigFeature.instance.data!!.getInt("matchpost.id")} ")
        }
        val events = ItemBuilder(Material.CLOCK)
            .name(" &4&lEvents")
            .addLore(" ")
            .addLore(" <gray>Final Heal is given in ${Chat.secondaryColor}${getEventTime("final-heal")} minutes ")
            .addLore(" <gray>PvP is enabled in ${Chat.secondaryColor}${getEventTime("pvp") + getEventTime("final-heal")} minutes ")
            .addLore(" <gray>The border begins shrinking in ${Chat.secondaryColor}${getEventTime("borderShrink") + getEventTime("pvp") + getEventTime("final-heal")} minutes ")
            .addLore(" <gray>Meetup is in ${Chat.secondaryColor}${getEventTime("meetup") + getEventTime("pvp") + getEventTime("final-heal")} minutes ")

            .addLore(" ")
            .make()
        gui.item(5, events).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig events")
            }
        }
        val healConfig = ItemBuilder(Material.GOLDEN_APPLE)
            .name(" &4&lHealing Config")
            .addLore(" ")
            .addLore(" <gray>Absorption ${Chat.dash} <gray>${getOption("absorption")} ")
            .addLore(" ")
            .addLore(" <gray>Notch Apples ${Chat.dash} <gray>${getOption("notchapples")} ")
            .addLore(" <gray>Golden Heads ${Chat.dash} <gray>${getOption("goldenheads")} <dark_gray>(<green>4 ❤<dark_gray>) ")
            .addLore(" ")
            .make()
        gui.item(10, healConfig).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig options")
            }
        }
        val caneRates = if (ConfigFeature.instance.worlds!!.getInt("${ConfigFeature.instance.data!!.get("pregen.world")}.canerate") <= 0) {
            "Vanilla"
        } else {
            "${ConfigFeature.instance.worlds!!.get("${ConfigFeature.instance.data!!.get("pregen.world")}.canerate")}% Increased"
        }
        val ratesConfig = ItemBuilder(Material.FLINT)
            .name(" &4&lRates Config")
            .addLore(" ")
            .addLore(" <gray>Apple Rates ${Chat.dash} <green>${ConfigFeature.instance.data!!.getInt("game.rates.apple")}% ")
            .addLore(" <gray>Flint Rates ${Chat.dash} <green>${ConfigFeature.instance.data!!.getInt("game.rates.flint")}% ")
            .addLore(" ")
            .addLore(" <gray>Sugar Cane Rates ${Chat.dash} <green>${caneRates} ")
            .addLore(" ")
            .make()
        gui.item(11, ratesConfig).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig rates")
            }
        }
        val ffa = if (ConfigFeature.instance.data!!.getBoolean("game.ffa")) {
            "<red>Disabled"
        } else {
            "<green>Enabled"
        }
        val teamConfig = ItemBuilder(Material.IRON_SWORD)
            .setAmount(ConfigFeature.instance.data!!.getInt("game.teamSize"))
            .name(" ${Chat.primaryColor}&lTeam Config")
            .noAttributes()
            .addLore("")
        if (ConfigFeature.instance.data!!.getString("matchpost.team") == "Auctions") {
            teamConfig.addLore(" <gray>Team Size ${Chat.dash} ${Chat.secondaryColor}Auctions ")
        } else {
            if (ConfigFeature.instance.data!!.getInt("game.teamSize") == 1) {
                teamConfig.addLore(" <gray>Team Size ${Chat.dash} ${Chat.secondaryColor}FFA ")
            } else {
                teamConfig.addLore(" <gray>Team Size ${Chat.dash} ${Chat.secondaryColor}To${ConfigFeature.instance.data!!.getInt("game.teamSize")} ")
            }
        }
        teamConfig.addLore(" <gray>Team Management ${Chat.dash} ${Chat.secondaryColor}${ffa} ").addLore(" ")
        teamConfig.addLore(" <gray>Friendly Fire ${Chat.dash} ${Chat.secondaryColor}${if (ConfigFeature.instance.data!!.getBoolean("game.friendlyFire")) "<green>Enabled" else "<red>Disabled"}")
        teamConfig.addLore(" ")
        val teamConf = teamConfig.make()
        gui.item(12, teamConf).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig teams")
            }
        }
        val scenarios = ItemBuilder(Material.EMERALD)
            .name(" ${Chat.primaryColor}&lScenarios ")
            .addLore(" ")
            .addLore(" <gray>Scenarios <dark_gray>(<yellow>${ScenarioHandler.getActiveScenarios().size}<dark_gray>) ${Chat.dash} ")
        for (scenario in ScenarioHandler.getActiveScenarios()) {
            scenarios.addLore("  <dark_gray>• ${Chat.secondaryColor}${scenario.name}")
        }
        scenarios.addLore(" ")
        gui.item(13, scenarios.make()).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "sm")
            }
        }
        val enchanting = ItemBuilder(Material.ENCHANTING_TABLE)
            .name(" ${Chat.primaryColor}&lEnchanting Config ")
            .addLore(" ")
            .addLore(" <gray>Enchanting ${Chat.dash} <yellow>1.8 ")
            .addLore("")
            .addLore(" <gray>Split Enchants ${Chat.dash} ${getOption("splitenchants")} ")
            .addLore(" <gray>Bookshelves ${Chat.dash} ${getOption("bookshelves")} ")
            .addLore(" <gray>Fire Weapons ${Chat.dash} ${getFireWeapons()} ")
            .addLore(" ")
            .make()
        gui.item(14, enchanting).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig options")
            }
        }
        val border = ItemBuilder(Material.BEDROCK)
            .name(" ${Chat.primaryColor}&lBorder Config ")
            .addLore(" ")
            .addLore(" <gray>Size ${Chat.dash} ${Chat.secondaryColor}${ConfigFeature.instance.data!!.getInt("pregen.border") * 2}x${ConfigFeature.instance.data!!.getInt("pregen.border") * 2} <dark_gray>(${Chat.secondaryColor}±${ConfigFeature.instance.data!!.getInt("pregen.border")}<dark_gray>) ")
            .addLore(" ")
            .addLore(" <gray>The border shrinks every <yellow>5 minutes<gray>. ")
            .addLore(" <gray>The first shrink will be ${Chat.secondaryColor}1000x1000 (${Chat.secondaryColor}±500)<gray>. ")
            .addLore(" <gray>The last shrink will be ${Chat.secondaryColor}${getFinalBorder()*2}x${getFinalBorder()*2} (${Chat.secondaryColor}±${getFinalBorder()})<gray>. ")
            .addLore(" ")
            .make()
        gui.item(15, border).onClick runnable@ {
            it.isCancelled = true
        }
        val goldRates = if (ConfigFeature.instance.worlds!!.getInt("${ConfigFeature.instance.data!!.get("pregen.world")}.orerates.gold") <= 0) {
            "<green>Vanilla"
        } else {
            "&6${ConfigFeature.instance.worlds!!.get("${ConfigFeature.instance.data!!.get("pregen.world")}.orerates.gold")}% Removed"
        }
        val diaRates = if (ConfigFeature.instance.worlds!!.getInt("${ConfigFeature.instance.data!!.get("pregen.world")}.orerates.diamond") <= 0) {
            "<green>Vanilla"
        } else {
            "&b${ConfigFeature.instance.worlds!!.get("${ConfigFeature.instance.data!!.get("pregen.world")}.orerates.diamond")}% Removed"
        }

        val caveRates = ConfigFeature.instance.worlds!!.getInt("${ConfigFeature.instance.data!!.get("pregen.world")}.caveRates")
        val message = if (caveRates == 7) {
            "Vanilla"
        } else {
            val increase = 7.0 / caveRates
            val rounded = (increase * 10).toInt() / 10.0  // round to 1 decimal place
            "${rounded}x Increased"
        }
        val miningConfig = ItemBuilder(Material.DIAMOND_PICKAXE)
            .name(" &4&lMining Config ")
            .noAttributes()
            .addLore(" ")
            .addLore(" <gray>F5 Abuse ${Chat.dash} ${getRule("f5abuse")} ")
            .addLore(" <gray>Anti-Stone ${Chat.dash} ${Chat.secondaryColor}${getOption("antistone")} ")
            .addLore(" <gray>Anti-Burn ${Chat.dash} ${Chat.secondaryColor}${getOption("antiburn")} ")
            .addLore(" ")
            .addLore(" <gray>Cave Rates ${Chat.dash} ${Chat.secondaryColor}${message} ")
            .addLore(" <gray>Diamond Ore Rates ${Chat.dash} ${Chat.secondaryColor}${diaRates} ")
            .addLore(" <gray>Gold Ore Rates ${Chat.dash} ${Chat.secondaryColor}${goldRates} ")
            .addLore(" <gray>Ores Outside Caves ${Chat.dash} ${Chat.secondaryColor}${if (ConfigFeature.instance.worlds!!.getBoolean("${ConfigFeature.instance.data!!.get("pregen.world")}.oresOutsideCaves")) "<green>Enabled" else "<red>Disabled"} ")
            .addLore(" ")
            .addLore(" <gray>Stripmining ${Chat.dash} ${Chat.secondaryColor}${getRule("stripmining")} ")
            .addLore(" <gray>Rollercoastering ${Chat.dash} ${Chat.secondaryColor}${getRule("rollarcoastering")} ")
            .addLore(" <gray>Pokeholing ${Chat.dash} ${Chat.secondaryColor}${getRule("pokeholing")}")
            .addLore(" ")
            .make()
        gui.item(16, miningConfig).onClick runnable@ {
            it.isCancelled = true
            if (sender.hasPermission("uhc.staff")) {
                Bukkit.dispatchCommand(sender, "editconfig rules")
            }
        }
        val netherConfig = ItemBuilder(Material.NETHERRACK)
            .name(" &4&lNether Config ")
            .addLore(" ")
            .addLore(" <gray>Nether ${Chat.dash} ${Chat.secondaryColor}${getNether("nether")} ")
            .addLore(" <gray>Nerfed Quartz ${Chat.dash} ${Chat.secondaryColor}${getNether("nerfedquartz")} ")
            .addLore(" ")
            .addLore(" <gray>Tier II Potions ${Chat.dash} ${Chat.secondaryColor}${getNether("tierii")} ")
            .addLore(" <gray>Strength Potions ${Chat.dash} ${Chat.secondaryColor}${getNether("strengthpotions")} ")
            .addLore(" <gray>Splash Potions ${Chat.dash} ${Chat.secondaryColor}${getNether("splashpotions")} ")
            .addLore(" ")
            .addLore(" <gray>Portal Trapping ${Chat.dash} ${Chat.secondaryColor}${getRule("portaltrapping")} ")
            .addLore(" <gray>Portal Camping ${Chat.dash} ${Chat.secondaryColor}${getRule("portalcamping")} ")
            .addLore(" ")
            .make()
        val editConfig = ItemBuilder(Material.COMMAND_BLOCK)
            .name(" &4&lEdit Config ")
            .addLore(" ")
            .addLore(" <gray>Click here to edit the UHC configuration. ")
            .addLore(" ")
            .make()

        val pvpConfig = ItemBuilder(Material.WRITABLE_BOOK)
            .name(" &4&lPvP/Meetup Config ")
            .addLore(" ")
            .addLore(" <gray>Stalking ${Chat.dash} ${Chat.secondaryColor}${getRule("stalking")}")
            .addLore(" <gray>Stealing ${Chat.dash} ${Chat.secondaryColor}${getRule("stealing")}")
            .addLore(" ")
            .addLore(" <gray>Crossteaming ${Chat.dash} ${Chat.secondaryColor}${getRule("crossteaming")}")
            .addLore(" <gray>Scumballing/Crossteam Killing ${Chat.dash} ${Chat.secondaryColor}${getRule("scumballing")}")
            .addLore(" <gray>Team Killing ${Chat.dash} ${Chat.secondaryColor}${getRule("teamkilling")}")
            .addLore(" <gray>iPvP ${Chat.dash} <red>Not Allowed")
            .addLore(" ")
            .addLore(" <gray>Skybasing ${Chat.dash} ${Chat.secondaryColor}${getRule("skybasing")}")
            .addLore(" <gray>Running At Meetup ${Chat.dash} ${Chat.secondaryColor}${getRule("runningatmu")}")
            .addLore(" ")
            .make()
        val specialsConfig = ItemBuilder(Material.BLAZE_POWDER)
            .name(" &4&lSpecial Options ")
            .addLore(" ")
            .addLore(" <gray>Fire Resistance before PvP ${Chat.dash} ${Chat.secondaryColor}${getSpecials("frbp")} ")
            .addLore(" <gray>Absorption before PvP ${Chat.dash} ${Chat.secondaryColor}${getSpecials("abp")} ")
            .addLore(" <gray>Block Decay at Meetup ${Chat.dash} ${Chat.secondaryColor}${getSpecials("meetupblockdecay")} ")
            .addLore(" <gray>Permaday at Meetup ${Chat.dash} ${Chat.secondaryColor}${getSpecials("permadayatmeetup")}")
            .addLore(" ")
            .make()
        val privateRoundConfig = ItemBuilder(Material.NAME_TAG)
            .name(" &4&lPrivate Options")
            .addLore(" ")
            .addLore(" <gray>Private Mode ${Chat.dash} ${Chat.secondaryColor}${getOption("private")}" )
            .addLore(" <gray>No Branding ${Chat.dash} ${Chat.secondaryColor}${getOption("noBranding")} ")
            .addLore(" <gray>Custom Branding ${Chat.dash} ${Chat.secondaryColor}${ConfigFeature.instance.data!!.getString("game.options.customBranding")} ")
            .addLore(" ")
            .addLore(" <gray>RR Mode ${Chat.dash} ${Chat.secondaryColor}${getOption("recordedRound")}")
            .addLore(" <gray>Time between Episodes ${Chat.dash} ${Chat.secondaryColor}${ConfigFeature.instance.data!!.getInt("episodeTimer")} ")
            .addLore(" <gray>Episodes ${Chat.dash} ${Chat.secondaryColor}${ConfigFeature.instance.data!!.getInt("episodeCount")} ")
            .addLore(" ")
            .make()

        if (sender.hasPermission("uhc.staff")) {
            gui.item(21, editConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig")
                }
            }
            gui.item(23, specialsConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig specials")
                }
            }
            gui.item(20, pvpConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig rules")
                }
            }
            gui.item(24, netherConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig nether")
                }
            }
            gui.item(22, privateRoundConfig).onClick runnable@ {
                it.isCancelled = true
                if (sender.hasPermission("uhc.staff")) {
                    Bukkit.dispatchCommand(sender, "editconfig options")
                }
            }
        } else {
            if (ConfigFeature.instance.data!!.getBoolean("game.options.private") == null || ConfigFeature.instance.data!!.getBoolean("game.options.private") == false) {
                gui.item(21, pvpConfig).onClick runnable@ {
                    it.isCancelled = true
                    if (sender.hasPermission("uhc.staff")) {
                        Bukkit.dispatchCommand(sender, "editconfig rules")
                    }
                }
                gui.item(23, netherConfig).onClick runnable@ {
                    it.isCancelled = true
                    if (sender.hasPermission("uhc.staff")) {
                        Bukkit.dispatchCommand(sender, "editconfig nether")
                    }
                }
                gui.item(22, specialsConfig).onClick runnable@ {
                    it.isCancelled = true
                    if (sender.hasPermission("uhc.staff")) {
                        Bukkit.dispatchCommand(sender, "editconfig specials")
                    }
                }
            } else {
                gui.item(20, pvpConfig).onClick runnable@ {
                    it.isCancelled = true
                    if (sender.hasPermission("uhc.staff")) {
                        Bukkit.dispatchCommand(sender, "editconfig rules")
                    }
                }
                gui.item(23, netherConfig).onClick runnable@ {
                    it.isCancelled = true
                    if (sender.hasPermission("uhc.staff")) {
                        Bukkit.dispatchCommand(sender, "editconfig nether")
                    }
                }
                gui.item(24, specialsConfig).onClick runnable@ {
                    it.isCancelled = true
                    if (sender.hasPermission("uhc.staff")) {
                        Bukkit.dispatchCommand(sender, "editconfig specials")
                    }
                }
                gui.item(21, privateRoundConfig).onClick runnable@ {
                    it.isCancelled = true
                    if (sender.hasPermission("uhc.staff")) {
                        Bukkit.dispatchCommand(sender, "editconfig options")
                    }
                }
            }

        }



        sender.playSound(sender.location, Sound.ENTITY_PLAYER_LEVELUP, 10.toFloat(), 10.toFloat())
        sender.openInventory(gui.make())
        return true
    }

}
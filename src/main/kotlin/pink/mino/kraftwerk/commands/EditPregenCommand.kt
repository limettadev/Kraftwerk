package pink.mino.kraftwerk.commands

import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder

class EditPregenCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.pregen")) {
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        } else {
            Chat.sendMessage(sender, "${ChatColor.RED}You must be a player to use this command.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} <gray>Usage: ${Chat.secondaryColor}/editpregen <border/generation/settings><gray>.")
            return false
        }
        val pregenConfig = PregenConfigHandler.getConfig(sender as OfflinePlayer)
        if (pregenConfig == null) {
            Chat.sendMessage(sender, "${Chat.prefix} <gray>You don't have a pregeneration configuration set up yet.")
            return false
        }
        val gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit Pregen Config")).owner(sender)
        if (args[0] == "border") {
            val border = ItemBuilder(Material.BEDROCK)
                .name("<gray>Border: ${Chat.primaryColor}±${pregenConfig.border}")
                .addLore("<gray>Click to change the border size.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click: <green>+50")
                .addLore("<dark_gray>Right Click: <red>-50")
                .addLore(" ")
                .make()
            gui.item(4, border).onClick {
                it.isCancelled = true
                if (it.isLeftClick) {
                    pregenConfig.border += 50
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Border: ${Chat.primaryColor}±${pregenConfig.border}"))
                    it.currentItem!!.itemMeta = meta
                } else {
                    pregenConfig.border -= 50
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Border: ${Chat.primaryColor}±${pregenConfig.border}"))
                    it.currentItem!!.itemMeta = meta
                }
            }
        } else if (args[0] == "generation") {
            val type = ItemBuilder(Material.GRASS_BLOCK)
                .name("<gray>World Environment: ${Chat.primaryColor}${pregenConfig.type}")
                .addLore("<gray>Click to change the generation type.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click <gray>to toggle between the types.")
                .addLore(" ")
                .make()
            gui.item(3, type).onClick {
                it.isCancelled = true
                when (pregenConfig.type) {
                    World.Environment.NORMAL -> {
                        pregenConfig.type = World.Environment.NETHER
                        it.currentItem!!.type = Material.NETHERRACK
                    }
                    World.Environment.NETHER -> {
                        pregenConfig.type = World.Environment.THE_END
                        it.currentItem!!.type = Material.END_STONE
                    }
                    World.Environment.THE_END -> {
                        pregenConfig.type = World.Environment.NORMAL
                        it.currentItem!!.type = Material.GRASS_BLOCK
                    }
                    else -> {}
                }
                val meta = it.currentItem!!.itemMeta
                meta.displayName(Chat.colored("<gray>World Environment: ${Chat.primaryColor}${pregenConfig.type}"))
                it.currentItem!!.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} <gray>World environment set to ${Chat.primaryColor}${pregenConfig.type.name.uppercase()}<gray>.")
            }
            val generator = ItemBuilder(Material.DIAMOND_BLOCK)
                .name("<gray>Generator: ${Chat.primaryColor}${pregenConfig.generator}")
                .addLore("<gray>Click to change the generator type.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click <gray>to toggle between the generator types.")
                .addLore(" ")
                .make()
            gui.item(5, generator).onClick {
                it.isCancelled = true
                when (pregenConfig.generator) {
                    PregenerationGenerationTypes.NONE -> {
                        pregenConfig.generator = PregenerationGenerationTypes.CITY_WORLD
                    }
                    PregenerationGenerationTypes.CITY_WORLD -> {
                        pregenConfig.generator = PregenerationGenerationTypes.NONE
                    }
                }
                val meta = it.currentItem!!.itemMeta
                meta.displayName(Chat.colored("<gray>Generator: ${Chat.primaryColor}${pregenConfig.generator}"))
                it.currentItem!!.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} <gray>Generator set to ${Chat.primaryColor}${pregenConfig.generator.name.uppercase()}<gray>.")
            }
        } else if (args[0] == "settings") {
            val oresOutsideCaves = ItemBuilder(Material.DIAMOND_PICKAXE)
                .name("<gray>Ores Outside Caves: ${Chat.primaryColor}${pregenConfig.oresOutsideCaves}")
                .addLore("<gray>Click to toggle spawning ores outside caves.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click <gray>to toggle spawning ores outside caves.")
                .addLore(" ")
                .make()
            val clearTrees = ItemBuilder(Material.OAK_LEAVES)
                .name("<gray>Clear Trees: ${Chat.primaryColor}${pregenConfig.clearTrees}")
                .addLore("<gray>Click to toggle clearing trees.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click <gray>to toggle clearing trees.")
                .addLore(" ")
                .make()
            val clearWater = ItemBuilder(Material.WATER_BUCKET)
                .name("<gray>Clear Water: ${Chat.primaryColor}${pregenConfig.clearWater}")
                .addLore("<gray>Click to toggle clearing water.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click <gray>to toggle clearing water.")
                .addLore(" ")
                .make()
            val diaRates = ItemBuilder(Material.DIAMOND_ORE)
                .name("<gray>Diamond Ore Rates: ${Chat.primaryColor}${pregenConfig.diamondore}% Removed")
                .addLore("<gray>Click to change the diamond ore rates.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click: <green>+5")
                .addLore("<dark_gray>Right Click: <red>-5")
                .addLore(" ")
                .make()
            val goldRates = ItemBuilder(Material.GOLD_ORE)
                .name("<gray>Gold Ore Rates: ${Chat.primaryColor}${pregenConfig.goldore}% Removed")
                .addLore("<gray>Click to change the gold ore rates.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click: <green>+5")
                .addLore("<dark_gray>Right Click: <red>-5")
                .addLore(" ")
                .make()
            val caneRates = ItemBuilder(Material.SUGAR_CANE)
                .name("<gray>Cane Rates: ${Chat.primaryColor}${pregenConfig.canerate}% Increased")
                .addLore("<gray>Click to change the cane rates.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click: <green>+5")
                .addLore("<dark_gray>Right Click: <red>-5")
                .addLore(" ")
                .make()
            val caveRates = ItemBuilder(Material.COBBLESTONE)
                .name("<gray>Cave Frequency: 1 in ${Chat.primaryColor}${pregenConfig.caveRate}")
                .addLore("<gray>Click to change the cave rates.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click: <green>+1")
                .addLore("<dark_gray>Right Click: <red>-1")
                .addLore(" ")
                .make()

            gui.item(1, oresOutsideCaves).onClick {
                it.isCancelled = true
                pregenConfig.oresOutsideCaves = !pregenConfig.oresOutsideCaves
                val meta = it.currentItem!!.itemMeta
                meta.displayName(Chat.colored("<gray>Ores Outside Caves: ${Chat.primaryColor}${pregenConfig.oresOutsideCaves}"))
                it.currentItem!!.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} <gray>Ores outside caves set to ${Chat.primaryColor}${pregenConfig.oresOutsideCaves}<gray>.")
            }
            gui.item(2, clearTrees).onClick {
                it.isCancelled = true
                pregenConfig.clearTrees = !pregenConfig.clearTrees
                val meta = it.currentItem!!.itemMeta
                meta.displayName(Chat.colored("<gray>Clear Trees: ${Chat.primaryColor}${pregenConfig.clearTrees}"))
                it.currentItem!!.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} <gray>Clear trees set to ${Chat.primaryColor}${pregenConfig.clearTrees}<gray>.")
            }
            gui.item(3, clearWater).onClick {
                it.isCancelled = true
                pregenConfig.clearWater = !pregenConfig.clearWater
                val meta = it.currentItem!!.itemMeta
                meta.displayName(Chat.colored("<gray>Clear Water: ${Chat.primaryColor}${pregenConfig.clearWater}"))
                it.currentItem!!.itemMeta = meta
                Chat.sendMessage(sender, "${Chat.prefix} <gray>Clear water set to ${Chat.primaryColor}${pregenConfig.clearWater}<gray>.")
            }
            gui.item(4, diaRates).onClick {
                it.isCancelled = true
                if (it.isLeftClick) {
                    pregenConfig.diamondore += 5
                    if (pregenConfig.diamondore > 100) {
                        pregenConfig.diamondore = 100
                    }
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Diamond Ore Rates: ${Chat.primaryColor}${pregenConfig.diamondore}% Removed"))
                    it.currentItem!!.itemMeta = meta
                } else {
                    pregenConfig.diamondore -= 5
                    if (pregenConfig.diamondore < 0) {
                        pregenConfig.diamondore = 0
                    }
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Diamond Ore Rates: ${Chat.primaryColor}${pregenConfig.diamondore}% Removed"))
                    it.currentItem!!.itemMeta = meta
                }
            }
            gui.item(5, goldRates).onClick {
                it.isCancelled = true
                if (it.isLeftClick) {
                    pregenConfig.goldore += 5
                    if (pregenConfig.goldore > 100) {
                        pregenConfig.goldore = 100
                    }
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Gold Ore Rates: ${Chat.primaryColor}${pregenConfig.goldore}% Removed"))
                    it.currentItem!!.itemMeta = meta
                } else {
                    pregenConfig.goldore -= 5
                    if (pregenConfig.goldore < 0) {
                        pregenConfig.goldore = 0
                    }
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Gold Ore Rates: ${Chat.primaryColor}${pregenConfig.goldore}% Removed"))
                    it.currentItem!!.itemMeta = meta
                }
            }
            gui.item(6, caneRates).onClick {
                it.isCancelled = true
                if (it.isLeftClick) {
                    pregenConfig.canerate += 5
                    if (pregenConfig.canerate > 100) {
                        pregenConfig.canerate = 100
                    }
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Cane Rates: ${Chat.primaryColor}${pregenConfig.canerate}% Increased"))
                    it.currentItem!!.itemMeta = meta
                } else {
                    pregenConfig.canerate -= 5
                    if (pregenConfig.canerate < 0) {
                        pregenConfig.canerate = 0
                    }
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Cane Rates: ${Chat.primaryColor}${pregenConfig.canerate}% Increased"))
                    it.currentItem!!.itemMeta = meta
                }
            }

            gui.item(7, caveRates).onClick {
                it.isCancelled = true
                if (it.isLeftClick) {
                    pregenConfig.caveRate += 1
                    if (pregenConfig.caveRate > 7) {
                        pregenConfig.caveRate = 7
                    }
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Cave Frequency: 1 in ${Chat.primaryColor}${pregenConfig.caveRate}"))
                    it.currentItem!!.itemMeta = meta
                } else {
                    pregenConfig.caveRate -= 1
                    if (pregenConfig.caveRate < 1) {
                        pregenConfig.caveRate = 1
                    }
                    val meta = it.currentItem!!.itemMeta
                    meta.displayName(Chat.colored("<gray>Cave Frequency: 1 in ${Chat.primaryColor}${pregenConfig.caveRate}"))
                    it.currentItem!!.itemMeta = meta
                }
            }
        }
        val back = ItemStack(Material.ARROW)
        val backMeta = back.itemMeta
        backMeta.displayName(Chat.colored("<red>Back"))
        backMeta.lore(listOf(
            Chat.colored("<gray>Go back to the pregen config menu.")
        ))
        back.itemMeta = backMeta
        gui.item(8, back).onClick runnable@ {
            it.isCancelled = true
            sender.closeInventory()
            Bukkit.getServer().dispatchCommand(sender as CommandSender, "pregen ${pregenConfig.name}")
        }
        sender.openInventory(gui.make())
        return true
    }
}
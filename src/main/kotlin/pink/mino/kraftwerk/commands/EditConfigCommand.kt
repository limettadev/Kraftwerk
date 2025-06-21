package pink.mino.kraftwerk.commands

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.config.ConfigOptionHandler
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder

class EditConfigCommand : CommandExecutor {

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
        val player = sender as Player
        var gui: GuiBuilder? = null
        var size: Int = 35
        if (args.isEmpty()) {
            gui = GuiBuilder().rows(2).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit UHC Config")).owner(sender)
            size = 17
            val rates = ItemBuilder(Material.FLINT)
                .name("${Chat.primaryColor}Rates")
                .addLore("<gray>Click here to edit rates.")
                .make()
            gui.item(0, rates).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig rates")
            }
            val settings = ItemBuilder(Material.LAVA_BUCKET)
                .name("${Chat.primaryColor}Settings")
                .addLore("<gray>Click here to edit general options.")
                .make()
            gui.item(1, settings).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig options")
            }
            val teams = ItemBuilder(Material.DIAMOND_SWORD)
                .name("${Chat.primaryColor}Teams")
                .addLore("<gray>Click here to edit teams.")
                .make()
            gui.item(2, teams).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig teams")
            }
            val starterFood = ItemBuilder(Material.COOKED_BEEF)
                .name("${Chat.primaryColor}Starter Food")
                .addLore("<gray>Click here to edit starter food.")
                .make()
            gui.item(3, starterFood).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig starterfood")
            }
            val host = ItemBuilder(Material.COMPASS)
                .name("${Chat.primaryColor}Host")
                .addLore("<gray>Click here to set yourself as the host.")
                .make()
            gui.item(4, host).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig host")
            }
            val events = ItemBuilder(Material.CLOCK)
                .name("${Chat.primaryColor}Events")
                .addLore("<gray>Click here to edit events.")
                .make()
            gui.item(5, events).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig events")
            }
            val nether = ItemBuilder(Material.NETHER_STAR)
                .name("${Chat.primaryColor}Nether")
                .addLore("<gray>Click here to edit nether options.")
                .make()
            gui.item(6, nether).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig nether")
            }
            val rules = ItemBuilder(Material.PAPER)
                .name("${Chat.primaryColor}Rules")
                .addLore("<gray>Click here to edit rules.")
                .make()
            gui.item(7, rules).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig rules")
            }
            val specials = ItemBuilder(Material.BLAZE_POWDER)
                .name("${Chat.primaryColor}Special Options")
                .addLore("<gray>Click here to edit special options.")
                .make()
            gui.item(8, specials).onClick runnable@ {
                it.isCancelled = true
                player.closeInventory()
                Bukkit.dispatchCommand(player, "editconfig specials")
            }
        } else if (args[0].lowercase() == "options") {
            gui = GuiBuilder().rows(3).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit UHC Config")).owner(sender)
            size = 17
            var iterator = 0
            for (option in ConfigOptionHandler.configOptions) {
                if (option.category === "options") {
                    val item = ItemStack(option.material)
                    val itemMeta = item.itemMeta
                    var color: String = if (option.enabled) "<green>"
                    else "<red>"
                    itemMeta.displayName(MiniMessage.miniMessage().deserialize("${color}${option.name}"))
                    itemMeta.lore(Chat.scenarioTextWrap("<gray>${option.description}", 40).toList())
                    item.itemMeta = itemMeta
                    gui.item(iterator, item).onClick runnable@ {
                        it.isCancelled = true
                        ConfigOptionHandler.getOption(option.id)?.toggle()
                        color = if (option.enabled) "<green>"
                        else "<red>"
                        val meta = it.currentItem!!.itemMeta
                        meta.displayName(MiniMessage.miniMessage().deserialize("${color}${option.name}"))
                        it.currentItem!!.itemMeta = meta
                    }
                    iterator++
                }
            }
        } else if (args[0].lowercase() == "nether") {
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit UHC Config")).owner(sender)
            size = 8
            var iterator = 0
            for (option in ConfigOptionHandler.configOptions) {
                if (option.category === "nether") {
                    val item = ItemStack(option.material)
                    val itemMeta = item.itemMeta
                    var color: String = if (option.enabled) "<green>"
                    else "<red>"
                    itemMeta.displayName(MiniMessage.miniMessage().deserialize("${color}${option.name}"))
                    itemMeta.lore(Chat.scenarioTextWrap("<gray>${option.description}", 40))
                    item.itemMeta = itemMeta
                    gui.item(iterator, item).onClick runnable@ {
                        it.isCancelled = true
                        ConfigOptionHandler.getOption(option.id)?.toggle()
                        color = if (option.enabled) "<green>"
                        else "<red>"
                        val meta = it.currentItem!!.itemMeta
                        meta.displayName(MiniMessage.miniMessage().deserialize("${color}${option.name}"))
                        it.currentItem!!.itemMeta = meta
                    }
                    iterator++
                }
            }
        } else if (args[0].lowercase() == "rules") {
            gui = GuiBuilder().rows(2).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit UHC Config")).owner(sender)
            size = 17
            var iterator = 0
            for (option in ConfigOptionHandler.configOptions) {
                if (option.category === "rules") {
                    val item = ItemStack(option.material)
                    val itemMeta = item.itemMeta
                    var color: String = if (option.enabled) "<green>"
                    else "<red>"
                    itemMeta.displayName(MiniMessage.miniMessage().deserialize("${color}${option.name}"))
                    itemMeta.lore(Chat.scenarioTextWrap("<gray>${option.description}", 40))
                    item.itemMeta = itemMeta
                    gui.item(iterator, item).onClick runnable@ {
                        it.isCancelled = true
                        ConfigOptionHandler.getOption(option.id)?.toggle()
                        color = if (option.enabled) "<green>"
                        else "<red>"
                        val meta = it.currentItem!!.itemMeta
                        meta.displayName(MiniMessage.miniMessage().deserialize("${color}${option.name}"))
                        it.currentItem!!.itemMeta = meta
                    }
                    iterator++
                }
            }
        } else if (args[0] == "events") {
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit UHC Config")).owner(sender)
            size = 8
            val finalHeal = ItemStack(Material.REDSTONE)
            val pvp = ItemStack(Material.IRON_SWORD)
            val meetup = ItemStack(Material.BEACON)

            val fhMeta = finalHeal.itemMeta
            val pvpMeta = pvp.itemMeta
            val muMeta = meetup.itemMeta
            fhMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Final Heal"))
            pvpMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}PvP"))
            muMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Meetup"))

            fhMeta.lore(listOf(
                Chat.colored("<gray>Final Heal happens in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.final-heal")} minutes<gray>."),
                Chat.colored(""),
                Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
            ))
            pvpMeta.lore(listOf(
                Chat.colored("<gray>PvP happens in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.final-heal")} minutes<gray>."),
                Chat.colored(""),
                Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
            ))
            muMeta.lore(listOf(
                Chat.colored("<gray>Meetup happens in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.meetup")} minutes<gray>."),
                Chat.colored(""),
                Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
            ))

            val bs = ItemBuilder(Material.BEDROCK)
                .name("${Chat.primaryColor}Border Shrinks")
                .addLore("<gray>The border begins to shrink in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.borderShrink")} minutes<gray>.")
                .addLore(" ")
                .addLore("<dark_gray>Left Click<gray> to add <green>one<gray>.")
                .addLore("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                .make()

            finalHeal.itemMeta = fhMeta
            pvp.itemMeta = pvpMeta
            meetup.itemMeta = muMeta
            gui.item(2, finalHeal).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    ConfigFeature.instance.data!!.set("game.events.final-heal", ConfigFeature.instance.data!!.getInt("game.events.final-heal") + 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Final Heal happens in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.final-heal")} minutes<gray>."),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.events.final-heal") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this timer is already at 1 minute.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.events.final-heal", ConfigFeature.instance.data!!.getInt("game.events.final-heal") - 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Final Heal happens in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.final-heal")} minutes<gray>."),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                }
            }
            gui.item(3, pvp).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    ConfigFeature.instance.data!!.set("game.events.pvp", ConfigFeature.instance.data!!.getInt("game.events.pvp") + 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    meta.lore(mutableListOf(
                        Chat.colored("<gray>PvP happens in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.pvp")} minutes<gray>."),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.events.pvp") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this timer is already at 1 minute.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.events.pvp", ConfigFeature.instance.data!!.getInt("game.events.pvp") - 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                    meta.lore(listOf(
                        Chat.colored("<gray>PvP happens in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.pvp")} minutes<gray>."),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                }
            }
            gui.item(4, meetup).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    ConfigFeature.instance.data!!.set("game.events.meetup", ConfigFeature.instance.data!!.getInt("game.events.meetup") + 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Meetup happens in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.meetup")} minutes<gray>."),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.events.meetup") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this timer is already at 1 minute.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.events.meetup", ConfigFeature.instance.data!!.getInt("game.events.meetup") - 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Meetup happens in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.meetup")} minutes<gray>."),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                }
            }
            gui.item(5, bs).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    ConfigFeature.instance.data!!.set("game.events.borderShrink", ConfigFeature.instance.data!!.getInt("game.events.borderShrink") + 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>The border begins to shrink in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.borderShrink")} minutes<gray>."),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.events.borderShrink") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this timer is already at 1 minute.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.events.borderShrink", ConfigFeature.instance.data!!.getInt("game.events.borderShrink") - 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>The border begins to shrink in ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.events.pvp") + ConfigFeature.instance.data!!.getInt("game.events.final-heal") + ConfigFeature.instance.data!!.getInt("game.events.borderShrink")} minutes<gray>."),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                }
            }
        } else if (args[0].lowercase() == "host") {
            ConfigFeature.instance.data!!.set("game.host", player.name)
            ConfigFeature.instance.saveData()
            Bukkit.broadcast(Chat.colored("${Chat.prefix} <yellow>${player.name}<gray> has set themself as the host."))
            player.playSound(player.location, Sound.ENTITY_PLAYER_LEVELUP, 10.toFloat(), 1.toFloat())
            return true
        } else if (args[0].lowercase() == "starterfood") {
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit UHC Config")).owner(sender)
            size = 8
            val starterFood = ItemStack(Material.COOKED_BEEF)
            val starterFoodMeta = starterFood.itemMeta
            starterFoodMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Starter Food"))
            starterFoodMeta.lore(listOf(
                Chat.colored("<gray>Starter Food ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.starterfood")}"),
                Chat.colored(""),
                Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
            ))
            starterFood.itemMeta = starterFoodMeta
            gui.item(4, starterFood).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    ConfigFeature.instance.data!!.set("game.starterfood", ConfigFeature.instance.data!!.getInt("game.starterfood") + 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Starter Food ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.starterfood")}"),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.starterfood") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this count is already at 1 starter food.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.starterfood", ConfigFeature.instance.data!!.getInt("game.starterfood") - 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Starter Food ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.starterfood")}"),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                }
            }
        } else if (args[0].lowercase() == "teams") {
            gui = GuiBuilder().rows(3).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit UHC Config")).owner(sender)
            size = 26
            val teamSize = ItemStack(Material.IRON_SWORD, ConfigFeature.instance.data!!.getInt("game.teamSize"))
            val teamSizeMeta = teamSize.itemMeta
            teamSizeMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Team Size"))
            teamSizeMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            teamSizeMeta.lore(listOf(
                Chat.colored("<gray>Team Size ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.teamSize")}"),
                Chat.colored(""),
                Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
            ))
            teamSize.itemMeta = teamSizeMeta
            gui.item(10, teamSize).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.teamSize") >= 64) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't add, this count is already at 64.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.teamSize", ConfigFeature.instance.data!!.getInt("game.teamSize") + 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    val item = ItemStack(it.currentItem!!.type, ConfigFeature.instance.data!!.getInt("game.teamSize"))
                    meta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Team Size"))
                    meta.lore(listOf(
                        Chat.colored("<gray>Team Size ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.teamSize")}"),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem = item
                    it.currentItem!!.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.teamSize") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't add, this count is already at 1.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.teamSize", ConfigFeature.instance.data!!.getInt("game.teamSize") - 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    val item = ItemStack(it.currentItem!!.type, ConfigFeature.instance.data!!.getInt("game.teamSize"))
                    meta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Team Size"))
                    meta.lore(listOf(
                        Chat.colored("<gray>Team Size ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.teamSize")}"),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem = item
                    it.currentItem!!.itemMeta = meta
                }
            }
            val teamManagement: ItemStack = if (ConfigFeature.instance.data!!.getBoolean("game.ffa")) {
                ItemStack(Material.RED_WOOL, 1)
            } else {
                ItemStack(Material.LIME_WOOL, 1)
            }
            val teamManagementMeta = teamManagement.itemMeta
            teamManagementMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Team Management"))
            var status = if (ConfigFeature.instance.data!!.getBoolean("game.ffa")) {
                "<red>Disabled"
            } else {
                "<green>Enabled"
            }
            teamManagementMeta.lore(listOf(
                Chat.colored("<gray>Status: $status"),
                Chat.colored(""),
                Chat.colored("<dark_gray>Left Click<gray> to toggle.")
            ))
            teamManagement.itemMeta = teamManagementMeta
            gui.item(12, teamManagement).onClick runnable@ {
                it.isCancelled = true
                status = if (ConfigFeature.instance.data!!.getBoolean("game.ffa")) {
                    "<green>Enabled"
                } else {
                    "<red>Disabled"
                }
                if (ConfigFeature.instance.data!!.getBoolean("game.ffa")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team management on")
                } else {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "team management off")
                }
                val wool: ItemStack = if (ConfigFeature.instance.data!!.getBoolean("game.ffa")) {
                    ItemStack(Material.RED_WOOL, 1)
                } else {
                    ItemStack(Material.LIME_WOOL, 1)
                }
                it.currentItem = wool
                val meta = it.currentItem!!.itemMeta
                meta.lore(listOf(
                    Chat.colored("<gray>Status: $status"),
                    Chat.colored(""),
                    Chat.colored("<dark_gray>Left Click<gray> to toggle.")
                ))
                meta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Team Management"))
                it.currentItem!!.itemMeta = meta
            }
            val randomizeTeams = ItemStack(Material.ENDER_EYE)
            val randomizeTeamsMeta = randomizeTeams.itemMeta
            randomizeTeamsMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Randomize Teams"))
            randomizeTeamsMeta.lore(listOf(
                Chat.colored("<gray>Click to randomize all players into teams of ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.teamSize")}<gray>."),
                Chat.colored(""),
                Chat.colored("&4&lWARNING<gray> All players that are not"),
                Chat.colored("<gray>going to play must be in Spectator mode.")
            ))
            randomizeTeams.itemMeta = randomizeTeamsMeta
            gui.item(14, randomizeTeams).onClick runnable@ {
                it.isCancelled = true
                Bukkit.dispatchCommand(player, "team randomize")
            }

            val resetTeams = ItemStack(Material.BARRIER)
            val resetTeamsMeta = randomizeTeams.itemMeta
            resetTeamsMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Reset Teams"))
            resetTeamsMeta.lore(listOf(
                Chat.colored("<gray>Click to reset all teams."),
                Chat.colored(""),
                Chat.colored("&4&lWARNING<gray> This is probably a bad idea!")
            ))
            resetTeams.itemMeta = resetTeamsMeta
            gui.item(16, resetTeams).onClick runnable@ {
                it.isCancelled = true
                Bukkit.dispatchCommand(player, "team reset")
            }
        } else if (args[0].lowercase() == "rates") {
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit UHC Config")).owner(sender)
            size = 8
            val flintRates = ItemStack(Material.FLINT)
            val flintRatesMeta = flintRates.itemMeta
            flintRatesMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Flint Rates"))
            flintRatesMeta.lore(listOf(
                Chat.colored("<gray>Flint Rates ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.rates.flint")}%"),
                Chat.colored(""),
                Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
            ))
            flintRates.itemMeta = flintRatesMeta
            gui.item(3, flintRates).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.rates.flint") >= 100) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't add, this count is already at 100%.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.rates.flint", ConfigFeature.instance.data!!.getInt("game.rates.flint") + 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Flint Rates ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.rates.flint")}%"),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.rates.flint") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this count is already at 1%.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.rates.flint", ConfigFeature.instance.data!!.getInt("game.rates.flint") - 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Flint Rates ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.rates.flint")}%"),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                }
            }
            val appleRates = ItemStack(Material.APPLE)
            val appleRatesMeta = appleRates.itemMeta
            appleRatesMeta.displayName(MiniMessage.miniMessage().deserialize("${Chat.primaryColor}Apple Rates"))
            appleRatesMeta.lore(listOf(
                Chat.colored("<gray>Apple Rates ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.rates.apple")}%"),
                Chat.colored(""),
                Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
            ))
            appleRates.itemMeta = appleRatesMeta
            gui.item(5, appleRates).onClick runnable@ {
                it.isCancelled = true
                if (it.click.isLeftClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.rates.apple") >= 100) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this count is already at 100%.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.rates.apple", ConfigFeature.instance.data!!.getInt("game.rates.apple") + 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Apple Rates ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.rates.apple")}%"),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                } else if (it.click.isRightClick) {
                    if (ConfigFeature.instance.data!!.getInt("game.rates.apple") <= 1) {
                        Chat.sendMessage(player, "${Chat.prefix} Can't subtract, this count is already at 1%.")
                        return@runnable
                    }
                    ConfigFeature.instance.data!!.set("game.rates.apple", ConfigFeature.instance.data!!.getInt("game.rates.apple") - 1)
                    ConfigFeature.instance.saveData()
                    val meta = it.currentItem!!.itemMeta
                    meta.lore(listOf(
                        Chat.colored("<gray>Apple Rates ${Chat.dash} ${Chat.primaryColor}${ConfigFeature.instance.data!!.getInt("game.rates.apple")}%"),
                        Chat.colored(""),
                        Chat.colored("<dark_gray>Left Click<gray> to add <green>one<gray>."),
                        Chat.colored("<dark_gray>Right Click<gray> to subtract <red>one<gray>.")
                    ))
                    it.currentItem!!.itemMeta = meta
                }
            }
        }  else if (args[0].lowercase() == "specials") {
            gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Edit UHC Config")).owner(sender)
            size = 8
            var iterator = 0
            for (option in ConfigOptionHandler.configOptions) {
                if (option.category === "specials") {
                    val item = ItemStack(option.material)
                    val itemMeta = item.itemMeta
                    var color: String = if (option.enabled) "<green>"
                    else "<red>"
                    itemMeta.displayName(MiniMessage.miniMessage().deserialize("${color}${option.name}"))
                    itemMeta.lore(Chat.scenarioTextWrap("<gray>${option.description}", 40))
                    item.itemMeta = itemMeta
                    gui.item(iterator, item).onClick runnable@ {
                        it.isCancelled = true
                        ConfigOptionHandler.getOption(option.id)?.toggle()
                        color = if (option.enabled) "<green>"
                        else "<red>"
                        val meta = it.currentItem!!.itemMeta
                        meta.displayName(MiniMessage.miniMessage().deserialize("${color}${option.name}"))
                        it.currentItem!!.itemMeta = meta
                    }
                    iterator++
                }
            }
        }
        val back = ItemStack(Material.ARROW)
        val backMeta = back.itemMeta
        backMeta.displayName(MiniMessage.miniMessage().deserialize("<red>Back"))
        backMeta.lore(listOf(
            Chat.colored("<gray>Go back to the UHC config editing menu.")
        ))
        back.itemMeta = backMeta
        gui!!.item(size, back).onClick runnable@ {
            it.isCancelled = true
            sender.closeInventory()
            if (args.isEmpty()) {
                Bukkit.getServer().dispatchCommand(player as CommandSender, "uhc")
            } else {
                Bukkit.getServer().dispatchCommand(player as CommandSender, "editconfig")
            }
        }
        player.openInventory(gui.make())
        return true
    }
}
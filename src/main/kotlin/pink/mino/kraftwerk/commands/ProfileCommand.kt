package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.*
import kotlin.math.floor

val availableArenaBlocks = listOf(
    Material.COBBLESTONE,
    Material.DIAMOND_BLOCK,
    Material.EMERALD_BLOCK,
    Material.DIAMOND_ORE,
    Material.EMERALD_ORE,
    Material.BOOKSHELF,
    Material.HAY_BLOCK,
    Material.SANDSTONE,
    Material.RED_SANDSTONE,
    Material.GLASS,
    Material.SEA_LANTERN,
    Material.BEDROCK,
    Material.OBSIDIAN,
    Material.WOOL,
    Material.MOSSY_COBBLESTONE,
    Material.GLOWSTONE
)

class ProfileCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            return false
        }
        val gui = GuiBuilder().rows(3).name("${Chat.primaryColor}&lYour Profile").owner(sender)
        val settings = ItemBuilder(Material.REDSTONE_COMPARATOR)
            .name(" ${Chat.primaryColor}&lSettings")
            .addLore(" ")
            .addLore(" <gray>Click to customize your settings. ")
            .addLore(" ")
            .make()
        val stats = ItemBuilder(Material.DIAMOND)
            .name(" ${Chat.primaryColor}&lStatistics")
            .addLore(" ")
            .addLore(" <gray>Click to view your player stats. ")
            .addLore(" ")
            .make()
        val profile = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!
        val xp = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.xp
        val xpNeeded = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.xpNeeded
        val level = JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(sender.uniqueId)!!.level
        val progress: Double = if (floor((xp / xpNeeded) * 100) > 0) {
            floor((xp / xpNeeded) * 100)
        } else {
            0.0
        }
        val misc = ItemBuilder(Material.NETHER_STAR)
            .name(" ${Chat.primaryColor}&lMisc.")
            .addLore(" ")
            .addLore(" <gray>Your Level ${Chat.dash} <green>${level} <dark_gray>(<green>${progress.toInt()}%<dark_gray>)")
            .addLore(" <gray>Chat Mode ${Chat.dash} ${Chat.secondaryColor}${profile.chatMode}")
            .addLore(" <gray>Coins ${Chat.dash} &6⚜ ${floor(profile.coins)}")
            .addLore(" ")
            .make()
        gui.item(12, misc).onClick runnable@ {
            it.isCancelled = true
        }
        gui.item(11, settings).onClick runnable@ { inventoryClickEvent ->
            val player = inventoryClickEvent.whoClicked as Player
            val gui = GuiBuilder().rows(1).name("${Chat.primaryColor}&lPlayer Settings").owner(player)
            val disableRedstonePickup = ItemBuilder(Material.REDSTONE)
            if (profile.disableRedstonePickup) {
                disableRedstonePickup.name("<green>&lDisable Redstone Pickup")
                    .addLore("<gray>Currently ${Chat.dash} <green>Enabled")
                    .addLore("<dark_gray>&oClick to toggle!")
            } else {
                disableRedstonePickup.name("<red>&lDisable Redstone Pickup")
                    .addLore("<gray>Currently ${Chat.dash} <red>Disabled")
                    .addLore("<dark_gray>&oClick to toggle!")
            }
            val disableLapisPickup = ItemBuilder(Material.INK_SACK)
                .setDurability(4)
            if (profile.disableLapisPickup) {
                disableLapisPickup.name("<green>&lDisable Lapis Pickup")
                    .addLore("<gray>Currently ${Chat.dash} <green>Enabled")
                    .addLore("<dark_gray>&oClick to toggle!")
            } else {
                disableLapisPickup.name("<red>&lDisable Lapis Pickup")
                    .addLore("<gray>Currently ${Chat.dash} <red>Disabled")
                    .addLore("<dark_gray>&oClick to toggle!")
            }
            val projectileMessages = ItemBuilder(Material.ARROW)
                .name("<yellow>&lProjectile Messages")
                .addLore("<gray>Currently ${Chat.dash} ${Chat.secondaryColor}${profile.projectileMessages}")
                .addLore("<dark_gray>&oClick to toggle!")
                .make()
            val borderPreference = ItemBuilder(Material.BEDROCK)
                .name("<yellow>&lBorder Preference")
                .addLore("<gray>Currently ${Chat.dash} ${Chat.secondaryColor}${profile.borderPreference}")
                .addLore("<dark_gray>&oClick to toggle!")
                .make()
            val deathMessageOnScreen = ItemBuilder(Material.BOOK_AND_QUILL)
            if (profile.deathMessageOnScreen) {
                deathMessageOnScreen.name("<green>&lDeath Message on Screen")
                    .addLore("<gray>Currently ${Chat.dash} <green>Enabled")
                    .addLore("<dark_gray>&oClick to toggle!")
            } else {
                deathMessageOnScreen.name("<red>&lDeath Message on Screen")
                    .addLore("<gray>Currently ${Chat.dash} <red>Disabled")
                    .addLore("<dark_gray>&oClick to toggle!")
            }
            gui.item(3, borderPreference).onClick runnable@ {
                if (profile.borderPreference == "RADIUS") {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.borderPreference = "DIAMETER"
                    Chat.sendMessage(player, "${Chat.prefix} Set your projectile messages to <dark_gray>'<yellow>DIAMETER<dark_gray>'<gray>.")
                    val meta = projectileMessages.itemMeta
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} ${Chat.secondaryColor}DIAMETER"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                } else if (profile.borderPreference == "DIAMETER") {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.borderPreference = "RADIUS"
                    Chat.sendMessage(player, "${Chat.prefix} Set your projectile messages to <dark_gray>'<yellow>RADIUS<dark_gray>'<gray>.")
                    val meta = projectileMessages.itemMeta
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} ${Chat.secondaryColor}RADIUS"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(4, deathMessageOnScreen.make()).onClick runnable@ {
                if (profile.deathMessageOnScreen) {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.deathMessageOnScreen = false
                    Chat.sendMessage(player, "${Chat.prefix} <red>Disabled&l death message on screen.")
                    val meta = deathMessageOnScreen.meta
                    meta.displayName = Chat.colored("<red>&lDeath Message on Screen")
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} <red>Disabled"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                } else {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.deathMessageOnScreen = true
                    Chat.sendMessage(player, "${Chat.prefix} <green>Enabled&l death message on screen.")
                    val meta = deathMessageOnScreen.meta
                    meta.displayName = Chat.colored("<green>&lDeath Message on Screen")
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} <green>Enabled"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(2, projectileMessages).onClick runnable@ {
                if (profile.projectileMessages == "CHAT") {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.projectileMessages = "SUBTITLE"
                    Chat.sendMessage(player, "${Chat.prefix} Set your projectile messages to <dark_gray>'<yellow>SUBTITLE<dark_gray>'<gray>.")
                    val meta = projectileMessages.itemMeta
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} ${Chat.secondaryColor}SUBTITLE"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                } else if (profile.projectileMessages == "SUBTITLE") {
                    JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.projectileMessages = "CHAT"
                    Chat.sendMessage(player, "${Chat.prefix} Set your projectile messages to <dark_gray>'<yellow>CHAT<dark_gray>'<gray>.")
                    val meta = projectileMessages.itemMeta
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} ${Chat.secondaryColor}CHAT"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
            }
            gui.item(0, disableRedstonePickup.make()).onClick runnable@ {
                if (!PerkChecker.checkPerks(player).contains(Perk.TOGGLE_PICKUPS)) {
                    Chat.sendMessage(player, "<red>This setting is locked to &2Donator<red> ranks. <red>Buy it at <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config tough tits"}<red>.")
                    return@runnable
                }
                if (profile.disableRedstonePickup) {
                    profile.disableRedstonePickup = false
                    val meta = disableRedstonePickup.meta
                    meta.displayName = Chat.colored("<red>&lDisable Redstone Pickup")
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} <red>Disabled"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                    Chat.sendMessage(player, "${Chat.prefix} Redstone pickup has been <green>enabled<gray>!")
                } else {
                    profile.disableRedstonePickup = true
                    Chat.sendMessage(player, "${Chat.prefix} Redstone pickup has been <red>disabled<gray>!")
                    val meta = disableRedstonePickup.meta
                    meta.displayName = Chat.colored("<green>&lDisable Redstone Pickup")
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} <green>Enabled"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.disableRedstonePickup = profile.disableRedstonePickup
            }
            gui.item(1, disableLapisPickup.make()).onClick runnable@ {
                if (!PerkChecker.checkPerks(player).contains(Perk.TOGGLE_PICKUPS)) {
                    Chat.sendMessage(player, "<red>This setting is locked to &2Donator<red> ranks. <red>Buy it at <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config tough tits"}<red>.")
                    return@runnable
                }
                if (profile.disableLapisPickup) {
                    profile.disableLapisPickup = false
                    Chat.sendMessage(player, "${Chat.prefix} Lapis pickup has been <green>enabled<gray>!")
                    val meta = disableLapisPickup.meta
                    meta.displayName = Chat.colored("<red>&lDisable Lapis Pickup")
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} <red>Disabled"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                } else {
                    profile.disableLapisPickup = true
                    Chat.sendMessage(player, "${Chat.prefix} Lapis pickup has been <red>disabled<gray>!")
                    val meta = disableLapisPickup.meta
                    meta.displayName = Chat.colored("<green>&lDisable Lapis Pickup")
                    meta.lore = listOf(
                        Chat.colored("<gray>Currently ${Chat.dash} <green>Enabled"),
                        Chat.colored("<dark_gray>&oClick to toggle!")
                    )
                    it.currentItem.itemMeta = meta
                }
                JavaPlugin.getPlugin(Kraftwerk::class.java).profileHandler.getProfile(player.uniqueId)!!.disableLapisPickup = profile.disableLapisPickup
            }
            val back = ItemBuilder(Material.BARRIER)
                .name("<red>Back")
                .addLore("Go back to the previous menu.")
                .make()
            gui.item(5, back).onClick runnable@ {
                Bukkit.dispatchCommand(sender, "profile")
            }
            sender.openInventory(gui.make())
        }
        val display = ItemBuilder(Material.REDSTONE)
            .name(" ${Chat.primaryColor}&lDisplay")
            .addLore(" ")
            .addLore(" <gray>Customize certain cosmetic settings for yourself. ")
            .addLore(" ")
            .make()
        gui.item(14, stats).onClick runnable@ {
            Bukkit.dispatchCommand(it.whoClicked as Player, "stats")
        }
        gui.item(15, display).onClick runnable@ {
            val gui = GuiBuilder().rows(3).name("${Chat.primaryColor}&lDisplay Settings").owner(sender)
            val tags = ItemBuilder(Material.NAME_TAG)
                .name(" ${Chat.primaryColor}&lTags")
                .addLore("<gray>Grant yourself suffixes at the end of your name!")
                .make()
            val arenaBlocks = ItemBuilder(Material.COBBLESTONE)
                .name(" ${Chat.primaryColor}&lArena Blocks")
                .addLore("<gray>Customize the blocks given in your arena kit!")
                .make()
            gui.item(12, tags).onClick runnable@ {
                val gui = GuiBuilder().rows(3).name("${Chat.primaryColor}&lTags").owner(sender)
                val profile = Kraftwerk.instance.profileHandler.getProfile(sender.uniqueId)!!
                if (profile.unlockedTags.size == 0) {
                    Chat.sendMessage(sender, "<red>You have no tags unlocked, buy some at the store at <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config tough tits"}<red>!")
                    return@runnable
                }
                var index = 0
                for (v in profile.unlockedTags) {
                    val tag = Tags.valueOf(v.uppercase())
                    val display = ItemBuilder(tag.item)
                        .name("&d${v.lowercase().replaceFirstChar { it.uppercase() }}")
                        .addLore("<gray>Applies ${tag.display}<gray> at the end of your name.")
                        .make()
                    gui.item(index, display).onClick runnable@ {
                        profile.selectedTag = v.uppercase()
                        Kraftwerk.instance.profileHandler.saveProfile(profile)
                        Chat.sendMessage(sender, "${Chat.prefix} Your tag has been set to ${tag.display}<gray>!")
                    }
                    index++
                }
                index++
                val none = ItemBuilder(Material.BARRIER)
                    .name("<gray>None!")
                    .addLore("<gray>Removes the currently applied tag.")
                    .make()
                gui.item(index, none).onClick runnable@ {
                    profile.selectedTag = null
                    Kraftwerk.instance.profileHandler.saveProfile(profile)
                    Chat.sendMessage(sender, "${Chat.prefix} Removed your currently applied tag!")
                }
                index++
                val back = ItemBuilder(Material.BARRIER)
                    .name("<red>Back")
                    .addLore("Go back to the previous menu.")
                    .make()
                gui.item(26, back).onClick runnable@ {
                    Bukkit.dispatchCommand(sender, "profile")
                }
                sender.openInventory(gui.make())
            }
            gui.item(14, arenaBlocks).onClick runnable@ {
                if (!PerkChecker.checkPerks(sender).contains(Perk.CHOOSE_ARENA_BLOCKS)) {
                    Chat.sendMessage(sender, "<red>You cannot use this feature, buy a rank on the store @ <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store url setup in config tough tits"}<red>!")
                    return@runnable
                }
                val gui = GuiBuilder().rows(2).name("${Chat.primaryColor}&lArena Blocks").owner(sender)
                val profile = Kraftwerk.instance.profileHandler.getProfile(sender.uniqueId)!!
                var index = 0
                for (block in availableArenaBlocks) {
                    val display = ItemBuilder(block)
                    if (Material.valueOf(profile.arenaBlock!!) == block) {
                        display.name("<green>${block.name.uppercase()}")
                        display.addLore("<dark_gray>Currently selected!")
                    } else {
                        display.name("<red>${block.name.uppercase()}")
                        display.addLore("<yellow>Click to select!")
                    }
                    gui.item(index, display.make()).onClick runnable@ {
                        profile.arenaBlock = block.name
                        Kraftwerk.instance.profileHandler.saveProfile(profile)
                        Chat.sendMessage(sender, "${Chat.prefix} Your arena block has been set to ${block.name}<gray>!")
                    }
                    index++
                }
                index++
                val back = ItemBuilder(Material.BARRIER)
                    .name("<red>Back")
                    .addLore("Go back to the previous menu.")
                    .make()
                gui.item(index, back).onClick runnable@ {
                    Bukkit.dispatchCommand(sender, "profile")
                }
                sender.openInventory(gui.make())
            }
            sender.openInventory(gui.make())
        }
        Chat.sendMessage(sender, "${Chat.prefix} Opening your player profile...")
        sender.openInventory(gui.make())
        return true
    }

}
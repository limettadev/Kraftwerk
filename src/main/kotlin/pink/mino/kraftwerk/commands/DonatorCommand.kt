package pink.mino.kraftwerk.commands

import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.utils.*

class DonatorCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command!")
            return true
        }
        val perks = PerkChecker.checkPerks(sender)
        if (perks.isEmpty()) {
            Chat.sendMessage(sender, "<red>You do not have any perks, buy some on the store at <yellow>${if (ConfigFeature.instance.config!!.getString("chat.storeUrl") != null) ConfigFeature.instance.config!!.getString("chat.storeUrl") else "no store app set in config tough tits"}<gray>!")
            return true
        }
        val gui = GuiBuilder().rows(perks.size / 9 + 1).name(Chat.colored("<dark_green><bold>Donator Perks")).owner(sender)
        for ((index, perk) in perks.withIndex()) {
            when (perk) {
                Perk.BYPASS_DEATH_KICK -> {
                    val item = ItemBuilder(Material.IRON_CHESTPLATE)
                        .name("<dark_green><bold>Bypass Death Kick")
                        .addLore("<gray>You will not be kicked automatically after a minute in-game.")
                        .addLore("<dark_gray>Automatic")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.BODY_SPEC -> {
                    val item = ItemBuilder(Material.IRON_CHESTPLATE)
                        .name("<dark_green><bold>Body Spectating")
                        .addLore("<gray>Dying in a UHC automatically will make you spectate a random teammate.")
                        .addLore("<dark_gray>Automatic")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.WHITE_CHAT -> {
                    val item = ItemBuilder(Material.WHITE_WOOL)
                        .name("<dark_green><bold>White Chat")
                        .addLore("<gray>Your chat messages by default are white instead of gray.")
                        .addLore("<dark_gray>Automatic")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.NO_CHAT_DELAY -> {
                    val item = ItemBuilder(Material.CARROT_ON_A_STICK)
                        .name("<dark_green><bold>No Chat Delay")
                        .addLore("<gray>You are exempt from the chat cooldown")
                        .addLore("<dark_gray>Automatic")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.EMOTES -> {
                    val item = ItemBuilder(Material.DIAMOND)
                        .name("<dark_green><bold>Emotes")
                        .addLore("<gray>You are provided some chat emotes to spice up chat!")
                        .addLore("<gray>View the list in <yellow>/emotes<gray>!")
                        .addLore("<dark_gray>Automatic")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.RIDE_PLAYERS -> {
                    val item = ItemBuilder(Material.PLAYER_HEAD)
                        .name("<dark_green><bold>Ride Players")
                        .addLore("<gray>You may ride a player by right-clicking them!")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.STATS_RESET -> {
                    val item = ItemBuilder(Material.NAME_TAG)
                        .name("<dark_green><bold>Stats Reset")
                        .addLore("<gray>You may reset your stats in the stats interface.")
                        .addLore("<dark_gray>Reset your stats in /stats")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.TEAM_COLORS -> {
                    val item = ItemBuilder(Material.BLUE_WOOL)
                        .name("<dark_green><bold>Team Colors")
                        .addLore("<gray>You can choose your team's color.")
                        .addLore("<dark_gray>Change your team color using /team color")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.CHOOSE_ARENA_BLOCKS -> {
                    val item = ItemBuilder(Material.COBBLESTONE)
                        .name("<dark_green><bold>Choose Arena Blocks")
                        .addLore("<gray>You may choose the arena blocks you spawn in while in the pre-game <red>/arena<gray>.")
                        .addLore("<dark_gray>Choose in /profile!")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.TOGGLE_PICKUPS -> {
                    val item = ItemBuilder(Material.LAPIS_BLOCK)
                        .name("<dark_green><bold>Toggle Pickups")
                        .addLore("<gray>You may toggle your pickups for certain ores!")
                        .addLore("<dark_gray>Toggle pickups using <red>/redstone<dark_gray> & or <dark_blue>/lapis<dark_gray>!")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
                Perk.SPAWN_FLY -> {
                    val item = ItemBuilder(Material.FEATHER)
                        .name("<dark_green><bold>Spawn Fly")
                        .addLore("<gray>You may fly in spawn!")
                        .addLore("<dark_gray>Fly in spawn using /fly")
                        .addLore("<dark_gray>Automatic & with /fly")
                        .make()
                    gui.item(index, item).onClick runnable@ {
                        it.isCancelled = true
                    }
                }
            }
        }
        sender.openInventory(gui.make())
        return true
    }
}
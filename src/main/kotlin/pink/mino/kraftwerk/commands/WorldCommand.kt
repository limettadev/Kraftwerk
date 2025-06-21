package pink.mino.kraftwerk.commands

import me.lucko.helper.utils.Log
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.SpawnFeature
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder
import java.nio.file.Files
import java.nio.file.Path

class WorldCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.whitelist")) {
                sender.sendMessage(Chat.colored("<red>You do not have permission to use this command."))
                return false
            }
        }
        val player = sender as Player
        if (args.isEmpty()) {
            Chat.sendMessage(player, Chat.line)
            Chat.sendCenteredMessage(player, "${Chat.primaryColor}&lWorld Help")
            Chat.sendMessage(player, "${Chat.dash} ${Chat.secondaryColor}/world tp <world> <dark_gray>- <gray>Teleport to the provided world.")
            Chat.sendMessage(player, "${Chat.dash} ${Chat.secondaryColor}/world list <dark_gray>- <gray>List all worlds.")
            Chat.sendMessage(player, "${Chat.dash} ${Chat.secondaryColor}/world worlds <dark_gray>- <gray>List all UHC worlds.")
            Chat.sendMessage(player, "${Chat.dash} ${Chat.secondaryColor}/world delete <world> <dark_gray>- <gray>Deletes the provided world.")
            Chat.sendMessage(player, Chat.line)
            return false
        } else if (args[0].lowercase() == "list") {
            Chat.sendMessage(player, Chat.line)
            Chat.sendCenteredMessage(player, "<red>&lWorld List")
            for (world in Bukkit.getServer().worlds) {
                when (world.environment) {
                    World.Environment.NORMAL -> {
                        Chat.sendMessage(player, "<dark_gray>• <green>${world.name} <dark_gray>- ${Chat.secondaryColor}${world.players.size} players")
                    }
                    World.Environment.NETHER -> {
                        Chat.sendMessage(player, "<dark_gray>• <red>${world.name} <dark_gray>- ${Chat.secondaryColor}${world.players.size} players")
                    }
                    World.Environment.THE_END -> {
                        Chat.sendMessage(player, "<dark_gray>• <yellow>${world.name} <dark_gray>- ${Chat.secondaryColor}${world.players.size} players")
                    }
                }
            }
            Chat.sendMessage(player, Chat.line)
        } else if (args[0].lowercase() == "tp") {
            if (args.size == 1) {
                Chat.sendMessage(player, "<red>You need to provide a world.")
                return false
            }
            if (Bukkit.getWorld(args[1]) == null) {
                Chat.sendMessage(player, "<red>You need to provide a valid world.")
                return false
            }
            val world = Bukkit.getWorld(args[1])
            player.teleport(world.spawnLocation)
            Chat.sendMessage(player, "${Chat.dash} Teleported to ${Chat.primaryColor}${world.name}<gray>'s spawn.")
        } else if (args[0].lowercase() == "worlds") {
            val gui = GuiBuilder().name("${Chat.primaryColor}&lWorlds").rows(4)
            for ((index, world) in Bukkit.getServer().worlds.withIndex()) {
                var item: ItemBuilder
                when (world.environment) {
                    World.Environment.NORMAL -> {
                        item = ItemBuilder(Material.GRASS)
                            .name("<green>${world.name}")
                            .addLore("<gray>Contains ${Chat.secondaryColor}${world.players.size} players<gray>.")
                            .addLore(" ")
                            .addLore("<dark_gray>Left Click<gray> to teleport to this world.")
                            .addLore("<dark_gray>Right Click<gray> to set this world as the current UHC world.")
                    }
                    World.Environment.NETHER -> {
                        item = ItemBuilder(Material.NETHERRACK)
                            .name("<red>${world.name}")
                            .addLore("<gray>Contains ${Chat.secondaryColor}${world.players.size} players<gray>.")
                            .addLore(" ")
                            .addLore("<dark_gray>Left Click<gray> to teleport to this world.")
                            .addLore("<dark_gray>Right Click<gray> to set this world as the current UHC world.")
                    }
                    World.Environment.THE_END -> {
                        item = ItemBuilder(Material.ENDER_STONE)
                            .name("&f${world.name}")
                            .addLore("<gray>Contains ${Chat.secondaryColor}${world.players.size} players<gray>.")
                            .addLore(" ")
                            .addLore("<dark_gray>Left Click<gray> to teleport to this world.")
                            .addLore("<dark_gray>Right Click<gray> to set this world as the current UHC world.")
                    }
                }
                if (ConfigFeature.instance.data!!.getString("pregen.world") == world.name) {
                    item.addEnchantment(Enchantment.DURABILITY, 1)
                    item.name("<green>${world.name} <dark_gray>(<gray>Current UHC World<dark_gray>)")
                }
                item.noAttributes()
                gui.item(index, item.make()).onClick {
                    it.isCancelled = true
                    if (it.isLeftClick) {
                        sender.teleport(world.spawnLocation)
                        Chat.sendMessage(sender, "${Chat.dash} Teleported to ${Chat.primaryColor}${world.name}<gray>'s spawn.")
                    } else if (it.isRightClick) {
                        ConfigFeature.instance.data!!.set("pregen.world", world.name)
                        ConfigFeature.instance.data!!.set("pregen.border", world.worldBorder.size / 2)
                        Chat.sendMessage(sender, "${Chat.dash} Set ${Chat.primaryColor}${world.name}<gray> as the current UHC world.")
                    }
                    ConfigFeature.instance.saveData()
                }
            }
            sender.openInventory(gui.make())
        } else if (args[0] == "delete") {
            if (args.size < 2) {
                Chat.sendMessage(sender, "${Chat.dash} <gray>Usage: ${Chat.secondaryColor}/world delete <world>")
                return false
            }
            val world = Bukkit.getWorld(args[1])
            if (world == null) {
                Chat.sendMessage(sender, "<red>That world doesn't exist.")
                return false
            }
            if (world.name == "Spawn" || world.name == "Arena") {
                Chat.sendMessage(sender, "<red>That world cannot be deleted.")
                return false
            }

            for (p in world.players) {
                SpawnFeature.instance.send(p)
            }

            Bukkit.getServer().unloadWorld(world.name, true)
            for (file in Bukkit.getServer().worldContainer.listFiles()!!) {
                if (file.name.lowercase() == world.name.lowercase()) {
                    Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach { it.delete() }
                    file.delete()
                    Log.info("Deleted world file for ${world.name}.")
                }
            }
            ConfigFeature.instance.worlds!!.set(world.name, null)
            ConfigFeature.instance.saveWorlds()
            Chat.sendMessage(sender, "${Chat.dash} <gray>Successfully deleted ${Chat.secondaryColor}${world.name}<gray>.")
        }

        return true
    }

}
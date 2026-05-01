package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.utils.Chat

class GamemodeCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, cmd: Command, lbl: String, args: Array<String>): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.gamemode")) {
                Chat.sendMessage(sender, "<red>You don't have permission to use this command.")
                return false
            }
        }
        if (sender !is Player) {
            sender.sendMessage("You can't use this command as you technically aren't a player.")
            return false
        }
        if (SpecFeature.instance.isSpec(sender)) {
            Chat.sendMessage(sender, "<red>You can't use this command while spectating.")
            return false
        }

        if (args.isEmpty()) {
            val player = sender
            when (cmd.name) {
                "gmsp" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Spectator<gray>.")
                    } else {
                        player.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(player, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Spectator<gray>.")
                    }
                }
                "gmc" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Creative<gray>.")
                    } else {
                        player.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(player, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Creative<gray>.")
                    }
                }
                "gma" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Adventure<gray>.")
                    } else {
                        player.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(player, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Adventure<gray>.")
                    }
                }
                "gms" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Survival<gray>.")
                    } else {
                        player.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(player, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Survival<gray>.")
                    }
                }
            }
        } else {
            when (args[0]) {
                "s" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Survival<gray>.")
                    } else {
                        sender.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Survival<gray>.")
                    }
                }
                "0" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Survival<gray>.")
                    } else {
                        sender.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Survival<gray>.")
                    }
                }
                "survival" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Survival<gray>.")
                    } else {
                        sender.gameMode = GameMode.SURVIVAL
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Survival<gray>.")
                    }
                }

                "c" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Creative<gray>.")
                    } else {
                        sender.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Creative<gray>.")
                    }
                }
                "1" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Creative<gray>.")
                    } else {
                        sender.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Creative<gray>.")
                    }
                }
                "creative" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Creative<gray>.")
                    } else {
                        sender.gameMode = GameMode.CREATIVE
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Creative<gray>.")
                    }
                }

                "a" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Adventure<gray>.")
                    } else {
                        sender.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Adventure<gray>.")
                    }
                }
                "2" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Adventure<gray>.")
                    } else {
                        sender.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Adventure<gray>.")
                    }
                }
                "adventure" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Adventure<gray>.")
                    } else {
                        sender.gameMode = GameMode.ADVENTURE
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Adventure<gray>.")
                    }
                }

                "3" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Spectator<gray>.")
                    } else {
                        sender.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Spectator<gray>.")
                    }
                }
                "sp" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Spectator<gray>.")
                    } else {
                        sender.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Spectator<gray>.")
                    }
                }
                "spectator" -> {
                    if (args.size == 2) {
                        val target = Bukkit.getPlayer(args[0])
                        if (target == null) {
                            Chat.sendMessage(sender, "<red>Player not found.")
                            return false
                        }
                        target.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(target, "${Chat.prefix} ${Chat.secondaryColor}${sender.name}<gray> has set your gamemode to ${Chat.primaryColor}Spectator<gray>.")
                    } else {
                        sender.gameMode = GameMode.SPECTATOR
                        Chat.sendMessage(sender, "${Chat.prefix} <gray>Set your gamemode to ${Chat.primaryColor}Spectator<gray>.")
                    }
                }
            }
        }

        return true
    }

}
package pink.mino.kraftwerk.commands

import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.ScatterFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import pink.mino.kraftwerk.utils.PlayerUtils
import pink.mino.kraftwerk.utils.Scoreboard

class LatescatterCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender is Player) {
            if (!sender.hasPermission("uhc.staff.ls")) {
                Chat.sendMessage(sender, "${ChatColor.RED}You don't have permission to use this command.")
                return false
            }
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "<red>You can't use this command right now.")
            return false
        }
        if (args.isEmpty()) {
            Chat.sendMessage(sender, "${Chat.prefix} Invalid usage: ${Chat.secondaryColor}/ls <player> [teammate]")
            return false
        }
        val player: Player
        val teammate: Player
        var list = ConfigFeature.instance.data!!.getStringList("game.list")
        if (list == null) list = ArrayList<String>()
        if (args.size == 1) {
            player = Bukkit.getPlayer(args[0])!!
            if (ConfigFeature.instance.data!!.getInt("game.teamSize") != 1) {
                TeamsFeature.manager.createTeam(player)
            }
            player.playSound(player.location, Sound.BLOCK_LEVER_CLICK, 10F, 1F)
            player.maxHealth = 20.0
            player.health = player.maxHealth
            player.isFlying = false
            player.allowFlight = false
            player.foodLevel = 20
            player.saturation = 20F
            player.gameMode = GameMode.SURVIVAL
            player.inventory.clear()
            player.inventory.helmet = ItemStack(Material.AIR)
            player.inventory.chestplate = ItemStack(Material.AIR)
            player.inventory.leggings = ItemStack(Material.AIR)
            player.inventory.boots = ItemStack(Material.AIR)
            player.inventory.setItemInOffHand(ItemStack(Material.AIR))

            player.enderChest.clear()
            player.setItemInHand(ItemStack(Material.AIR))
            val openInventory = player.openInventory
            if (openInventory.type == InventoryType.CRAFTING) {
                openInventory.topInventory.clear()
            }
            val effects = player.activePotionEffects
            for (effect in effects) {
                player.removePotionEffect(effect.type)
            }
            player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 300, 1000, true, false))
            ScatterFeature.scatterSolo(player, Bukkit.getWorld(ConfigFeature.instance.data!!.getString("pregen.world")!!)!!, ConfigFeature.instance.data!!.getInt("pregen.border"))
            player.inventory.setItem(0, ItemStack(Material.COOKED_BEEF, ConfigFeature.instance.data!!.getInt("game.starterfood")))
            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(player)!!.gamesPlayed++
            for (scenario in ScenarioHandler.getActiveScenarios()) {
                scenario.givePlayer(player)
            }
            WhitelistCommand().addWhitelist(player.name.lowercase())
            if (!list.contains(player.name)) {
                list.add(player.name)
            }
            Bukkit.broadcast(Chat.colored("${Chat.prefix} ${Chat.secondaryColor}${player.name}<gray> has been late-scattered<gray>."))
            Chat.sendMessage(player, "${Chat.prefix} You've successfully been added to the game.")
        } else if (args.size == 2) {
            player = Bukkit.getPlayer(args[0])!!
            teammate = Bukkit.getPlayer(args[1])!!
            if (TeamsFeature.manager.getTeam(teammate) == null) {
                val team = TeamsFeature.manager.createTeam(player)
                TeamsFeature.manager.joinTeam(team.name, player)
                SendTeamView(team).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
            } else {
                val team = TeamsFeature.manager.getTeam(teammate)
                TeamsFeature.manager.joinTeam(team!!.name, player)
            }
            player.playSound(player.location, Sound.BLOCK_LEVER_CLICK, 10F, 1F)
            player.maxHealth = 20.0
            player.health = player.maxHealth
            player.isFlying = false
            player.allowFlight = false
            player.foodLevel = 20
            player.saturation = 20F
            player.gameMode = GameMode.SURVIVAL
            player.inventory.clear()
            player.enderChest.clear()
            player.inventory.helmet = ItemStack(Material.AIR)
            player.inventory.chestplate = ItemStack(Material.AIR)
            player.inventory.leggings = ItemStack(Material.AIR)
            player.inventory.boots = ItemStack(Material.AIR)
            player.inventory.setItemInOffHand(ItemStack(Material.AIR))
            player.inventory.setItemInMainHand(ItemStack(Material.AIR))

            val openInventory = player.openInventory
            if (openInventory.type == InventoryType.CRAFTING) {
                openInventory.topInventory.clear()
            }
            val effects = player.activePotionEffects
            for (effect in effects) {
                player.removePotionEffect(effect.type)
            }
            player.addPotionEffect(PotionEffect(PotionEffectType.RESISTANCE, 300, 1000, true, false))
            player.teleport(teammate.location)
            player.inventory.setItem(0, ItemStack(Material.COOKED_BEEF, ConfigFeature.instance.data!!.getInt("game.starterfood")))
            JavaPlugin.getPlugin(Kraftwerk::class.java).statsHandler.getStatsPlayer(player)!!.gamesPlayed++
            for (scenario in ScenarioHandler.getActiveScenarios()) {
                scenario.givePlayer(player)
            }
            WhitelistCommand().addWhitelist(player.name.lowercase())
            if (!list.contains(player.name)) {
                list.add(player.name)
            }
            Bukkit.broadcast(Chat.colored("${Chat.prefix} ${Chat.secondaryColor}${player.name}<gray> has been late-scattered to their teammate ${Chat.secondaryColor}${teammate.name}<gray>."))
            Chat.sendMessage(player, "${Chat.prefix} You've successfully been added to the game, you've also been teamed with ${Chat.secondaryColor}${teammate.name}<gray>")
        }
        ConfigFeature.instance.data!!.set("game.list", list)
        ConfigFeature.instance.saveData()
        Scoreboard.setScore(Chat.colored("${Chat.dash} <gray>Playing..."), PlayerUtils.getPlayingPlayers().size)
        return true
    }

}
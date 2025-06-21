package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GameState
import java.util.*
import kotlin.math.floor

class ParafusionScenario : Scenario(
    "Parafusion",
    "When a player mines gold, or diamonds, crafts an enchant table, anvil, golden apples or heads, or brewing stand, eats a golden apple or golden head. or dies, then coordinates will be broadcasted with the names being \"???\". Health on tab will be disabled. Along with the /h command",
    "parafusion",
    Material.BEACON
) {
    val prefix = "<dark_gray>[${Chat.primaryColor}Parafusion<dark_gray>]<gray>"
    var brokenBlocks: HashMap<UUID, HashSet<Block>> = HashMap<UUID, HashSet<Block>>()

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (brokenBlocks.containsKey(e.player.uniqueId)) {
            if (brokenBlocks[e.player.uniqueId]!!.contains(e.block)) return
        }
        if (e.block.type == Material.DIAMOND_ORE) {
            var diamonds = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.DIAMOND_ORE) {
                            diamonds++
                            if (brokenBlocks.containsKey(e.player.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[e.player.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[e.player.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[e.player.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            Bukkit.broadcastMessage(Chat.colored("$prefix &f???<gray> found &b$diamonds diamonds<gray>. <dark_gray>(${Chat.primaryColor}x: ${floor(e.player.location.x)}<gray>, ${Chat.primaryColor}y: ${floor(e.player.location.y)}<gray>, ${Chat.primaryColor}z: ${floor(e.player.location.z)}<dark_gray>)"))
        } else if (e.block.type == Material.GOLD_ORE) {
            var gold = 0
            for (x in -2..1) {
                for (y in -2..1) {
                    for (z in -2..1) {
                        val block: Block = e.block.location.add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (block.type === Material.GOLD_ORE) {
                            gold++
                            if (brokenBlocks.containsKey(e.player.uniqueId)) {
                                val blocks: HashSet<Block> = brokenBlocks[e.player.uniqueId]!!
                                blocks.add(block)
                                brokenBlocks[e.player.uniqueId] = blocks
                            } else {
                                val blocks: HashSet<Block> = HashSet<Block>()
                                blocks.add(block)
                                brokenBlocks[e.player.uniqueId] = blocks
                            }
                        }
                    }
                }
            }
            Bukkit.broadcastMessage(Chat.colored("$prefix &f???<gray> found &6$gold gold<gray>. <dark_gray>(${Chat.primaryColor}x: ${floor(e.player.location.x)}<gray>, ${Chat.primaryColor}y: ${floor(e.player.location.y)}<gray>, ${Chat.primaryColor}z: ${floor(e.player.location.z)}<dark_gray>)"))
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        Bukkit.broadcastMessage(Chat.colored("$prefix &f???<gray> died. <dark_gray>(${Chat.primaryColor}x: ${floor(e.entity.location.x)}<gray>, ${Chat.primaryColor}y: ${floor(e.entity.location.y)}<gray>, ${Chat.primaryColor}z: ${floor(e.entity.location.z)}<dark_gray>)"))
    }

    @EventHandler
    fun onPlayerConsume(e: PlayerItemConsumeEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.item.type == Material.GOLDEN_APPLE) Bukkit.broadcastMessage(Chat.colored("$prefix &f???<gray> ate a &6Golden Apple<gray>. <dark_gray>(${Chat.primaryColor}x: ${floor(e.player.location.x)}<gray>, ${Chat.primaryColor}y: ${floor(e.player.location.y)}<gray>, ${Chat.primaryColor}z: ${floor(e.player.location.z)}<dark_gray>)"))
    }

    @EventHandler
    fun onPlayerCraft(e: CraftItemEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        when (e.inventory.result.type) {
            Material.ANVIL -> {
                Bukkit.broadcastMessage(Chat.colored("$prefix &f???<gray> crafted an &fAnvil<gray>. <dark_gray>(${Chat.primaryColor}x: ${floor(e.whoClicked.location.x)}<gray>, ${Chat.primaryColor}y: ${floor(e.whoClicked.location.y)}<gray>, ${Chat.primaryColor}z: ${floor(e.whoClicked.location.z)}<dark_gray>)"))
            }
            Material.GOLDEN_APPLE -> {
                Bukkit.broadcastMessage(Chat.colored("$prefix &f???<gray> crafted a &6Golden Apple<gray>. <dark_gray>(${Chat.primaryColor}x: ${floor(e.whoClicked.location.x)}<gray>, ${Chat.primaryColor}y: ${floor(e.whoClicked.location.y)}<gray>, ${Chat.primaryColor}z: ${floor(e.whoClicked.location.z)}<dark_gray>)"))
            }
            Material.ENCHANTMENT_TABLE -> {
                Bukkit.broadcastMessage(Chat.colored("$prefix &f???<gray> crafted an &dEnchantment Table<gray>. <dark_gray>(${Chat.primaryColor}x: ${floor(e.whoClicked.location.x)}<gray>, ${Chat.primaryColor}y: ${floor(e.whoClicked.location.y)}<gray>, ${Chat.primaryColor}z: ${floor(e.whoClicked.location.z)}<dark_gray>)"))
            }
            else -> {}
        }
    }
}
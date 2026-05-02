package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.entity.ExperienceOrb
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.inventory.ItemStack
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.BlockUtil
import pink.mino.kraftwerk.utils.GameState

class CutCleanScenario : Scenario(
    "CutClean",
    "Ores/food drop smelted.",
    "cutclean",
    Material.BLAZE_POWDER
) {
    @EventHandler
    fun onItemSpawn(e: ItemSpawnEvent) {
        if (GameState.currentState == GameState.LOBBY) return
        if (!enabled) return
        when (e.entity.itemStack.type) {
            Material.IRON_ORE, Material.RAW_IRON -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.IRON_INGOT
            }
            Material.GOLD_ORE, Material.RAW_GOLD -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.GOLD_INGOT
            }
            Material.RAW_IRON_BLOCK -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.IRON_BLOCK
            }
            Material.RAW_COPPER -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.COPPER_INGOT
            }
            Material.RAW_COPPER_BLOCK -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.COPPER_BLOCK
            }
            Material.RAW_GOLD_BLOCK -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.GOLD_BLOCK
            }
            Material.CHICKEN -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.COOKED_CHICKEN
            }
            Material.BEEF -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.COOKED_BEEF
            }
            Material.MUTTON -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.COOKED_MUTTON
            }
            Material.PORKCHOP -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.COOKED_PORKCHOP
            }
            Material.RABBIT -> {
                (e.location.world.spawn(e.location, ExperienceOrb::class.java) as ExperienceOrb).experience = 1
                e.entity.itemStack.type = Material.COOKED_RABBIT
            }
            else -> {}
        }
    }

    @EventHandler
    fun on(e: BlockBreakEvent) {
        if (GameState.currentState == GameState.LOBBY) return
        if (!enabled) return
        if (e.block.type != Material.SAND) return
        if (!e.player.isSneaking) return
        for (online in e.block.world.players) {
            if (e.player != null && online == e.player) {
                continue
            }
            online.playEffect(e.block.location, org.bukkit.Effect.STEP_SOUND, e.block.type)
        }
        BlockUtil().degradeDurability(e.player)
        e.block.world.dropItemNaturally(e.block.location.add(0.5, 0.7, 0.5), ItemStack(Material.GLASS))
        e.isCancelled = true
        e.block.type = Material.AIR
    }
}
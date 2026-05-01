package pink.mino.kraftwerk.config.options

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.block.Block
import org.bukkit.block.Skull
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.config.ConfigOption
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.BlockRotation
import pink.mino.kraftwerk.utils.ItemBuilder

class GoldenHeadsOption : ConfigOption(
  "Golden Heads",
  "Players drop a head when they die, you can craft golden heads (apples) using them.",
  "options",
  "goldenheads",
  Material.GOLDEN_CARROT
) {

  @EventHandler
  fun onPlayerConsume(e: PlayerItemConsumeEvent) {
    if (!enabled) {
      return
    }
    val player = e.player
    if (e.item.type === Material.GOLDEN_APPLE && e.item.itemMeta.displayName != null && e.item.itemMeta.displayName == "§6Golden Head") {
      player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 1))
    }
  }
  @EventHandler
  fun onPlayerDeath(e: PlayerDeathEvent) {
    if (!enabled) {
      return
    }
    val player = e.entity
    if (player.world.name != "Arena") {
      if (ScenarioHandler.getScenario("goldenretriever")!!.enabled || ScenarioHandler.getScenario("barebones")!!.enabled) {
        return
      }
      if (ScenarioHandler.getScenario("graverobbers")!!.enabled || ScenarioHandler.getScenario("timebomb")!!.enabled) {
        val skull = ItemBuilder(Material.PLAYER_HEAD)
          .toSkull()
          .setOwner(player.name)
          .make()
        e.drops.add(skull)
        return
      }
      player.location.block.type = Material.NETHER_BRICK_FENCE
      player.location.add(0.0, 1.0, 0.0).block.type = Material.PLAYER_HEAD

        val block = player.location.add(0.0, 1.0, 0.0).block
        block.type = Material.PLAYER_HEAD
        val skull = block.state as Skull
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(player.uniqueId))
        skull.rotation = player.facing
        skull.update()

      val b: Block = player.location.add(0.0, 1.0, 0.0).block
    }
    if (ScenarioHandler.getScenario("champions")!!.enabled) {
      val skull = ItemBuilder(Material.PLAYER_HEAD)
        .name("${player.name}'s Head")
        .addLore("<gray>Gives you beneficial effects...")
        .make()
      e.drops.add(skull)
    }
  }
}
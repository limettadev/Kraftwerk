package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.scenarios.Scenario

class DoubleHealthScenario : Scenario(
    "Double Health",
    "All players start with double health.",
    "doublehealth",
    Material.APPLE
) {
    override fun givePlayer(player: Player) {
        player.health = 40.0
        player.maxHealth = 40.0
    }

    override fun onStart() {
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.isSpec(player)) continue
            player.health = 40.0
            player.maxHealth = 40.0
        }
    }
}
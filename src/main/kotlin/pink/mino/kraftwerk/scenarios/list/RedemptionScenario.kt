package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import pink.mino.kraftwerk.scenarios.Scenario
import java.util.*

class RedemptionScenario : Scenario(
    "Redemption",
    "All players in the game have two lives. Once you die a first time, you will be rescattered with your current inventory. Dying for a second time represents your elimination from the game.",
    "redemption",
    Material.DRAGON_EGG,
) {
    companion object {
        val trueDeadPlayers = arrayListOf<UUID>()
    }

    override fun onStart() {
        trueDeadPlayers.clear()
    }
}
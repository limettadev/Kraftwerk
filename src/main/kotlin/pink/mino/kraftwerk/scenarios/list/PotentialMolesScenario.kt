package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat
import kotlin.random.Random

class PotentialMolesScenario : Scenario(
    "Potential Moles",
    "At PvP, every player has a chance to be a mole.",
    "potential_moles",
    Material.IRON_SPADE
) {
    val prefix = "<dark_gray>[${Chat.primaryColor}Potential Moles<dark_gray>]<gray>"

    fun assignMoles() {
        MolesScenario.instance.moleTeam = TeamsFeature.manager.createTeam()
        ArrayList<Player>()
        for (player in Bukkit.getOnlinePlayers()) {
            val random = Random.nextBoolean()
            if (random == true) {
                MolesScenario.instance.moles[player.uniqueId] = false
                Chat.sendMessage(player, "$prefix You are the ${Chat.secondaryColor}mole<gray>! Use ${Chat.secondaryColor}/mole help<gray> to see mole commands.")
            }

        }
    }

        override fun onPvP() {
        assignMoles()
        Bukkit.broadcastMessage(Chat.colored("$prefix Moles have been assigned!"))
    }
}
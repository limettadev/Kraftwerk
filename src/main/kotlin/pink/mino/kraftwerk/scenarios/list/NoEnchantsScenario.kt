package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.Chat

class NoEnchantsScenario : Scenario(
    "No Enchants",
    "Enchantments from the enchantment table are disabled.",
    "noenchants",
    Material.ENCHANTMENT_TABLE
) {
    @EventHandler
    fun onPrepareItemEnchant(e: PrepareItemEnchantEvent) {
        if (!enabled) return
        val player = e.enchanter
        for (i in e.expLevelCostsOffered.indices) {
            e.expLevelCostsOffered[i] = 0
        }
        Chat.sendMessage(player, "&cYou can't enchant in this gamemode.")
    }
}
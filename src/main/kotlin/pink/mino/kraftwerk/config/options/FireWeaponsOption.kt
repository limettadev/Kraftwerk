package pink.mino.kraftwerk.config.options

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.enchantment.EnchantItemEvent
import pink.mino.kraftwerk.config.ConfigOption


class FireWeaponsOption : ConfigOption(
    "Fire Weapons",
    "Toggles fire weapons.",
    "options",
    "fireweapons",
    Material.BLAZE_POWDER
) {
    private val fireAspectEnchant: Enchantment = Enchantment.FIRE_ASPECT
    private val flameEnchant: Enchantment = Enchantment.FLAME

    @EventHandler
    fun onPlayerEnchant(event: EnchantItemEvent) {
        if (enabled) return
        if (event.enchantsToAdd.containsKey(fireAspectEnchant) || event.enchantsToAdd.containsKey(flameEnchant)) {
            event.enchantsToAdd.remove(fireAspectEnchant)
            event.enchantsToAdd.remove(flameEnchant)
        } else {
            return
        }
        if (event.enchantsToAdd.containsKey(Enchantment.SHARPNESS) || event.enchantsToAdd.containsKey(Enchantment.SMITE) || event.enchantsToAdd.containsKey(Enchantment.BANE_OF_ARTHROPODS)) {
            return
        }
        event.enchantsToAdd[Enchantment.SHARPNESS] = (event.whichButton() + 1).coerceAtMost(3)

    }
}
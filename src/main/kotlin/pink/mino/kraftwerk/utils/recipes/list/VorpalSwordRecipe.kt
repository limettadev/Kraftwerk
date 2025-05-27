package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.ItemBuilder

class VorpalSwordRecipe : Recipe(
    "Vorpal Sword",
    "Weaponsmith Craft 1",
    ItemStack(Material.IRON_SWORD),
    3,
    "vorpal_sword"
) {
    init {
        val vorpalSword = ItemBuilder(Material.IRON_SWORD)
            .name("&5Vorpal Sword")
            .addEnchantment(Enchantment.DAMAGE_ARTHROPODS, 2)
            .addEnchantment(Enchantment.DAMAGE_UNDEAD, 2)
            .addEnchantment(Enchantment.LOOT_BONUS_MOBS, 2)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(vorpalSword, id)).shape(" $ ", " * ", " + ")
                .setIngredient('$', Material.BONE)
                .setIngredient('*', Material.IRON_SWORD)
                .setIngredient('+', Material.ROTTEN_FLESH)
    }
}
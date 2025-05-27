package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.ItemBuilder

class SevenLeagueBootsRecipe : Recipe(
    "Seven League Boots",
    "Survivalism Ultimate",
    ItemStack(Material.DIAMOND_BOOTS),
    1,
    "seven_league_boots"
) {
    init {
        val sevenLeagueBoots = ItemBuilder(Material.DIAMOND_BOOTS)
            .name("&5Seven League Boots")
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
            .addEnchantment(Enchantment.PROTECTION_FALL, 3)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(sevenLeagueBoots, id)).shape("FEF", "FDF", "FWF")
            .setIngredient('F', Material.FEATHER)
            .setIngredient('E', Material.ENDER_PEARL)
            .setIngredient('D', Material.DIAMOND_BOOTS)
            .setIngredient('W', Material.WATER_BUCKET)
    }
}
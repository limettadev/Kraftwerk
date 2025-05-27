package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.ItemBuilder

class SpikedArmorRecipe : Recipe(
    "Spiked Armor",
    "Survivalism Craft",
    ItemStack(Material.LEATHER_CHESTPLATE),
    3,
    "spiked_armor"
) {
    init {
        val spikedArmor = ItemBuilder(Material.LEATHER_CHESTPLATE)
            .name("&5Spiked Armor")
            .addEnchantment(Enchantment.THORNS, 0)
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(spikedArmor, id)).shape(" L ", " C ", " P ")
            .setIngredient('C', Material.CACTUS)
            .setIngredient('L', Material.WATER_LILY)
            .setIngredient('P', Material.LEATHER_CHESTPLATE)
    }
}
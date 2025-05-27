package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.ItemBuilder

class TarnhelmRecipe : Recipe(
    "Tarnhelm",
    "Engineering Craft",
    ItemStack(Material.DIAMOND_HELMET),
    3,
    "tarnhelm"
) {
    init {
        val tarnhelm = ItemBuilder(Material.DIAMOND_HELMET)
            .name("&5Tarnhelm")
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .addEnchantment(Enchantment.PROTECTION_FIRE, 1)
            .addEnchantment(Enchantment.WATER_WORKER, 1)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(tarnhelm, id)).shape("DID", "DRD", "   ")
            .setIngredient('D', Material.DIAMOND)
            .setIngredient('I', Material.IRON_INGOT)
            .setIngredient('R', Material.REDSTONE_BLOCK)
    }
}
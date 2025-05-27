package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.recipes.Recipe

class LightEnchantingTableRecipe : Recipe(
    "Light Enchanting Table",
    "Enchanting Craft",
    ItemStack(Material.ENCHANTMENT_TABLE),
    3,
    "light_enchanting_table"
){
    init {
        recipe = ShapedRecipe(convertToRecipeItem(ItemStack(Material.ENCHANTMENT_TABLE), id)).shape(" B ", "ODO", "OXO")
            .setIngredient('B', Material.BOOKSHELF)
            .setIngredient('O', Material.OBSIDIAN)
            .setIngredient('D', Material.DIAMOND)
            .setIngredient('X', Material.EXP_BOTTLE)
    }
}
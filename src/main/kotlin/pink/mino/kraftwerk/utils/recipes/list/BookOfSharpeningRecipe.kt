package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class BookOfSharpeningRecipe : Recipe(
    "Book of Sharpening",
    "Easy to make sharpness for practically nothing!",
    ItemStack(Material.ENCHANTED_BOOK),
    3,
    "book_of_sharpening"
) {
    init {
        val bookOfSharpening = ItemBuilder(Material.ENCHANTED_BOOK)
            .name("&5Book of Sharpening")
            .toEnchant()
            .addStoredEnchant(Enchantment.DAMAGE_ALL, 1)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(bookOfSharpening, id)).shape("F  ", " PP", " PS")
            .setIngredient('F', Material.FLINT)
            .setIngredient('P', Material.PAPER)
            .setIngredient('S', Material.IRON_SWORD)
    }
}
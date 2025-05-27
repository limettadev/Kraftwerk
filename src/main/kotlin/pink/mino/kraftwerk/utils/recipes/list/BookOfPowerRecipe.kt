package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class BookOfPowerRecipe : Recipe(
    "Book of Power",
    "Easy to make power for practically nothing!",
    ItemStack(Material.ENCHANTED_BOOK),
    1,
    "book_of_power"
) {
    init {
        val bookOfPower = ItemBuilder(Material.ENCHANTED_BOOK)
            .name("&5Book of Power")
            .toEnchant()
            .addStoredEnchant(Enchantment.ARROW_DAMAGE, 1)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(bookOfPower, id)).shape("F  ", " PP", " PB")
            .setIngredient('F', Material.FLINT)
            .setIngredient('P', Material.PAPER)
            .setIngredient('B', Material.BONE)
    }
}
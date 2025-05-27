package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class BookOfThothRecipe : Recipe(
    "Book of Thoth",
    "Enchanting Ultimate",
    ItemStack(Material.ENCHANTED_BOOK),
    1,
    "book_of_thoth"
) {
    init {
        val bookOfThoth = ItemBuilder(Material.ENCHANTED_BOOK)
            .name("&5Book of Thoth")
            .addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
            .addStoredEnchant(Enchantment.DAMAGE_ALL, 2)
            .addStoredEnchant(Enchantment.FIRE_ASPECT, 1)
            .addStoredEnchant(Enchantment.ARROW_DAMAGE, 2)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(bookOfThoth, id)).shape("E  ", " PP", " PB")
            .setIngredient('E', Material.EYE_OF_ENDER)
            .setIngredient('B', Material.FIREBALL)
            .setIngredient('P', Material.PAPER)
    }
}
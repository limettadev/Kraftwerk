package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class ArtemisBookRecipe : Recipe(
    "Artemis' Book",
    "Armorsmith Craft",
    ItemStack(Material.ENCHANTED_BOOK),
    3,
    "artemis_book"
) {
    init {
        val artemisBook = ItemBuilder(Material.ENCHANTED_BOOK)
            .name("&5Artemis' Book")
            .toEnchant()
            .addStoredEnchant(Enchantment.PROTECTION_PROJECTILE, 1)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(artemisBook, id)).shape("   ", " PP", " PA")
            .setIngredient('P', Material.PAPER)
            .setIngredient('A', Material.ARROW)
    }
}
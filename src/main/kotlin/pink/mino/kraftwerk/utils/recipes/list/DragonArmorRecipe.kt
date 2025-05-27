package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.recipes.Recipe

class DragonArmorRecipe : Recipe(
    "Dragon Armor",
    "Armorsmith Ultimate",
    ItemStack(Material.DIAMOND_CHESTPLATE),
    1,
    "dragon_armor"
) {
    init {
        val dragonArmor = ItemBuilder(Material.DIAMOND_CHESTPLATE)
            .name("&5Dragon Armor")
            .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4)
            .make()
        recipe = ShapedRecipe(convertToRecipeItem(dragonArmor, id)).shape(" B ", " D ", "OAO")
            .setIngredient('B', Material.MAGMA_CREAM)
            .setIngredient('D', Material.DIAMOND_CHESTPLATE)
            .setIngredient('O', Material.OBSIDIAN)
            .setIngredient('A', Material.ANVIL)
    }
}
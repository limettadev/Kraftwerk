package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.PotionBuilder

class HolyWaterRecipe : Recipe(
    "Holy Water",
    "Cooking Craft",
    ItemStack(Material.GLASS_BOTTLE),
    3,
    "holy_water"
) {
    init {
        val holyWater = PotionBuilder.createPotion(PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 3, false, true))
        recipe = ShapedRecipe(convertToRecipeItem(holyWater, id)).shape("GRG", " D ", " P ")
            .setIngredient('G', Material.GOLD_INGOT)
            .setIngredient('R', Material.REDSTONE_BLOCK)
            .setIngredient('D', Material.GOLD_RECORD)
            .setIngredient('P', Material.GLASS_BOTTLE)
    }
}
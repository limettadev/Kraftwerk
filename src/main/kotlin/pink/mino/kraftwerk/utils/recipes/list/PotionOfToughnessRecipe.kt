package pink.mino.kraftwerk.utils.recipes.list

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.PotionBuilder

class PotionOfToughnessRecipe : Recipe(
    "Potion of Toughness",
    "Survivalism Craft",
    ItemStack(Material.GLASS_BOTTLE),
    3,
    "potion_of_toughness"
) {
    init {
        val potionOfToughness =
            PotionBuilder.createPotion(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 1, false, true))
        recipe = ShapedRecipe(convertToRecipeItem(potionOfToughness, id)).shape(" S ", " W ", " P ")
            .setIngredient('P', Material.GLASS_BOTTLE)
            .setIngredient('S', Material.SLIME_BALL)
            .setIngredient('W', Material.WOOL)
    }
}
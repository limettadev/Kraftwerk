package pink.mino.kraftwerk.utils.recipes

import org.bukkit.NamespacedKey
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.persistence.PersistentDataType
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.ItemBuilder

abstract class Recipe(
    val name: String,
    val description: String,
    val icon: ItemStack,
    val crafts: Int,
    val id: String,
    var recipe: Recipe? = null
) : Listener {
    fun createRecipeBookItem(): ItemStack {
        val item = ItemBuilder(icon.type)
            .name("<green>$name")
            .addLore("<gray>Max Crafts: <aqua>$crafts")
            .addLore(" ")
            .addLore("<gray>$description")
            .addLore(" ")
            .addLore("<green>Click to view recipe!")
        return item.make()
    }

    companion object {
        fun convertToRecipeItem(item: ItemStack, id: String): ItemStack {
            val meta = item.itemMeta!!
            val key = NamespacedKey(Kraftwerk.instance, "uhcId")
            meta.persistentDataContainer.set(key, PersistentDataType.STRING, id)
            item.itemMeta = meta
            return item
        }
    }
}
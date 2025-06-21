package pink.mino.kraftwerk.utils.recipes

import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.minecraft.server.v1_8_R3.NBTTagString
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
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
            .addLore("<gray>Max Crafts: &b$crafts")
            .addLore(" ")
            .addLore("<gray>$description")
            .addLore(" ")
            .addLore("<green>Click to view recipe!")
        return item.make()
    }

    companion object {
        fun convertToRecipeItem(item: ItemStack, id: String): ItemStack {
            val stack = CraftItemStack.asNMSCopy(item)
            val compound: NBTTagCompound = if (stack.hasTag()) stack.tag else NBTTagCompound()
            compound.set("uhcId", NBTTagString(id))
            stack.tag = compound
            return CraftItemStack.asBukkitCopy(stack)
        }
    }
}
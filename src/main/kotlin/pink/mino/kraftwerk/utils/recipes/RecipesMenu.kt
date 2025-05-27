package pink.mino.kraftwerk.utils.recipes

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.menus.Button
import pink.mino.kraftwerk.utils.menus.pagination.PaginatedMenu
import java.util.concurrent.atomic.AtomicInteger

class RecipesMenu : PaginatedMenu() {
    override fun getPrePaginatedTitle(p0: Player?): String {
        return "Recipes"
    }

    override fun getAllPagesButtons(p0: Player?): MutableMap<Int, Button> {
        val buttons: HashMap<Int, Button> = hashMapOf()
        val count = AtomicInteger(0)
        RecipeHandler.recipes
            .stream()
            .sorted(Comparator.comparing(Recipe::name))
            .forEach { recipe ->
                buttons[count.get()] = object : Button() {
                    override fun getName(p0: Player): String {
                        return Chat.colored("${Chat.primaryColor}${recipe.name}")
                    }

                    override fun getMaterial(player: Player): Material {
                        return recipe.icon.type
                    }

                    override fun getDescription(p0: Player): List<String> {
                        return mutableListOf(
                            Chat.colored("&7Crafts: ${Chat.primaryColor}${recipe.crafts}"),
                            " ",
                            Chat.colored("&7${recipe.description}"),
                            " ",
                            Chat.colored("${Chat.primaryColor}Click to view crafting recipe.")
                        ).toList()
                    }

                    override fun clicked(player: Player, slot: Int, clickType: ClickType) {
                        Bukkit.dispatchCommand(player, "recipe ${recipe.id}")
                    }
                }
                count.getAndIncrement()
            }
        return buttons
    }

    override fun getMaxItemsPerPage(player: Player?): Int {
        return 45
    }
}
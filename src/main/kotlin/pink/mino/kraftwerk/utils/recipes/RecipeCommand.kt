package pink.mino.kraftwerk.utils.recipes

import me.lucko.helper.utils.Log
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.GuiBuilder
import pink.mino.kraftwerk.utils.ItemBuilder
import pink.mino.kraftwerk.utils.MiscUtils

class RecipeCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("nope")
            return true
        }
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("champions"))) {
            Chat.sendMessage(sender, "<red>Champions is not enabled.")
            return false
        }
        if (args.isEmpty()) {
            RecipesMenu().openMenu(sender)
            return true
        }
        var gui = GuiBuilder()
            .owner(sender)
            .name("Recipes")
            .rows(5)
        gui = MiscUtils.populateCrafting(gui)
        val slots = arrayListOf(
            10, 11, 12,
            19, 20, 21,
            28, 29, 30
        )
        val data = RecipeHandler.getRecipe(args[0])
        if (data == null) {
            Chat.sendMessage(sender, "<red>Invalid recipe!")
            return false
        }
        if (data.recipe is ShapedRecipe) {
            val shape = (data.recipe as ShapedRecipe).shape
            val rows = arrayListOf<List<String>>()
            for (row in shape) {
                val dummy = arrayListOf<String>()
                for (symbol in row.split("")) {
                    if (symbol.isNotEmpty() && !Character.isWhitespace(symbol[0])) {
                        dummy.add(symbol)
                    }
                    if (symbol.isNotEmpty() && symbol == " ") {
                        dummy.add(symbol)
                    }
                }
                rows.add(dummy)
            }
            for (row in rows) {
                for (symbol in row) {
                    if (slots.isNotEmpty()) {
                        Log.info(row.toString())
                        val first = slots[0]
                        val item = if (symbol.isEmpty()) {
                            ItemStack(Material.AIR)
                        } else {
                            (data.recipe as ShapedRecipe).ingredientMap[symbol[0]]
                        }
                        if (item == null) {
                            gui.item(first, ItemStack(Material.AIR)).onClick runnable@{
                                it.isCancelled = true
                            }
                        } else {
                            gui.item(first, item).onClick runnable@{
                                it.isCancelled = true
                            }
                        }
                        slots.remove(first)
                    }
                }
            }
            gui.item(25, (data.recipe as ShapedRecipe).result).onClick runnable@{
                it.isCancelled = true
            }
        } else if (data.recipe is ShapelessRecipe) {
            for (ingredient in (data.recipe as ShapelessRecipe).ingredientList) {
                val first = slots[0]
                if (ingredient == null) {
                    gui.item(first, ItemStack(Material.AIR)).onClick runnable@{
                        it.isCancelled = true
                    }
                } else {
                    gui.item(first, ingredient).onClick runnable@{
                        it.isCancelled = true
                    }
                }
                slots.remove(first)
            }
            gui.item(25, (data.recipe as ShapelessRecipe).result).onClick runnable@{
                it.isCancelled = true
            }
        }
        val back = ItemBuilder(Material.ARROW)
            .name("<red>Back")
            .addLore("<gray>Go back")
            .make()
        gui.item(44, back).onClick runnable@{
            sender.closeInventory()
            Bukkit.getScheduler().runTaskLater(Kraftwerk.instance, runnable@{
                Bukkit.dispatchCommand(sender, "recipes")
            }, 1L)
            return@runnable
        }
        Chat.sendMessage(sender, "${Chat.prefix} Opening ${Chat.primaryColor}${data.name}<yellow> recipe...")
        sender.openInventory(gui.make())
        return true
    }
}
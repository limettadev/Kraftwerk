package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionType
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.scenarios.list.MolesScenario
import pink.mino.kraftwerk.utils.*

class MoleKitCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        if (sender !is Player) {
            Chat.sendMessage(sender, "You probably shouldn't use this command as you aren't a player.")
            return false
        }
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("moles"))) {
            Chat.sendMessage(sender, "${Chat.prefix} ${Chat.primaryColor}Moles<gray> isn't enabled!")
            return false
        }
        if (GameState.currentState != GameState.INGAME) {
            Chat.sendMessage(sender, "${Chat.prefix} ${Chat.primaryColor}Moles<gray> isn't available right now!")
            return false
        }
        if (MolesScenario.instance.moles[sender.uniqueId] == null) {
            Chat.sendMessage(sender, "${Chat.prefix} <gray>You aren't a mole!")
            return false
        }
        if (MolesScenario.instance.moles[sender.uniqueId] == true) {
            Chat.sendMessage(sender, "${Chat.prefix} You already redeemed your mole kit.")
            return false
        }
        when {
            args.isEmpty() -> {
                val gui = GuiBuilder().rows(1).name(ChatColor.translateAlternateColorCodes('&', "${Chat.primaryColor}Mole Kits")).owner(sender)
                val troll = ItemBuilder(Material.WEB)
                    .name("&6Troll Kit")
                    .addLore(Chat.guiLine)
                    .addLore("&6Troll Kit:")
                    .addLore("<dark_gray>- &f16 Cobwebs")
                    .addLore("<dark_gray>- &f5 TNT")
                    .addLore("<dark_gray>- &f1 Flint and Steel")
                    .addLore(Chat.guiLine)
                    .make()
                val potter = ItemBuilder(Material.POTION)
                    .name("&6Potter Kit")
                    .addLore(Chat.guiLine)
                    .addLore("&6Potter Kit:")
                    .addLore("<dark_gray>- &f1 Speed II Potion")
                    .addLore("<dark_gray>- &f1 Splash Potion of Weakness")
                    .addLore("<dark_gray>- &f1 Splash Potion of Poison II")
                    .addLore(Chat.guiLine)
                    .make()
                val fighter = ItemBuilder(Material.DIAMOND_SWORD)
                    .name("&6Fighter Kit")
                    .addLore(Chat.guiLine)
                    .addLore("&6Fighter Kit:")
                    .addLore("<dark_gray>- &f1 Diamond Sword")
                    .addLore("<dark_gray>- &f1 Golden Apple")
                    .addLore("<dark_gray>- &f1 Fishing Rod")
                    .addLore(Chat.guiLine)
                    .make()
                val trapper = ItemBuilder(Material.TNT)
                    .name("&6Trapper Kit")
                    .noAttributes()
                    .addLore(Chat.guiLine)
                    .addLore("&6Trapper Kit:")
                    .addLore("<dark_gray>- &f16 TNT")
                    .addLore("<dark_gray>- &f1 Sticky Piston")
                    .addLore("<dark_gray>- &f1 Piston")
                    .addLore("<dark_gray>- &f1 Flint and Steel")
                    .addLore(Chat.guiLine)
                    .make()
                val tank = ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .setDurability(5)
                    .name("&6Tank Kit")
                    .addLore(Chat.guiLine)
                    .addLore("&6Tank Kit:")
                    .addLore("<dark_gray>- &fFull Diamond Armor w/ 5 Durability Remaining")
                    .addLore(Chat.guiLine)
                    .make()
                val enchanter = ItemBuilder(Material.ENCHANTMENT_TABLE)
                    .setDurability(5)
                    .name("&6Enchanter Kit")
                    .addLore(Chat.guiLine)
                    .addLore("&6Enchanter Kit:")
                    .addLore("<dark_gray>- &f1 Enchantment Table")
                    .addLore("<dark_gray>- &f64 XP Bottles")
                    .addLore("<dark_gray>- &f8 Lapis Blocks")
                    .addLore(Chat.guiLine)
                    .make()
                val healer = ItemBuilder(Material.GOLDEN_APPLE)
                    .name("&6Healer Kit")
                    .addLore(Chat.guiLine)
                    .addLore("&6Healer Kit:")
                    .addLore("<dark_gray>- &f2 Golden Apples")
                    .addLore("<dark_gray>- &f1 Splash Potion of Healing")
                    .addLore(Chat.guiLine)
                    .make()
                val projectile = ItemBuilder(Material.ARROW)
                    .name("&6Projectile Kit")
                    .addLore(Chat.guiLine)
                    .addLore("&6Projectile Kit:")
                    .addLore("<dark_gray>- &f64 Arrows")
                    .addLore("<dark_gray>- &f1 Bow")
                    .addLore("<dark_gray>- &f1 Fishing Rod")
                    .addLore(Chat.guiLine)
                    .make()
                gui.item(0, troll).onClick runnable@ {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "molekit troll")
                }
                gui.item(1, potter).onClick runnable@ {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "molekit potter")
                }
                gui.item(2, fighter).onClick runnable@ {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "molekit fighter")
                }
                gui.item(3, trapper).onClick runnable@ {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "molekit trapper")
                }
                gui.item(4, tank).onClick runnable@ {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "molekit tank")
                }
                gui.item(5, enchanter).onClick runnable@ {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "molekit enchanter")
                }
                gui.item(6, healer).onClick runnable@ {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "molekit healer")
                }
                gui.item(7, projectile).onClick runnable@ {
                    it.isCancelled = true
                    Bukkit.dispatchCommand(sender, "molekit projectile")
                }
                sender.openInventory(gui.make())
            }
            args[0].lowercase() == "projectile" -> {
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    ItemStack(Material.FISHING_ROD, 1),
                    ItemStack(Material.ARROW, 64),
                    ItemStack(Material.BOW, 1)
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Projectile<gray> kit.")
            }
            args[0].lowercase() == "healer" -> {
                val healingPotion = Potion(PotionType.INSTANT_HEAL, 2)
                healingPotion.isSplash = true
                healingPotion.level = 2
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    ItemStack(Material.GOLDEN_APPLE, 2),
                    healingPotion.toItemStack(1)
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Healer<gray> kit.")
            }
            args[0].lowercase() == "enchanter" -> {
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    ItemStack(Material.ENCHANTMENT_TABLE, 1),
                    ItemStack(Material.EXP_BOTTLE, 64),
                    ItemStack(Material.LAPIS_BLOCK, 8),
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Enchanter<gray> kit.")
            }
            args[0].lowercase() == "tank" -> {
                val helmet = ItemBuilder(Material.DIAMOND_HELMET)
                    .setDurability(5)
                    .make()
                val chestplate = ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .setDurability(5)
                    .make()
                val leggings = ItemBuilder(Material.DIAMOND_LEGGINGS)
                    .setDurability(5)
                    .make()
                val boots = ItemBuilder(Material.DIAMOND_BOOTS)
                    .setDurability(5)
                    .make()
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    helmet, chestplate, leggings, boots
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Tank<gray> kit.")
            }
            args[0].lowercase() == "trapper" -> {
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    ItemStack(Material.TNT, 16) ,
                    ItemStack(Material.PISTON_BASE, 1),
                    ItemStack(Material.PISTON_STICKY_BASE, 1),
                    ItemStack(Material.FLINT_AND_STEEL, 1)
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Trapper<gray> kit.")
            }
            args[0].lowercase() == "fighter" -> {
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    ItemStack(Material.DIAMOND_SWORD, 1),
                    ItemStack(Material.GOLDEN_APPLE, 1),
                    ItemStack(Material.FISHING_ROD, 1)
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Fighter<gray> kit.")
            }
            args[0].lowercase() == "potter" -> {
                val speed2Potion = Potion(PotionType.SPEED, 2)
                val weaknessPotion = Potion(PotionType.WEAKNESS, 1)
                weaknessPotion.isSplash = true
                weaknessPotion.level = 1
                val poisonPotion = Potion(PotionType.POISON, 2)
                poisonPotion.isSplash = true
                poisonPotion.level = 2
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    speed2Potion.toItemStack(1),
                    weaknessPotion.toItemStack(1),
                    poisonPotion.toItemStack(1),
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Potter<gray> kit.")
            }
            args[0].lowercase() == "troll" -> {
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    ItemStack(Material.WEB, 16),
                    ItemStack(Material.TNT, 5),
                    ItemStack(Material.FLINT_AND_STEEL, 1)
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Troll<gray> kit.")
            }
        }
        return true
    }
}
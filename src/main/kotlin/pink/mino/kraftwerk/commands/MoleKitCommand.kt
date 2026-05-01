package pink.mino.kraftwerk.commands

import net.minecraft.world.item.alchemy.Potion
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
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
                val troll = ItemBuilder(Material.COBWEB)
                    .name("<gold>Troll Kit")
                    .addLore(Chat.guiLine)
                    .addLore("<gold>Troll Kit:")
                    .addLore("<dark_gray>- <white>16 Cobwebs")
                    .addLore("<dark_gray>- <white>5 TNT")
                    .addLore("<dark_gray>- <white>1 Flint and Steel")
                    .addLore(Chat.guiLine)
                    .make()
                val potter = ItemBuilder(Material.POTION)
                    .name("<gold>Potter Kit")
                    .addLore(Chat.guiLine)
                    .addLore("<gold>Potter Kit:")
                    .addLore("<dark_gray>- <white>1 Speed II Potion")
                    .addLore("<dark_gray>- <white>1 Splash Potion of Weakness")
                    .addLore("<dark_gray>- <white>1 Splash Potion of Poison II")
                    .addLore(Chat.guiLine)
                    .make()
                val fighter = ItemBuilder(Material.DIAMOND_SWORD)
                    .name("<gold>Fighter Kit")
                    .addLore(Chat.guiLine)
                    .addLore("<gold>Fighter Kit:")
                    .addLore("<dark_gray>- <white>1 Diamond Sword")
                    .addLore("<dark_gray>- <white>1 Golden Apple")
                    .addLore("<dark_gray>- <white>1 Fishing Rod")
                    .addLore(Chat.guiLine)
                    .make()
                val trapper = ItemBuilder(Material.TNT)
                    .name("<gold>Trapper Kit")
                    .noAttributes()
                    .addLore(Chat.guiLine)
                    .addLore("<gold>Trapper Kit:")
                    .addLore("<dark_gray>- <white>16 TNT")
                    .addLore("<dark_gray>- <white>1 Sticky Piston")
                    .addLore("<dark_gray>- <white>1 Piston")
                    .addLore("<dark_gray>- <white>1 Flint and Steel")
                    .addLore(Chat.guiLine)
                    .make()
                val tank = ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .setDurability(5)
                    .name("<gold>Tank Kit")
                    .addLore(Chat.guiLine)
                    .addLore("<gold>Tank Kit:")
                    .addLore("<dark_gray>- <white>Full Diamond Armor w/ 5 Durability Remaining")
                    .addLore(Chat.guiLine)
                    .make()
                val enchanter = ItemBuilder(Material.ENCHANTING_TABLE)
                    .setDurability(5)
                    .name("<gold>Enchanter Kit")
                    .addLore(Chat.guiLine)
                    .addLore("<gold>Enchanter Kit:")
                    .addLore("<dark_gray>- <white>1 Enchantment Table")
                    .addLore("<dark_gray>- <white>64 XP Bottles")
                    .addLore("<dark_gray>- <white>8 Lapis Blocks")
                    .addLore(Chat.guiLine)
                    .make()
                val healer = ItemBuilder(Material.GOLDEN_APPLE)
                    .name("<gold>Healer Kit")
                    .addLore(Chat.guiLine)
                    .addLore("<gold>Healer Kit:")
                    .addLore("<dark_gray>- <white>2 Golden Apples")
                    .addLore("<dark_gray>- <white>1 Splash Potion of Healing")
                    .addLore(Chat.guiLine)
                    .make()
                val projectile = ItemBuilder(Material.ARROW)
                    .name("<gold>Projectile Kit")
                    .addLore(Chat.guiLine)
                    .addLore("<gold>Projectile Kit:")
                    .addLore("<dark_gray>- <white>64 Arrows")
                    .addLore("<dark_gray>- <white>1 Bow")
                    .addLore("<dark_gray>- <white>1 Fishing Rod")
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
                val healingPotion = ItemStack(Material.SPLASH_POTION)
                val potionMeta = healingPotion.itemMeta as PotionMeta
                potionMeta.addCustomEffect(PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1), false)
                healingPotion.itemMeta = potionMeta
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    ItemStack(Material.GOLDEN_APPLE, 2),
                    healingPotion
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Healer<gray> kit.")
            }
            args[0].lowercase() == "enchanter" -> {
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    ItemStack(Material.ENCHANTING_TABLE, 1),
                    ItemStack(Material.EXPERIENCE_BOTTLE, 64),
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
                    ItemStack(Material.PISTON, 1),
                    ItemStack(Material.STICKY_PISTON, 1),
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
                val speed2Potion = ItemStack(Material.POTION)
                var potionMeta = speed2Potion.itemMeta as PotionMeta
                potionMeta.addCustomEffect(PotionEffect(PotionEffectType.SPEED, 90, 1), false)
                speed2Potion.itemMeta = potionMeta

                val weaknessPotion = ItemStack(Material.SPLASH_POTION)
                potionMeta = weaknessPotion.itemMeta as PotionMeta
                potionMeta.addCustomEffect(PotionEffect(PotionEffectType.WEAKNESS, 90, 0), false)
                weaknessPotion.itemMeta = potionMeta

                val poisonPotion = ItemStack(Material.SPLASH_POTION)
                potionMeta = poisonPotion.itemMeta as PotionMeta
                potionMeta.addCustomEffect(PotionEffect(PotionEffectType.POISON, 21, 1), false)
                weaknessPotion.itemMeta = potionMeta

                val bulk: ArrayList<ItemStack> = arrayListOf(
                    speed2Potion,
                    weaknessPotion,
                    poisonPotion,
                )
                PlayerUtils.bulkItems(sender, bulk)
                MolesScenario.instance.moles[sender.uniqueId] = true
                Chat.sendMessage(sender, "${MolesScenario.instance.prefix} You've been given your ${Chat.secondaryColor}Potter<gray> kit.")
            }
            args[0].lowercase() == "troll" -> {
                val bulk: ArrayList<ItemStack> = arrayListOf(
                    ItemStack(Material.COBWEB, 16),
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
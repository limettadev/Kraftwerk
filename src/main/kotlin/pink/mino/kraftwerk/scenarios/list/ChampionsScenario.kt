package pink.mino.kraftwerk.scenarios.list

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.material.SpawnEgg
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.SpecFeature
import pink.mino.kraftwerk.features.TeamsFeature
import pink.mino.kraftwerk.scenarios.Scenario
import pink.mino.kraftwerk.utils.*
import pink.mino.kraftwerk.utils.recipes.Recipe
import pink.mino.kraftwerk.utils.recipes.RecipeHandler
import java.util.*
import kotlin.math.floor


class ChampionsScenario : Scenario(
    "Champions",
    "Adds Hypixel UHC crafts & other features to the game.",
    "champions",
    Material.GOLDEN_APPLE
), CommandExecutor {
    val prefix = Chat.colored("<dark_gray>[${Chat.primaryColor}Champions<dark_gray>] <gray>")
    val kits = hashMapOf<UUID, String>()

    init {
        JavaPlugin.getPlugin(Kraftwerk::class.java).getCommand("championsKit").executor = this
    }

    override fun onToggle(to: Boolean) {
        if (!to) {
            Bukkit.resetRecipes()
            JavaPlugin.getPlugin(Kraftwerk::class.java).addRecipes()
        } else {
            Bukkit.resetRecipes()
            RecipeHandler.setup()
        }
    }

    private val perunCooldownsMap = hashMapOf<Player, Long>()
    @EventHandler
    fun onPvP(e: EntityDamageByEntityEvent) {
        if (!enabled) return
        if (GameState.currentState != GameState.INGAME) return
        if (e.damager is Player && e.entity is Player) {
            if ((e.damager as Player).inventory.itemInHand != null && (e.damager as Player).inventory.itemInHand.hasItemMeta() && (e.damager as Player).inventory.itemInHand.itemMeta.displayName == Chat.colored("<yellow>Death's Scythe")) {
                (e.entity as Player).damage((e.entity as Player).health * .2)
            }
            if ((e.damager as Player).inventory.helmet != null && (e.damager as Player).inventory.helmet.hasItemMeta() && (e.damager as Player).inventory.helmet.itemMeta.displayName == Chat.colored("<yellow>Exodus")) {
                if (!(e.damager as Player).hasPotionEffect(PotionEffectType.REGENERATION)) {
                    (e.damager as Player).addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 50, 0))
                }
            }
            if ((e.damager as Player).inventory.itemInHand != null && (e.damager as Player).inventory.itemInHand.hasItemMeta() && (e.damager as Player).inventory.itemInHand.itemMeta.displayName == Chat.colored("<yellow>Axe of Perun")) {
                if (perunCooldownsMap[e.damager as Player] == null || perunCooldownsMap[e.damager as Player]!! < System.currentTimeMillis()) {
                    (e.damager as Player).world.strikeLightning((e.entity as Player).location)
                    perunCooldownsMap[e.damager as Player] = System.currentTimeMillis() + 8000
                } else {
                    return
                }
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (!enabled) return
        if (e.entity.killer == null) return
        if (e.entity.killer !is Player) return
        e.drops.add(ItemStack(Material.GOLD_NUGGET, 10))
        e.droppedExp = (e.droppedExp + floor((e.droppedExp * 0.50))).toInt()
        if (e.entity.killer.itemInHand != null && e.entity.killer.itemInHand.hasItemMeta() && e.entity.killer.itemInHand.itemMeta.displayName == Chat.colored("<yellow>Bloodlust")) {
            if (ConfigFeature.instance.data!!.getInt("game.kills.${e.entity.killer.name}") == 1) {
                e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 2)
            } else if (ConfigFeature.instance.data!!.getInt("game.kills.${e.entity.killer.name}") == 3) {
                e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 3)
            } else if (ConfigFeature.instance.data!!.getInt("game.kills.${e.entity.killer.name}") == 6) {
                e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 4)
            } else if (ConfigFeature.instance.data!!.getInt("game.kills.${e.entity.killer.name}") == 10) {
                e.entity.killer.itemInHand.addEnchantment(Enchantment.DAMAGE_ALL, 5)
            }
        }
        e.entity.killer.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 5, 0, false, true))
        e.entity.killer.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 0, false, true))
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (!enabled) return
        if (e.item == null) return
        if (e.item.type == Material.SKULL_ITEM && e.item.itemMeta.displayName == Chat.colored("&6Golden Head")) {
            e.isCancelled = true
            val item = e.item.clone()
            item.amount = 1
            e.player.inventory.removeItem(item)
            if (TeamsFeature.manager.getTeam(e.player) == null) {
                Chat.sendMessage(
                    e.player,
                    "$prefix You ate a &6Golden Head<gray> and gained 15 seconds of Regeneration II & 2 minutes of Absorption."
                )
                e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1, false, true))
                e.player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 1, false, true))
            } else {
                for (teammate in TeamsFeature.manager.getTeam(e.player)!!.players) {
                    if (teammate.isOnline && teammate != null) {
                        Chat.sendMessage(
                            teammate as Player,
                            "$prefix &6${e.player.name}<gray> ate a &6Golden Head<gray> and you gained gained 5 seconds of Regeneration II & 1 minute of Absorption."
                        )
                        teammate.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1, false, true))
                        teammate.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 20 * 60, 1, false, true))
                    }
                }
            }
        } else if (e.item.type == Material.SKULL_ITEM) {
            e.isCancelled = true
            e.item.amount = e.item.amount - 1
            if (e.item.amount == 0) {
                e.player.inventory.remove(e.item)
            }
            Chat.sendMessage(
                e.player,
                "$prefix You ate a player head and gained 20 seconds of Regeneration I! You gained 9 seconds of Speed II"
            )
            e.player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 0, false, true))
            e.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 9, 0, false, true))
        }
        if (e.item.itemMeta.displayName == Chat.colored("<red>Crafting Recipes")) {
            e.isCancelled = true
            Bukkit.dispatchCommand(e.player, "recipes")
        }
    }

    @EventHandler
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (!enabled) return
        if (e.entity !is Player) return
        if (
            e.cause == EntityDamageEvent.DamageCause.FALL ||
            e.cause == EntityDamageEvent.DamageCause.CONTACT ||
            e.cause == EntityDamageEvent.DamageCause.VOID ||
            e.cause == EntityDamageEvent.DamageCause.SUFFOCATION ||
            e.cause == EntityDamageEvent.DamageCause.LIGHTNING ||
            e.cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
            e.cause == EntityDamageEvent.DamageCause.FIRE ||
            e.cause == EntityDamageEvent.DamageCause.LAVA ||
            e.cause == EntityDamageEvent.DamageCause.DROWNING ||
            e.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
            e.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
            e.cause == EntityDamageEvent.DamageCause.MAGIC
        ) {
            e.damage = e.damage - (e.damage * 0.8)
        }
    }

    @EventHandler
    fun onBlockBreak(e: BlockBreakEvent) {
        if (!enabled) return
        if (e.player !is Player) return
        e.player.addPotionEffect(PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 100, 0, false, false))
        if (e.block.type == Material.GOLD_ORE || e.block.type == Material.IRON_ORE) {
            val chance = (0..100).random()
            if (chance <= 14) {
                e.player.location.world.dropItem(e.player.location, ItemBuilder(e.block.type).make())
            }
        }
        if (e.block.type == Material.SAND || e.block.type == Material.GRAVEL || e.block.type == Material.OBSIDIAN) {
            val chance = (0..100).random()
            if (chance <= 50) {
                e.player.location.world.dropItem(e.player.location, ItemBuilder(e.block.type).make())
            }
        }
    }

    override fun givePlayer(player: Player) {
        player.maxHealth = 40.0
        player.health = 40.0
        player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 6000, 0, false, false))
        player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 6000, 0, false, false))
        if (kits[player.uniqueId] == null) {
            kits[player.uniqueId] = "leather"
        }
        if (kits[player.uniqueId] == "leather") {
            val helmet = ItemBuilder(Material.LEATHER_HELMET)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .make()
            val chestplate = ItemBuilder(Material.LEATHER_CHESTPLATE)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .make()
            val leggings = ItemBuilder(Material.LEATHER_LEGGINGS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .make()
            val boots = ItemBuilder(Material.LEATHER_BOOTS)
                .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                .make()
            player.inventory.helmet = helmet
            player.inventory.chestplate = chestplate
            player.inventory.leggings = leggings
            player.inventory.boots = boots
        } else if (kits[player.uniqueId] == "enchanter") {
            val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                .addEnchantment(Enchantment.DIG_SPEED, 3)
                .addEnchantment(Enchantment.DURABILITY, 1)
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.BOOK, 4),
                    ItemStack(Material.EXP_BOTTLE, 15),
                    ItemStack(Material.INK_SACK, 18, 4),
                    pickaxe
                )
            )
        } else if (kits[player.uniqueId] == "archer") {
            val shovel = ItemBuilder(Material.STONE_SPADE)
                .addEnchantment(Enchantment.DIG_SPEED, 3)
                .addEnchantment(Enchantment.DURABILITY, 1)
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.STRING, 6),
                    ItemStack(Material.FEATHER, 9),
                    shovel
                )
            )
        } else if (kits[player.uniqueId] == "stoneGear") {
            val shovel = ItemBuilder(Material.STONE_SPADE)
                .addEnchantment(Enchantment.DIG_SPEED, 3)
                .addEnchantment(Enchantment.DURABILITY, 1)
                .make()
            val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                .addEnchantment(Enchantment.DIG_SPEED, 3)
                .addEnchantment(Enchantment.DURABILITY, 1)
                .make()
            val axe = ItemBuilder(Material.STONE_AXE)
                .addEnchantment(Enchantment.DIG_SPEED, 3)
                .addEnchantment(Enchantment.DURABILITY, 1)
                .make()
            val sword = ItemBuilder(Material.STONE_SWORD)
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    shovel,
                    pickaxe,
                    axe,
                    sword
                )
            )
        } else if (kits[player.uniqueId] == "lunchBox") {
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.COOKED_BEEF, 9),
                    ItemStack(Material.CARROT, 12),
                    ItemStack(Material.MELON, 2),
                    ItemStack(Material.APPLE, 3),
                    ItemStack(Material.GOLD_INGOT, 3),
                )
            )
        } else if (kits[player.uniqueId] == "looter") {
            val sword = ItemBuilder(Material.STONE_SWORD)
                .addEnchantment(Enchantment.LOOT_BONUS_MOBS, 1)
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.BONE, 3),
                    ItemStack(Material.SLIME_BALL, 3),
                    ItemStack(Material.SULPHUR, 2),
                    ItemStack(Material.SPIDER_EYE, 2),
                    sword
                )
            )
        } else if (kits[player.uniqueId] == "ecologist") {
            val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                .addEnchantment(Enchantment.DIG_SPEED, 3)
                .addEnchantment(Enchantment.DURABILITY, 1)
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.VINE, 21),
                    ItemStack(Material.WATER_LILY, 64),
                    ItemStack(Material.SUGAR_CANE, 12),
                    pickaxe
                )
            )
        } else if (kits[player.uniqueId] == "farmer") {
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.IRON_HOE),
                    ItemStack(Material.MELON, 3),
                    ItemStack(Material.CARROT, 3),
                    ItemStack(Material.INK_SACK, 15),
                )
            )
        } else if (kits[player.uniqueId] == "horseman") {
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.LEATHER, 12),
                    ItemStack(Material.HAY_BLOCK, 1),
                    ItemStack(Material.STRING, 4),
                    ItemStack(Material.IRON_BARDING),
                    SpawnEgg(EntityType.HORSE).toItemStack(1)
                )
            )
        } else if (kits[player.uniqueId] == "trapper") {
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    ItemStack(Material.PISTON_BASE, 8),
                    ItemStack(Material.PISTON_STICKY_BASE, 8),
                    ItemStack(Material.REDSTONE, 25),
                    ItemStack(Material.LOG, 16)
                )
            )
        }
        val book = ItemBuilder(Material.ENCHANTED_BOOK)
            .name("<red>Crafting Recipes")
            .addLore("<gray>Click to open & view a list of crafting recipes.")
            .make()
        PlayerUtils.bulkItems(
            player, arrayListOf(
                book
            )
        )
    }

    private val recipeNotified: MutableMap<UUID, MutableSet<String>> = hashMapOf()

    @EventHandler
    fun onPlayerPickup(event: PlayerPickupItemEvent) {
        if (!enabled) return
        val player = event.player
        val item = event.item.itemStack
        val notified = recipeNotified.getOrPut(player.uniqueId) { mutableSetOf() }

        for (recipe in RecipeHandler.recipes) {
            val isIngredient = when (val r = recipe.recipe) {
                is ShapedRecipe -> r.ingredientMap.values.any { it != null && it.isSimilar(item) }
                is ShapelessRecipe -> r.ingredientList.any { it != null && it.isSimilar(item) }
                else -> false
            }
            if (isIngredient && canCraft(player, recipe) && recipe.id !in notified) {
                Chat.sendMessage(player, "<green>You now have the items to craft <yellow>${recipe.name}<green>!")
                notified.add(recipe.id)
            }
        }
    }

    @EventHandler
    fun onPlayerCraft(e: CraftItemEvent) {
        if (!enabled) return
        val player = e.whoClicked as? Player ?: return
        val crafted = e.recipe.result
        for (recipe in RecipeHandler.recipes) {
            if (crafted.isSimilar(recipe.recipe!!.result)) {
                recipeNotified[player.uniqueId]?.remove(recipe.id)
            }
        }
    }

    private fun canCraft(player: Player, recipe: Recipe): Boolean {
        val inv = player.inventory
        if (recipe.recipe is ShapedRecipe) {
            for (ingredient in (recipe.recipe as ShapedRecipe).ingredientMap.values) {
                if (ingredient != null && !inv.containsAtLeast(ingredient, ingredient.amount)) {
                    return false
                }
            }
            return true
        }
        if (recipe.recipe is ShapelessRecipe) {
            for (ingredient in (recipe.recipe as ShapelessRecipe).ingredientList) {
                if (ingredient != null && !inv.containsAtLeast(ingredient, ingredient.amount)) {
                    return false
                }
            }
            return true
        }
        return false
    }

    override fun onStart() {
        Bukkit.resetRecipes()
        RecipeHandler.setup()
        for (player in Bukkit.getOnlinePlayers()) {
            if (SpecFeature.instance.isSpec(player)) continue
            player.maxHealth = 40.0
            player.health = 40.0
            player.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 6000, 0, false, false))
            player.addPotionEffect(PotionEffect(PotionEffectType.ABSORPTION, 6000, 0, false, false))
            if (kits[player.uniqueId] == null) {
                kits[player.uniqueId] = "leather"
            }
            if (kits[player.uniqueId] == "leather") {
                val helmet = ItemBuilder(Material.LEATHER_HELMET)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                    .make()
                val chestplate = ItemBuilder(Material.LEATHER_CHESTPLATE)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                    .make()
                val leggings = ItemBuilder(Material.LEATHER_LEGGINGS)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                    .make()
                val boots = ItemBuilder(Material.LEATHER_BOOTS)
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3)
                    .make()
                player.inventory.helmet = helmet
                player.inventory.chestplate = chestplate
                player.inventory.leggings = leggings
                player.inventory.boots = boots
            } else if (kits[player.uniqueId] == "enchanter") {
                val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.BOOK, 4),
                        ItemStack(Material.EXP_BOTTLE, 15),
                        ItemStack(Material.INK_SACK, 18, 4),
                        pickaxe
                    )
                )
            } else if (kits[player.uniqueId] == "archer") {
                val shovel = ItemBuilder(Material.STONE_SPADE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.STRING, 6),
                        ItemStack(Material.FEATHER, 9),
                        shovel
                    )
                )
            } else if (kits[player.uniqueId] == "stoneGear") {
                val shovel = ItemBuilder(Material.STONE_SPADE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                val axe = ItemBuilder(Material.STONE_AXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                val sword = ItemBuilder(Material.STONE_SWORD)
                    .make()
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        shovel,
                        pickaxe,
                        axe,
                        sword
                    )
                )
            } else if (kits[player.uniqueId] == "lunchBox") {
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.COOKED_BEEF, 9),
                        ItemStack(Material.CARROT, 12),
                        ItemStack(Material.MELON, 2),
                        ItemStack(Material.APPLE, 3),
                        ItemStack(Material.GOLD_INGOT, 3),
                    )
                )
            } else if (kits[player.uniqueId] == "looter") {
                val sword = ItemBuilder(Material.STONE_SWORD)
                    .addEnchantment(Enchantment.LOOT_BONUS_MOBS, 1)
                    .make()
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.BONE, 3),
                        ItemStack(Material.SLIME_BALL, 3),
                        ItemStack(Material.SULPHUR, 2),
                        ItemStack(Material.SPIDER_EYE, 2),
                        sword
                    )
                )
            } else if (kits[player.uniqueId] == "ecologist") {
                val pickaxe = ItemBuilder(Material.STONE_PICKAXE)
                    .addEnchantment(Enchantment.DIG_SPEED, 3)
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .make()
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.VINE, 21),
                        ItemStack(Material.WATER_LILY, 64),
                        ItemStack(Material.SUGAR_CANE, 12),
                        pickaxe
                    )
                )
            } else if (kits[player.uniqueId] == "farmer") {
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.IRON_HOE),
                        ItemStack(Material.MELON, 3),
                        ItemStack(Material.CARROT, 3),
                        ItemStack(Material.INK_SACK, 15),
                    )
                )
            } else if (kits[player.uniqueId] == "horseman") {
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.LEATHER, 12),
                        ItemStack(Material.HAY_BLOCK, 1),
                        ItemStack(Material.STRING, 4),
                        ItemStack(Material.IRON_BARDING),
                        SpawnEgg(EntityType.HORSE).toItemStack(1)
                    )
                )
            } else if (kits[player.uniqueId] == "trapper") {
                PlayerUtils.bulkItems(
                    player, arrayListOf(
                        ItemStack(Material.PISTON_BASE, 8),
                        ItemStack(Material.PISTON_STICKY_BASE, 8),
                        ItemStack(Material.REDSTONE, 25),
                        ItemStack(Material.LOG, 16)
                    )
                )
            }
            val book = ItemBuilder(Material.ENCHANTED_BOOK)
                .name("<red>Crafting Recipes")
                .addLore("<gray>Click to open & view a list of crafting recipes.")
                .make()
            PlayerUtils.bulkItems(
                player, arrayListOf(
                    book
                )
            )
        }
        object : BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.inventory.itemInHand != null && player.inventory.itemInHand.hasItemMeta() && player.inventory.itemInHand.itemMeta.displayName == Chat.colored("<yellow>Andūril")) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20, 0, false, true))
                        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 0, false, true))
                    }
                    if (player.inventory.chestplate != null && player.inventory.chestplate.hasItemMeta() && player.inventory.chestplate.itemMeta.displayName == Chat.colored("<yellow>Barbarian Chestplate")) {
                        player.addPotionEffect(PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 0, false, true))
                        player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 0, false, true))
                    }
                    if (player.inventory.boots != null && player.inventory.boots.hasItemMeta() && player.inventory.boots.itemMeta.displayName == Chat.colored("<yellow>Hermes' Boots")) {
                        player.walkSpeed = 0.2F + 0.02F
                    } else {
                        player.walkSpeed = 0.2F
                    }
                }
            }
        }.runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
    }

    private val rand = Random()

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String?,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this command.")
            return true
        }
        if (!enabled) {
            sender.sendMessage("$prefix Champions is not enabled.")
            return true
        }
        val gui = GuiBuilder().rows(2).name("<red>Champions Kit Selector").owner(sender)
        val leatherKit = ItemBuilder(Material.LEATHER_CHESTPLATE)
            .name("<green>Leather Armor")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with a full set of ")
            .addLore("<gray>protection III leather armor.")
            .make()
        val enchanterKit = ItemBuilder(Material.BOOK)
            .name("<green>Enchanting Set")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with 4 books, 15")
            .addLore("<gray>bottles, 18 lapis and an")
            .addLore("<gray>efficiency 3, unbreaking 1 stone")
            .addLore("<gray>pickaxe.")
        val archerKit = ItemBuilder(Material.BOW)
            .name("<green>Archer Set")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with 6 strings, 9")
            .addLore("<gray>feathers and an efficiency 3,")
            .addLore("<gray>unbreaking 1 stone shovel.")
            .make()
        val stoneGear = ItemBuilder(Material.STONE_PICKAXE)
            .name("<green>Stone Gear")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with a full set of stone")
            .addLore("<gray>tools with efficiency III and")
            .addLore("<gray>unbreaking I.")
            .make()
        val lunchBox = ItemBuilder(Material.APPLE)
            .name("<green>Lunch Box")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with 9 steaks, 12")
            .addLore("<gray>carrots, 2 melon slices, 2 gold")
            .addLore("<gray>ingots and 3 apples.")
            .make()
        val looter = ItemBuilder(Material.BONE)
            .name("<green>Looter")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with 3 bones, 3 slime")
            .addLore("<gray>balls, 2 gunpowder, 2 spider")
            .addLore("<gray>eyes and a looting 1 stone")
            .addLore("<gray>sword.")
            .make()
        val ecologist = ItemBuilder(Material.IRON_AXE)
            .name("<green>Ecologist")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with 21 vines, 64 lily")
            .addLore("<gray>pads, 12 sugar cane and an")
            .addLore("<gray>efficiency 3, unbreaking 1 stone")
            .addLore("<gray>pickaxe.")
            .make()
        val farmer = ItemBuilder(Material.SEEDS)
            .name("<green>Farmer")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with an iron hoe, 3")
            .addLore("<gray>melon slices, 3 carrots and 4")
            .addLore("<gray>bonemeal.")
            .make()
        val horseman = ItemBuilder(Material.SADDLE)
            .name("<green>Horseman")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with 12 leather, 1 hay")
            .addLore("<gray>bale, 4 string, gold horse armor")
            .addLore("<gray>and a horse spawn egg.")
            .make()
        val trapper = ItemBuilder(Material.PISTON_BASE)
            .name("<green>Trapper")
            .addLore("&6Kit:")
            .addLore("<gray>Spawn with 8 pistons, 8 sticky")
            .addLore("<gray>pistons, 25 redstone and 16 oak")
            .addLore("<gray>logs.")
            .make()
        gui.item(0, leatherKit).onClick runnable@{
            if (this.kits[sender.uniqueId] == "leather") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "leather"
            Chat.sendMessage(sender, "<green>You have selected the &6Leather Armor<green> kit.")
        }
        gui.item(1, enchanterKit.make()).onClick runnable@{
            if (this.kits[sender.uniqueId] == "enchanter") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "enchanter"
            Chat.sendMessage(sender, "<green>You have selected the &6Enchanting Set<green> kit.")
        }
        gui.item(2, archerKit).onClick runnable@{
            if (this.kits[sender.uniqueId] == "archer") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "archer"
            Chat.sendMessage(sender, "<green>You have selected the &6Archer Set<green> kit.")
        }
        gui.item(3, stoneGear).onClick runnable@{
            if (this.kits[sender.uniqueId] == "stoneGear") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "stoneGear"
            Chat.sendMessage(sender, "<green>You have selected the &6Stone Gear<green> kit.")
        }
        gui.item(4, lunchBox).onClick runnable@{
            if (this.kits[sender.uniqueId] == "lunchBox") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "lunchBox"
            Chat.sendMessage(sender, "<green>You have selected the &6Lunch Box<green> kit.")
        }
        gui.item(5, looter).onClick runnable@{
            if (this.kits[sender.uniqueId] == "looter") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "looter"
            Chat.sendMessage(sender, "<green>You have selected the &6Looter<green> kit.")
        }
        gui.item(6, ecologist).onClick runnable@{
            if (this.kits[sender.uniqueId] == "ecologist") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "ecologist"
            Chat.sendMessage(sender, "<green>You have selected the &6Ecologist<green> kit.")
        }
        gui.item(7, farmer).onClick runnable@{
            if (this.kits[sender.uniqueId] == "farmer") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "farmer"
            Chat.sendMessage(sender, "<green>You have selected the &6Farmer<green> kit.")
        }
        gui.item(8, horseman).onClick runnable@{
            if (this.kits[sender.uniqueId] == "horseman") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "horseman"
            Chat.sendMessage(sender, "<green>You have selected the &6Horseman<green> kit.")
        }
        gui.item(9, trapper).onClick runnable@{
            if (this.kits[sender.uniqueId] == "trapper") {
                Chat.sendMessage(sender, "<red>You already have this kit.")
                return@runnable
            }
            this.kits[sender.uniqueId] = "trapper"
            Chat.sendMessage(sender, "<green>You have selected the &6Trapper<green> kit.")
        }
        sender.openInventory(gui.make())
        return true
    }
}
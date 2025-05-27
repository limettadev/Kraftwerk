package pink.mino.kraftwerk.utils.recipes

import me.lucko.helper.utils.Log
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.scenarios.ScenarioHandler
import pink.mino.kraftwerk.utils.Chat
import pink.mino.kraftwerk.utils.recipes.list.*
import java.util.*

class RecipeHandler : Listener {
    val crafts: HashMap<UUID, HashMap<String, Int>> = hashMapOf()

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (!ScenarioHandler.getActiveScenarios().contains(ScenarioHandler.getScenario("champions"))) return
        if (crafts[e.player.uniqueId] == null) crafts[e.player.uniqueId] = hashMapOf()
    }

    @EventHandler
    fun onPlayerCraft(e: CraftItemEvent) {
        val player = e.whoClicked as Player
        val item = e.inventory.result
        val craftItem = CraftItemStack.asNMSCopy(item)
        if (craftItem.hasTag()) {
            val tag = craftItem.tag
            if (tag.getString("uhcId") != null) {
                if (crafts[player.uniqueId]!![getRecipe(tag.getString("uhcId"))!!.id] == null) {
                    crafts[player.uniqueId]!![getRecipe(tag.getString("uhcId"))!!.id] = getRecipe(tag.getString("uhcId"))!!.crafts
                    crafts[player.uniqueId]!![getRecipe(tag.getString("uhcId"))!!.id] = crafts[player.uniqueId]!![getRecipe(tag.getString("uhcId"))!!.id]!! - 1
                    Chat.sendMessage(
                        player,
                        "$prefix You crafted a &9${item.itemMeta.displayName}&e! &8(&b${
                            crafts[player.uniqueId]!![getRecipe(tag.getString("uhcId"))!!.id]!!
                        }&8/&b${getRecipe(tag.getString("uhcId"))!!.crafts}&8)"
                    )
                    Log.info("[Recipes] Allowing player to craft ${getRecipe(tag.getString("uhcId"))!!.id.uppercase()} recipe.")
                    craftItem.tag.remove("uhcId")
                    e.inventory.result = CraftItemStack.asBukkitCopy(craftItem)
                } else {
                    if (crafts[player.uniqueId]!![getRecipe(tag.getString("uhcId"))!!.id]!! <= 0) {
                        Chat.sendMessage(player, "$prefix &7You ran out of crafts for this item!")
                        e.isCancelled = true
                        return
                    }
                    crafts[player.uniqueId]!![getRecipe(tag.getString("uhcId"))!!.id] = crafts[player.uniqueId]!![getRecipe(tag.getString("uhcId"))!!.id]!! - 1
                    Chat.sendMessage(
                        player,
                        "$prefix &eYou crafted a &9${item.itemMeta.displayName}&e! &8(&b${
                            crafts[player.uniqueId]!![getRecipe(tag.getString("uhcId"))!!.id]!!
                        }&8/&b${getRecipe(tag.getString("uhcId"))!!.crafts}&8)"
                    )
                    Log.info("[Recipes] Allowing player to craft ${getRecipe(tag.getString("uhcId"))!!.id.uppercase()} recipe.")
                    craftItem.tag.remove("uhcId")
                    e.inventory.result = CraftItemStack.asBukkitCopy(craftItem)
                }
            }
        }
    }

    companion object {
        var recipes = ArrayList<Recipe>()
        val prefix = "&8[${Chat.primaryColor}&lUHC&8]&7"

        fun setup() {
            recipes = arrayListOf()
            Bukkit.resetRecipes()
            addRecipe(ArtemisBowRecipe())
            addRecipe(VorpalSwordRecipe())
            addRecipe(BookOfSharpeningRecipe())
            addRecipe(BookOfPowerRecipe())
            addRecipe(DragonSwordRecipe())
            addRecipe(LeatherEconomyRecipe())
            addRecipe(ArtemisBookRecipe())
            addRecipe(DragonArmorRecipe())
            addRecipe(DustOfLightRecipe())
            addRecipe(NectarRecipe())
            addRecipe(BrewingArtifactRecipe())
            addRecipe(FlamingArtifactRecipe())
            addRecipe(DeliciousMealRecipe())
            addRecipe(PotionOfToughnessRecipe())
            addRecipe(SpikedArmorRecipe())
            addRecipe(SevenLeagueBootsRecipe())
            addRecipe(IronEconomyRecipe())
            addRecipe(ObsidianRecipe())
            addRecipe(TarnhelmRecipe())
            addRecipe(PhilosophersPickaxeRecipe())
            addRecipe(EnlighteningPackRecipe())
            addRecipe(LightAnvilRecipe())
            addRecipe(LightEnchantingTableRecipe())
            addRecipe(BookOfThothRecipe())
            addRecipe(EvesTemptationRecipe())
            addRecipe(HealingFruitRecipe())
            addRecipe(HolyWaterRecipe())
            addRecipe(LightAppleRecipe())
            addRecipe(GoldenHeadRecipe())
            addRecipe(PandorasBoxRecipe())
            addRecipe(PanaceaRecipe())
            addRecipe(CupidsBowRecipe())
            addRecipe(ArrowEconomyRecipe())
            addRecipe(SaddleRecipe())
            addRecipe(PotionOfVelocityRecipe())
            addRecipe(FenrirRecipe())
            addRecipe(ForgeRecipe())
            addRecipe(QuickPickRecipe())
            addRecipe(LumberjackAxeRecipe())
            addRecipe(ApprenticeHelmetRecipe())
            addRecipe(GoldPackRecipe())
            addRecipe(SugarRushRecipe())
            addRecipe(FlaskOfIchorRecipe())
            addRecipe(ExodusRecipe())
            addRecipe(HideOfLeviathanRecipe())
            addRecipe(TabletsOfDestinyRecipe())
            addRecipe(AxeOfPerunRecipe())
            addRecipe(AndurilRecipe())
            addRecipe(DeathsScytheRecipe())
            addRecipe(ChestOfFateRecipe())
            addRecipe(EssenceOfYggdrasilRecipe())
            addRecipe(DeusExMachinaRecipe())
            addRecipe(DiceOfGodRecipe())
            addRecipe(KingsRodRecipe())
            addRecipe(DaredevilRecipe())
            addRecipe(ExcaliburRecipe())
            addRecipe(ShoesOfVidarRecipe())
            addRecipe(BloodlustRecipe())
            addRecipe(PotionOfVitalityRecipe())
            addRecipe(ModularBowRecipe())
            addRecipe(HermesBootsRecipe())
            addRecipe(BarbarianChestplateRecipe())
            addRecipe(ApprenticeSwordRecipe())
            addRecipe(ApprenticeBowRecipe())
            addRecipe(PotionOfVitalityRecipe())
            addRecipe(CornucopiaRecipe())
            //TODO: Add Apprentice Sword
            //TODO: Add Apprentice Bow
        }

        fun addRecipe(recipe: Recipe) {
            recipes.add(recipe)
            Log.info("[Recipes] Added ${recipe.name} crafting recipe.")
            Bukkit.getServer().pluginManager.registerEvents(recipe, Kraftwerk.instance)
            if (recipe.recipe is ShapedRecipe || recipe.recipe is ShapelessRecipe) {
                Bukkit.getServer().addRecipe(recipe.recipe)
            } else {
                throw Error("You must include a valid ShapedRecipe or ShapelessRecipe")
            }
        }

        fun getRecipe(id: String?): Recipe? {
            for (recipe in recipes) {
                if (recipe.id == id) {
                    return recipe
                }
            }
            return null
        }
    }
}
package pink.mino.kraftwerk.utils.recipes

import me.lucko.helper.utils.Log
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
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
        val item = e.inventory.result ?: return
        val meta = item.itemMeta ?: return
        val key = NamespacedKey(JavaPlugin.getPlugin(Kraftwerk::class.java), "uhcId")
        val uhcId = meta.persistentDataContainer.get(key, PersistentDataType.STRING) ?: return
        val recipe = getRecipe(uhcId) ?: return

        if (crafts[player.uniqueId]!![recipe.id] == null) {
            crafts[player.uniqueId]!![recipe.id] = recipe.crafts
            crafts[player.uniqueId]!![recipe.id] = crafts[player.uniqueId]!![recipe.id]!! - 1
            Chat.sendMessage(
                player,
                "$prefix You crafted a <blue>${meta.displayName}<yellow>! <dark_gray>(<aqua>${crafts[player.uniqueId]!![recipe.id]!!}<dark_gray>/<aqua>${recipe.crafts}<dark_gray>)"
            )
            Log.info("[Recipes] Allowing player to craft ${recipe.id.uppercase()} recipe.")
            meta.persistentDataContainer.remove(key)
            item.itemMeta = meta
            e.inventory.result = item
        } else {
            if (crafts[player.uniqueId]!![recipe.id]!! <= 0) {
                Chat.sendMessage(player, "$prefix <gray>You ran out of crafts for this item!")
                e.isCancelled = true
                return
            }
            crafts[player.uniqueId]!![recipe.id] = crafts[player.uniqueId]!![recipe.id]!! - 1
            Chat.sendMessage(
                player,
                "$prefix <yellow>You crafted a <blue>${meta.displayName}<yellow>! <dark_gray>(<aqua>${crafts[player.uniqueId]!![recipe.id]!!}<dark_gray>/<aqua>${recipe.crafts}<dark_gray>)"
            )
            Log.info("[Recipes] Allowing player to craft ${recipe.id.uppercase()} recipe.")
            meta.persistentDataContainer.remove(key)
            item.itemMeta = meta
            e.inventory.result = item
        }
    }

    companion object {
        var recipes = ArrayList<Recipe>()
        val prefix = "<dark_gray>[${Chat.primaryColor}<bold>UHC<dark_gray>]<gray>"

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
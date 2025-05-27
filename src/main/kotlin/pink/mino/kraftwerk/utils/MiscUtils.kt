package pink.mino.kraftwerk.utils

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.awt.Color
import kotlin.math.floor

class MiscUtils {
    companion object {
        fun timeToString(ticks: Long): String {
            var t = ticks
            val hours = floor(t / 3600.toDouble()).toInt()
            t -= hours * 3600
            val minutes = floor(t / 60.toDouble()).toInt()
            t -= minutes * 60
            val seconds = t.toInt()
            val output = StringBuilder()
            if (hours > 0) {
                output.append(hours).append('h')
                if (minutes == 0) {
                    output.append(minutes).append('m')
                }
            }
            if (minutes > 0) {
                output.append(minutes).append('m')
            }
            output.append(seconds).append('s')
            return output.toString()
        }

        fun hexToColor(hex: String): Color {
            var hex = hex
            require(!(hex == null || hex.isEmpty())) { "Hex color string cannot be null or empty" }


            // Remove # if present
            if (hex.startsWith("#")) {
                hex = hex.substring(1)
            }


            // Handle 3-digit hex (e.g., "F0A" -> "FF00AA")
            if (hex.length == 3) {
                hex = "" + hex.get(0) + hex.get(0) +
                        hex.get(1) + hex.get(1) +
                        hex.get(2) + hex.get(2)
            }


            // Validate hex string
            require(hex.length == 6) { "Invalid hex color format: " + hex }

            try {
                val r = hex.substring(0, 2).toInt(16)
                val g = hex.substring(2, 4).toInt(16)
                val b = hex.substring(4, 6).toInt(16)

                return Color(r, g, b)
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Invalid hex color format: " + hex)
            }
        }

        fun populateCrafting(gui: GuiBuilder): GuiBuilder {
            for (i in 0..44) {
                val blank = ItemBuilder(Material.STAINED_GLASS_PANE)
                    .noAttributes()
                    .setDurability(8)
                    .name(" ")
                    .make()
                gui.item(i, blank).onClick runnable@{
                    it.isCancelled = true
                }
            }
            val result = ItemBuilder(Material.STAINED_GLASS_PANE)
                .noAttributes()
                .setDurability(5)
                .name("&aResult")
                .addLore("&7Crafting recipe result ->")
                .make()
            gui.item(23, result).onClick runnable@{
                it.isCancelled = true
            }
            val slots = listOf(
                10, 11, 12,
                19, 20, 21,
                28, 29, 30,
                25
            )
            for (i in slots) {
                gui.item(i, ItemStack(Material.AIR)).onClick runnable@{
                    it.isCancelled = true
                }
            }
            return gui
        }
    }
}
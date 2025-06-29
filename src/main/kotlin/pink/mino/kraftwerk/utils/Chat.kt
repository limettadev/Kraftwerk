package pink.mino.kraftwerk.utils

import DefaultFontInfo
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import pink.mino.kraftwerk.features.ConfigFeature
import pink.mino.kraftwerk.features.SpecFeature

class Chat {
    companion object {
        private const val CENTER_PX = 154

        val secondaryColor = if (ConfigFeature.instance.config!!.getString("chat.secondaryColor") != null) ConfigFeature.instance.config!!.getString("chat.secondaryColor") else "<white>"
        val primaryColor = if (ConfigFeature.instance.config!!.getString("chat.primaryColor") != null) ConfigFeature.instance.config!!.getString("chat.primaryColor") else "<red>"
        val serverName = if (ConfigFeature.instance.config!!.getString("chat.serverName") != null) ConfigFeature.instance.config!!.getString("chat.serverName") else "applejuice"
        val scoreboardTitle = if (ConfigFeature.instance.config!!.getString("chat.scoreboardTitle") != null) ConfigFeature.instance.config!!.getString("chat.scoreboardTitle") else "<red>apple<green>juice"
        val prefix = if (ConfigFeature.instance.config!!.getString("chat.prefix") != null) (ConfigFeature.instance.config!!.getString("chat.prefix")) else "<dark_gray>[<red>UHC<dark_gray>]<gray>"
        const val dash = "<dark_gray>»<gray>"
        const val dot = "<dark_gray>●<gray>"
        const val line = "<dark_gray><strikethrough>-----------------------------------------------------"
        const val guiLine = "<dark_gray><strikethrough>-------------------"

        fun colored(message: String): Component {
            return MiniMessage.miniMessage().deserialize(message)
        }

        fun broadcast(message: String) {
            Bukkit.broadcast(colored(message))
        }

        fun clear() {
            for (player in Bukkit.getOnlinePlayers()) {
                if (SpecFeature.instance.isSpec(player)) continue
                for (i in 1..1000) {
                    player.sendMessage(" ")
                    player.sendMessage("  ")
                }
            }
        }

        fun scenarioTextWrap(text: String, width: Int): ArrayList<Component> {
            val words = text.split(" ")
            val lines = ArrayList<Component>()
            var currentLine = ""
            for (word in words) {
                if (currentLine.length + word.length + 1 > width) {
                    lines.add(colored("<gray>${currentLine}"))
                    currentLine = "<gray>$word "
                } else {
                    currentLine += "<gray>$word "
                }
            }
            lines.add(colored("<gray>${currentLine}"))
            return lines
        }

        /* Function to get centered MOTDs */
        fun centerMotd(message: String): String {
            val text = ChatColor.translateAlternateColorCodes('&', message)
            var messagePxSize = 0
            var previousCode = false
            var isBold = false
            var charIndex = 0
            var lastSpaceIndex = 0
            var toSendAfter: String? = null
            var recentColorCode = ""
            for (c in text.toCharArray()) {
                if (c == '§') {
                    previousCode = true
                    continue
                } else if (previousCode) {
                    previousCode = false
                    recentColorCode = "§$c"
                    if (c == 'l' || c == 'L') {
                        isBold = true
                        continue
                    } else {
                        isBold = false
                    }
                } else if (c == ' ') {
                    lastSpaceIndex = charIndex
                } else {
                    val dFI: DefaultFontInfo = DefaultFontInfo.getDefaultFontInfo(c)
                    messagePxSize += if (isBold) dFI.boldLength else dFI.length
                    messagePxSize++
                }
                if (messagePxSize >= 240) {
                    toSendAfter = recentColorCode + text.substring(lastSpaceIndex + 1, text.length)
                    text.substring(0, lastSpaceIndex + 1)
                    break
                }
                charIndex++
            }
            val halvedMessageSize = messagePxSize / 2
            val toCompensate = CENTER_PX - halvedMessageSize
            val spaceLength = DefaultFontInfo.SPACE.length + 1
            var compensated = 0
            val sb = StringBuilder()
            while (compensated < toCompensate) {
                sb.append(" ")
                compensated += spaceLength
            }
            if (toSendAfter != null) {
                centerMotd(toSendAfter)
            }
            return sb.toString() + text
        }

        /* Function to send centered messages to players */
        fun sendCenteredMessage(player: CommandSender, message: String?) {
            var text = message
            if (text == null || text == "") player.sendMessage("")
            text = ChatColor.translateAlternateColorCodes('&', message!!)
            var messagePxSize = 0
            var previousCode = false
            var isBold = false
            for (c in text.toCharArray()) {
                if (c == '§') {
                    previousCode = true
                    continue
                } else if (previousCode) {
                    previousCode = false
                    if (c == 'l' || c == 'L') {
                        isBold = true
                        continue
                    } else isBold = false
                } else {
                    val dFI: DefaultFontInfo = DefaultFontInfo.getDefaultFontInfo(c)
                    messagePxSize += if (isBold) dFI.boldLength else dFI.length
                    messagePxSize++
                }
            }
            val halvedMessageSize = messagePxSize / 2
            val toCompensate = CENTER_PX - halvedMessageSize
            val spaceLength = DefaultFontInfo.SPACE.length + 1
            var compensated = 0
            val sb = StringBuilder()
            while (compensated < toCompensate) {
                sb.append(" ")
                compensated += spaceLength
            }
            player.sendMessage(sb.toString() + text)
        }

        /* Simple function to send colored messages to players */
        fun sendMessage(player: CommandSender, message: String?) {
            var text = message
            if (text == null || text == "") player.sendMessage("")
            text = ChatColor.translateAlternateColorCodes('&', message!!)
            player.sendMessage(text)
        }
    }
}
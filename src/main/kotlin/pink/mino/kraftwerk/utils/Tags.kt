package pink.mino.kraftwerk.utils

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pink.mino.kraftwerk.Kraftwerk

enum class Tags(
    val display: String,
    val item: Material
) {
    FLOWER("<dark_gray>[&d✿<dark_gray>]&r", Material.POPPY),
    SMILEY("&b(◕‿◕)&r", Material.POTATO),
    HEART("<dark_gray>[<red>❤<dark_gray>]&r", Material.GOLDEN_APPLE),

    MUSICAL("<dark_gray>[&b♫<dark_gray>]&r", Material.NOTE_BLOCK),
    STAR("<dark_gray>[<yellow>✯<dark_gray>]&r", Material.GOLD_BLOCK),
    YIN_YANG("<dark_gray>[&f☯<dark_gray>]&r", Material.ENDER_PEARL),
    PEACE("<dark_gray>[&d☮<dark_gray>]&r", Material.BREWING_STAND)
}

class GrantTagCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender.name != "minota") {
            if (sender is Player) {
                Chat.sendMessage(sender, "<red>This command can only be executed by console.")
                return false
            }
        }
        val player = Bukkit.getOfflinePlayer(args[0])
        val profile = Kraftwerk.instance.profileHandler.getProfile(player.uniqueId)!!
        profile.unlockedTags.add(args[1].uppercase())
        Kraftwerk.instance.profileHandler.saveProfile(profile)
        if (player.isOnline) {
            Chat.sendMessage(player as Player, "${Chat.prefix} You've been granted the <yellow>${args[1].uppercase()} ${Tags.valueOf(args[1].uppercase()).display}<gray> tag.")
        }
        return true
    }
}

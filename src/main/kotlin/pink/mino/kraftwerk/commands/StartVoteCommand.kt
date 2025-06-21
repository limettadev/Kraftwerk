package pink.mino.kraftwerk.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import pink.mino.kraftwerk.Kraftwerk
import pink.mino.kraftwerk.utils.Chat

class VoteTimer(private val vote: Vote) : BukkitRunnable() {
    private var timer = 20
    override fun run() {
        timer -= 1
        if (timer == 0) {
            Bukkit.broadcastMessage(Chat.colored(Chat.line))
            Bukkit.broadcastMessage(Chat.colored(" <gray>Vote results: <green>${vote.yes} yes(s) <gray>/ <red>${vote.no} no(s)"))
            Bukkit.broadcastMessage(Chat.colored(" <gray>Question: ${Chat.primaryColor}${vote.question}"))
            Bukkit.broadcastMessage(Chat.colored(Chat.line))
            JavaPlugin.getPlugin(Kraftwerk::class.java).vote = null
            cancel()
        }
    }
}

class Vote(val question: String) {
    var yes = 0
    var no = 0
    var voted = arrayListOf<Player>()

    fun startTimer() {
        for (online: Player in Bukkit.getOnlinePlayers()) {
            online.sendTitle(Chat.colored("${Chat.primaryColor}&lNew Vote!"), Chat.colored("<gray>Vote using <green>/yes <gray>or <red>/no<gray>!"))
        }
        VoteTimer(this).runTaskTimer(JavaPlugin.getPlugin(Kraftwerk::class.java), 0L, 20L)
        Bukkit.broadcastMessage(Chat.colored("${Chat.prefix} Poll: ${Chat.primaryColor}${question} <dark_gray>|<gray> Use <green>/yes <gray>or <red>/no<gray> to respond."))
    }
}

class StartVoteCommand : CommandExecutor {
    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): Boolean {
        val message = StringBuilder()
        if (args.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}Usage: /startvote <question>")
            return true
        }
        for (element in args) {
            message.append(element).append(" ")
        }
        val question = message.toString().trim()
        if (JavaPlugin.getPlugin(Kraftwerk::class.java).vote != null) {
            Chat.sendMessage(sender, "<red>There's already a poll currently running.")
            return false
        }
        Chat.sendMessage(sender, "${Chat.prefix} Starting poll...")
        JavaPlugin.getPlugin(Kraftwerk::class.java).vote = Vote(question)
        JavaPlugin.getPlugin(Kraftwerk::class.java).vote!!.startTimer()
        return true
    }
}
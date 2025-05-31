package pink.mino.kraftwerk.discord

import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import pink.mino.kraftwerk.discord.listeners.ButtonInteraction
import pink.mino.kraftwerk.discord.listeners.MemberJoin
import pink.mino.kraftwerk.discord.listeners.SlashCommand
import pink.mino.kraftwerk.features.ConfigFeature
import javax.security.auth.login.LoginException


class Discord : ListenerAdapter() {
    companion object {
        var instance: JDA? = null

        fun main() {
            if (ConfigFeature.instance.config!!.getString("discord.token") == null || ConfigFeature.instance.config!!.getString("discord.token") == "") {
                throw(LoginException("No token found in config.yml"))
            }
            val jda = JDABuilder.createLight(
                ConfigFeature.instance.config!!.getString("discord.token"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_VOICE_STATES
            )
                .enableCache(CacheFlag.VOICE_STATE)
                .addEventListeners(MemberJoin())
                .addEventListeners(SlashCommand())
                .addEventListeners(ButtonInteraction())
                .build()
            val commands = jda.updateCommands()

            commands.addCommands(
                Commands.slash("online", "View how many players are online on the server."),
                Commands.slash("ip", "View the IP for the server."),
                Commands.slash("togglealerts", "Removes/adds the Notify role in the Discord server."),
                Commands.slash("scenarios", "Sends a list of scenarios available on the server."),
                Commands.slash("wl", "Attempts to whitelist yourself on the server if the conditions are met.")
                    .addOption(OptionType.STRING, "ign", "The player you want to be whitelisted.", true)
            )

            instance = jda
            commands.queue()
        }
    }
}
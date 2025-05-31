package pink.mino.kraftwerk.discord.listeners

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import pink.mino.kraftwerk.Kraftwerk

class ButtonInteraction : ListenerAdapter() {
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.componentId != "toggle_alerts") return

        val member = event.member ?: return
        val guild = event.guild ?: return
        val alertsRoleId = Kraftwerk.instance.alertsRoleId ?: return
        val role = guild.getRoleById(alertsRoleId) ?: return

        val hasRole = member.roles.contains(role)

        val action = if (hasRole)
            guild.removeRoleFromMember(member, role)
        else
            guild.addRoleToMember(member, role)

        val replyText = if (hasRole)
            "✅ You will no longer receive matchpost alerts."
        else
            "🔔 You will now receive matchpost alerts."

        action.queue {
            event.reply(replyText).setEphemeral(true).queue()
        }
    }

}
package modules

import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent

@ModuleID("Welcomer")
class WelcomeModule(private val message : String, private val roleID : String) : ListenerModule() {
	override val name: String
		get() = "Welcomer"

	override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
		event.guild.addRoleToMember(event.member, getRole(event)).complete()
		event.user.openPrivateChannel().complete().sendMessage(message).complete()
	}

	private fun getRole(event: GuildMemberJoinEvent) = event.guild.getRoleById(roleID)!!
}
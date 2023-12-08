package modules.voiceChannel

import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.guild.GenericGuildEvent
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent

@ModuleID("Voice Channel Manager")
class VCModule(private val roleId : String, private val vcChannelID : String) : ListenerModule() {
	override val name = "Voice Channel Manager"

	override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
		if (event.channelJoined != null) {
			event.guild.addRoleToMember(event.member, role(event)).complete()
		}
		if (event.channelLeft != null) {

			event.guild.removeRoleFromMember(event.member, role(event)).complete()
			if (event.guild.voiceChannels.all {it.members.all {m -> m.user.isBot}}) {
				clearMessageHistory(event)
			}
		}
	}

	private fun clearMessageHistory(e : GenericGuildEvent) {
		val tc = e.guild.getTextChannelById(vcChannelID) ?: return
		var messages = tc.history.retrievePast(50).complete()
		while (messages.size > 0) {
			if (messages.size == 1) {
				tc.deleteMessageById(messages[0].id).complete()
			} else {
				tc.deleteMessages(messages).complete()
			}
			messages = tc.history.retrievePast(50).complete()
		}
		for (threadChannel in tc.threadChannels) {
			threadChannel.delete().complete()
		}
	}

	private fun role(e : GenericGuildEvent) : Role = e.guild.getRoleById(roleId)!!

}
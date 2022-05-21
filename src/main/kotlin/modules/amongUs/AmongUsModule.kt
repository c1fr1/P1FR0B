package modules.amongUs

import bot.*
import bot.commands.*
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.*

@ModuleID("Among Us Room Manager")
class AmongUsModule(val tcTarget : String) : ListenerModule() {
	override val name: String
		get() = "Among Us Room Manager"

	var roomCode : String? = null
	var oldVCName : String? = null
	var vcTarget : VoiceChannel? = null

	@CommandFunction("Sets the among us room code",
		"sets the among us room code that is displayed in a vc, and the topic of a text channel\n\n" +
			"`/room <code>`\n" +
			"`/room clear`")
	fun room(e : SlashCommandInteractionEvent, bot : Bot, @CMDParam("new room code, or clear") code : String) : String {
		if (code == "clear") {
			if (vcTarget == null) {
				return "a room code was never set"
			}
			vcTarget!!.manager.setName(oldVCName!!).complete()
			bot.getGuild().getTextChannelById(tcTarget)?.manager
				?.setTopic("set room code with \"/room XXXXXX\"")?.complete()
			return "room code cleared"
		}
		return if (code.length == 6 && code.uppercase(Locale.getDefault()).all {it.isLetter()}) {
			setCode(code, e.member!!)
			"code is now $code"
		} else {
			"invalid code"
		}
	}

	override fun onMessageReceived(event : MessageReceivedEvent) {
		if (event.channel.id == tcTarget) {
			if (event.message.contentRaw.length == 6 &&
					event.message.contentRaw.all {it.isLetter()}) {
				setCode(event.message.contentRaw, event.member!!)
			}
		}
	}

	fun setCode(code : String, m : Member) {
		if (vcTarget == null) {
			vcTarget = findVC(m) ?: return
		}
		val tc = m.guild.getTextChannelById(tcTarget) ?: return
		roomCode = code.trim().uppercase(Locale.getDefault())
		if (oldVCName == null) {
			oldVCName = vcTarget!!.name
		}
		vcTarget!!.manager.setName("Among Us in: ${code.trim().uppercase(Locale.getDefault())}").complete()
		tc.manager.setTopic("room ID: ${code.trim().uppercase(Locale.getDefault())}").complete()
	}

	private fun findVC(m : Member) : VoiceChannel? {
		for (vc in m.guild.voiceChannels.filter { it.members.contains(m) }) {
			return vc
		}

		var max = -1
		var ret : VoiceChannel? = null

		for (vc in m.guild.voiceChannels) {
			if (vc.members.size > max) {
				ret = vc
				max = vc.members.size
			}
		}
		return ret
	}
}
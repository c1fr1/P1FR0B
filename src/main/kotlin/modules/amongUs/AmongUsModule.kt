package modules.amongUs

import bot.*
import bot.commands.Command
import bot.commands.GeneralCommandModule
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.VoiceChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.*

@ModuleID("Among Us Room Manager")
class AmongUsModule(val tcTarget : String) : ListenerModule() {
	override val name: String
		get() = "Among Us Room Manager"

	var roomCode : String? = null
	var oldVCName : String? = null
	var vcTarget : VoiceChannel? = null

	override fun onStartup(bot: Bot): Boolean {
		val commandModule = bot.resolveDependency(GeneralCommandModule::class) ?: return false
		commandModule.addCommands(Command("room",
			"sets the among us room code that is displayed in a vc, and the topic of a text channel\n\n" +
					"`${commandModule.prefix}room <room code>`\n" +
					"`${commandModule.prefix}room clear",
			this)
		{m, e, _ ->
			if (m.trim() == "clear") {
				if (vcTarget == null) {
					e.channel.sendMessage("a room code was never set").complete()
					return@Command
				}
				vcTarget!!.manager.setName(oldVCName!!).complete()
				e.guild.getTextChannelById(tcTarget)?.manager
					?.setTopic("set room code with \"${commandModule.prefix}room XXXXXX\"")?.complete()
				return@Command
			}
			if (m.trim().length == 6 && m.trim().uppercase(Locale.getDefault()).all {it.isLetter()}) {
				setCode(m, e.member!!)
			} else {
				e.channel.sendMessage("invalid code").complete()
			}
		})
		return super.onStartup(bot)
	}

	override fun onMessageReceived(event: MessageReceivedEvent) {
		if (event.channel.id == tcTarget) {
			if (event.message.contentRaw.length == 6 &&
					event.message.contentRaw.all {it.isLetter()}) {
				setCode(event.message.contentRaw, event.member!!)
			}
		}
	}

	fun setCode(code : String, m: Member) {
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
package modules

import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import modules.moduleCommunications.ContactableModule
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel

@ModuleID("Minecraft Trivia")
class MCTriviaModule : ListenerModule(), ContactableModule {
	override val name : String = "Minecraft Trivia"

	var lobbyVC : VoiceChannel? = null
	var teamVCs : ArrayList<VoiceChannel> = arrayListOf()

	override fun receiveMessage(message : String) : String? {
		val functionID = message.substringBefore("|")
		val body = message.substringAfter("|")
		return when (functionID) {
			"LinkUser" -> {
				val options = getBot().getGuild().members.filter { it.effectiveName.lowercase() == body.lowercase() }
				when (options.size) {
					0 -> "NoMatch"
					1 -> options.first().id
					else -> "MultipleMatches"
				}
			}
			"GetNames" -> getBot().getGuild().members.joinToString(",") {
				it.effectiveName.replace("\\", "\\\\").replace(",", "\\,")
			}

			"StartRound" -> {
				val teams = Json.decodeFromString<List<StartRoundObject>>(body)
				for (team in teams) {
					val members = team.ids.mapNotNull { getBot().getGuild().getMemberById(it) }.filter { member ->
						getBot().getGuild().voiceChannels.any { it.members.contains(member) }
					}
					val vc = getVoiceChannel(team.teamName)
					for (member in members) {
						getBot().getGuild().moveVoiceMember(member, vc).complete()
					}
				}
				null
			}
			"EndRound" -> {
				for (vc in teamVCs) {
					for (member in vc.members) {
						getBot().getGuild().moveVoiceMember(member, lobbyVC).complete()
					}
				}
				null
			}
			else -> null
		}
	}

	fun getVoiceChannel(teamName : String) : VoiceChannel {
		val targetVoiceChannelName = "Team $teamName"
		return teamVCs.firstOrNull { it.name == targetVoiceChannelName }
			?: getBot().getGuild().createVoiceChannel(targetVoiceChannelName, getLobbyVC().parentCategory).complete()
	}

	fun getLobbyVC() : VoiceChannel {
		val ret = lobbyVC
			?: getBot().getGuild().voiceChannels.filter { it.members.size > 0 && !teamVCs.contains(it) }.maxByOrNull { it.members.size }
			?: getBot().getGuild().voiceChannels.first()
		lobbyVC = ret
		return ret
	}

	@SlashCommand("", "")
	fun endMCTrivia() : String {
		val lobbyVC = getLobbyVC()
		for (vc in teamVCs) {
			for (member in vc.members) {
				lobbyVC.guild.moveVoiceMember(member, lobbyVC).queue()
			}
			vc.delete().complete()
		}
		teamVCs.clear()
		return "trivia vcs closed"
	}
}

@Serializable
class StartRoundObject(val teamName : String, val ids : List<Long>)
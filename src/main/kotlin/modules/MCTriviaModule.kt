package modules

import bot.Logger
import bot.commands.CMDParam
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import modules.moduleCommunications.ContactableModule
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

@ModuleID("Minecraft Trivia")
class MCTriviaModule : ListenerModule(), ContactableModule {
	override val name : String = "Minecraft Trivia"

	var lobbyVC : VoiceChannel? = null
	var teamVCs : ArrayList<VoiceChannel> = arrayListOf()

	val discordParticipants = ArrayList<TriviaParticipant>()

	override fun receiveMessage(message : String) : String? {
		val functionID = message.substringBefore("|")
		val body = message.substringAfter("|")
		Logger.verbose("received trivia call $functionID")
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
				val roundSummary = Json.decodeFromString<RoundSummary>(body)
				findLobbyVC()
				for (team in roundSummary.teams) {
					val members = team.ids.mapNotNull { getBot().getGuild().getMemberById(it) }.filter { member ->
						getBot().getGuild().voiceChannels.any { it.members.contains(member) }
					}.toMutableList()
					members.addAll(discordParticipants.filter { it.team.lowercase() == team.teamName.lowercase() }
						.mapNotNull { participant -> lobbyVC?.members?.firstOrNull {it.id == participant.user.id} })
					val vc = getVoiceChannel(team.teamName)
					if (members.size > 1) {
						for (member in members) {
							getBot().getGuild().moveVoiceMember(member, vc).complete()
						}
					}
				}
				for (participant in discordParticipants) {
					participant.currentAnswer = null
					participant.user.openPrivateChannel().queue {
						it.sendMessage(roundSummary.question).complete()
					}
				}
				Json.encodeToString(discordParticipants.map { it.team })
			}
			"EndRound" -> {
				for (vc in teamVCs) {
					for (member in vc.members) {
						getBot().getGuild().moveVoiceMember(member, findLobbyVC()).complete()
					}
				}
				for (participant in discordParticipants) {
					participant.user.openPrivateChannel().queue {
						it.sendMessage("Time up!").complete()
					}
				}
				val response = Json.encodeToString(discordParticipants.mapNotNull { it.currentAnswer?.let { ans -> RoundAnswer(it.team, ans)} })
				for (participant in discordParticipants) {
					participant.currentAnswer = null
				}
				response
			}
			"RenameTeam" -> {
				val obj = Json.decodeFromString<TeamRenameMessage>(body)
				for (participant in discordParticipants) {
					if (participant.team == obj.oldName) {
						participant.team = obj.newName
						participant.user.openPrivateChannel().queue {
							it.sendMessage("team name changed to \"${obj.newName}\"").complete()
						}
					}
				}
				null
			}
			"AddDiscordMembers" -> {
				val obj = Json.decodeFromString<AddDiscordMembersMessage>(body)

				for (member in obj.discordMembers) {
					val user = getBot().getGuild().members.firstOrNull {it.effectiveName == member} ?: continue
					discordParticipants.add(TriviaParticipant(user.user, obj.team))
					user.user.openPrivateChannel().queue {
						it.sendMessage("added to trivia team \"${obj.team}\"").complete()
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
			?: run {
				val ret = getBot().getGuild().createVoiceChannel(targetVoiceChannelName, findLobbyVC().parentCategory).complete()
				teamVCs.add(ret)
				ret
			}
	}

	fun findLobbyVC() : VoiceChannel {
		val ret = lobbyVC
			?: getBot().getGuild().voiceChannels.filter { it.members.size > 0 && !teamVCs.contains(it) }.maxByOrNull { it.members.size }
			?: getBot().getGuild().voiceChannels.first()
		lobbyVC = ret
		return ret
	}

	override fun onMessageReceived(event : MessageReceivedEvent) {
		if (event.isFromGuild) return
		val participant = discordParticipants.firstOrNull { it.user.id == event.author.id } ?: return
		if (participant.currentAnswer == null) {
			event.channel.sendMessage("Answer set")
		} else {
			event.channel.sendMessage("Answer changed")
		}.queue()

		participant.currentAnswer = event.message.contentDisplay
	}

	@SlashCommand("Joins the current game of trivia from discord", "Joins the current game of trivia from discord. You do not need to use this if you are in the minecraft server.")
	fun joinMcTrivia(e : SlashCommandInteractionEvent, @CMDParam("name of the team you wish to join") teamName : String) : String {
		discordParticipants.add(TriviaParticipant(e.user, teamName))
		return "Questions will be dmed to you by the bot, so make sure your dms are open.\n\n" +
				"To provide an answer, simply send the bot a message with your answer within the time limit. If multiple messages are sent only the most recent one is used so you can change your answer. Edits will not be considered.\n\n" +
				"If you are on a team, only one member of the team needs to provide an answer (either in discord or in the game)."
	}

	@SlashCommand("Cleans up voice channels created by this module", "Cleans up voice channels created by this module")
	fun endMcTrivia() : String {
		val lobbyVC = findLobbyVC()
		for (vc in teamVCs) {
			for (member in vc.members) {
				lobbyVC.guild.moveVoiceMember(member, lobbyVC).complete()
			}
			vc.delete().complete()
		}
		teamVCs.clear()
		this.lobbyVC = null
		discordParticipants.clear()
		return "trivia vcs closed"
	}
}

class TriviaParticipant(val user : User, var team : String) {
	var currentAnswer : String? = null
}

@Serializable
class RoundSummary(val question : String, val teams : List<TeamSummary>)

@Serializable
class TeamSummary(val teamName : String, val ids : List<Long>)

@Serializable
class RoundAnswer(val team : String, val answer : String)

@Serializable
class TeamRenameMessage(val oldName : String, val newName : String)

@Serializable
class AddDiscordMembersMessage(val team : String, val discordMembers : List<String>)
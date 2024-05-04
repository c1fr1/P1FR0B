package modules.nyt

import bot.IDS
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import java.time.LocalDate
import java.util.concurrent.CompletableFuture

object Connections {
	private val REFERENCE_NUMBER = 271
	private val REFERENCE_DATE =  LocalDate.of(2024, 3, 8)

	private fun dateFromNumber(number: Int) = REFERENCE_DATE.plusDays((number - REFERENCE_NUMBER).toLong())

	private val roleID = IDS["NYT_GAMES_ROLE"]!!

	fun handleMessage(message: Message) {
		val userID = (message.member ?: return).id
		val summary = message.contentRaw

		handleGameSubmission(userID, summary, message)
	}

	fun handleGameSubmission(userID : String, summary : String, message : Message) {
		val member = message.guild.getMemberById(userID) ?: return
		val game = Game.parse(message) ?: return
		val gameDay = dateFromNumber(game.puzzleNumber)
		val today = LocalDate.now()

		if (!isCommissionsUser(member)) return

		val data = ConnectionsData.get()

		val submitDay = data.getSubmitDay(gameDay) ?: run { message.reply("yeah not so sure about that puzzle number").submit(); return }
		val submitId = member.idLong

		if (submitDay.complete || submitDay.submittedIds.contains(submitId)) return

		data.addSubmittedId(submitDay, submitId)

		if (today != gameDay) return

		getSubmittedSet(message.guild, submitDay.submittedIds).thenAccept { (roleMembers, submittedMembers) ->
			if (submittedMembers.size == roleMembers.size) {
				data.completeDay(submitDay)
				message.channel.sendMessage("all connections submitted today\nyou may now send messages without spoilers").submit()
			} else {
				message.channel.sendMessage("${submittedMembers.size} out of ${roleMembers.size} connections submitted").submit()
			}
		}
	}

	fun getRoleUsers(guild: Guild): CompletableFuture<MutableList<Member>> {
		val role = guild.getRoleById(roleID) ?: return CompletableFuture.failedFuture(Exception("no role"))

		guild.findMembersWithRoles()
		val future = CompletableFuture<MutableList<Member>>()

		guild.findMembersWithRoles(role).onSuccess { future.complete(it) }.onError { future.completeExceptionally(it) }

		return future
	}

	fun isCommissionsUser(member: Member): Boolean {
		return member.roles.any { it.id == roleID }
	}

	data class SubmittedSet(val roleMembers: List<Member>, val submittedMembers: List<Member>)

	fun getSubmittedSet(guild: Guild, submittedIds: HashSet<Long>): CompletableFuture<SubmittedSet> {
		return getRoleUsers(guild).thenApply { roleMembers ->
			SubmittedSet(roleMembers, roleMembers.filter { submittedIds.contains(it.idLong) })
		}
	}
}
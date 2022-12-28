package modules

import bot.Bot
import bot.Storage
import bot.commands.CMDParam
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.nio.ByteBuffer
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@ModuleID("pleasure")
class PleasureModule : ListenerModule() {
	override val name = "pleasure"

	data class PleasureEntry(val from: Long, val to: Long, val time: Date)
	data class PleasureLog(val log: ArrayList<PleasureEntry>, val latest: HashMap<Long, Date>)

	private val storage = object : Storage<PleasureLog>(this) {
		override val filename = "pleasure.log"

		override fun default() = PleasureLog(ArrayList(), HashMap())

		override fun deserialize(buffer: ByteBuffer): PleasureLog {
			val log = ArrayList<PleasureEntry>()
			val latest = HashMap<Long, Date>()

			while (buffer.hasRemaining()) {
				val from = buffer.long
				val to = buffer.long
				val date = Date(buffer.long)

				log.add(PleasureEntry(from, to, date))

				val existing = latest[from]
				if (existing == null || date > existing) latest[from] = date
			}

			return PleasureLog(log, latest)
		}

		override fun serialize(data: PleasureLog): ByteBuffer {
			val buffer = ByteBuffer.allocateDirect(data.log.size * 8 * 3)

			data.log.forEach { (from, to, time) ->
				buffer.putLong(from)
				buffer.putLong(to)
				buffer.putLong(time.time)
			}

			return buffer
		}
	}

	override fun onStartup(bot: Bot): Boolean {
		storage.get()
		return super.onStartup(bot)
	}

	/**
	 * @return if a user can pleasure someone today. They can only pleasure once a day
	 */
	fun canSend(userId: Long): Boolean {
		val log = storage.get()

		val latest = log.latest[userId] ?: return true

		/* latest is on a previous day */
		return LocalDate.ofInstant(latest.toInstant(), ZoneId.systemDefault()) < LocalDate.now()
	}

	fun recordPleasure(from: Long, to: Long) {
		val log = storage.get()

		val now = Date()

		log.log.add(PleasureEntry(from, to, now))
		log.latest[from] = now
		storage.save()
	}

	@SlashCommand("pleasure", "send pleasure someone's way")
	fun pleasure(event : SlashCommandInteractionEvent, @CMDParam("who to pleasure") target : User) {
		if (event.user.idLong == target.idLong) {
			return event.reply("you can't pleasure yourself").queue()
		}

		if (event.user.idLong == getBot().jda.selfUser.idLong) {
			return event.reply("illegal action").queue()
		}

		if (!canSend(event.user.idLong)) {
			return event.reply("you've already pleasured someone today").queue()
		}

		try {
			target.openPrivateChannel().queue({ channel ->
				recordPleasure(event.user.idLong, target.idLong)

				event.reply("pleasuring...").queue()
				for (i in 0 until 5) {
					channel.sendMessage("augh you bitch").queue()
				}
			}, { event.reply("can't pleasure that person").queue() })
		} catch (_ : Throwable) {
			event.reply("can't pleasure that person").queue()
		}
	}
}

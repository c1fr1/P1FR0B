package modules

import bot.Bot
import bot.commands.CMDParam
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import bot.storage.createStorage
import bot.storage.smartRegisterType
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.PrivateChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.requests.restaction.MessageAction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@ModuleID("pleasure")
class PleasureModule(private val allowSelfPleasure: Boolean) : ListenerModule() {
	override val name = "pleasure"

	data class PleasureEntry(val from: Long, val to: Long, val time: Instant)
	data class PleasureLog(val log: ArrayList<PleasureEntry>, val latest: HashMap<Long, Instant>)

	private val storage = createStorage(
		"pleasure.json",
		GsonBuilder().setPrettyPrinting().serializeNulls()
			.smartRegisterType<Instant>(
				{ instant -> JsonPrimitive(instant.toEpochMilli()) },
				{ json -> Instant.ofEpochMilli(json.asLong) }
			)
			.create(),
	) { PleasureLog(ArrayList(), HashMap()) }

	override fun onStartup(bot: Bot): Boolean {
		storage.load()
		return super.onStartup(bot)
	}

	/**
	 * @return if a user can pleasure someone today. They can only pleasure once a day
	 */
	private fun canSend(userId: Long): Boolean {
		val log = storage.load()

		val latest = log.latest[userId] ?: return true

		/* latest is on a previous day */
		return LocalDate.ofInstant(latest, ZoneId.systemDefault()) < LocalDate.now()
	}

	private fun recordPleasure(from: Long, to: Long) {
		val log = storage.load()

		val now = Instant.now()

		log.log.add(PleasureEntry(from, to, now))
		log.latest[from] = now
		storage.save()
	}

	private fun doPleasure(channel: PrivateChannel, fromName: String?): RestAction<MutableList<Message>> {
		val list = (0 until 5).map { channel.sendMessage("augh you bitch") } as ArrayList<MessageAction>
		fromName?.let { name -> list.add(channel.sendMessage("from $name, with love")) }

		return RestAction.allOf(list)
	}

	@SlashCommand("pleasure", "send pleasure someone's way")
	fun pleasure(
		event : SlashCommandInteractionEvent,
		@CMDParam("who to pleasure") target : User,
		@CMDParam("sign your name") signed : Boolean = false
	) {
		if (!allowSelfPleasure && event.user.idLong == target.idLong) {
			return event.reply("you can't pleasure yourself").setEphemeral(true).queue()
		}

		if (target.idLong == getBot().jda.selfUser.idLong) {
			return event.replyEmbeds(EmbedBuilder().setImage("https://i.imgur.com/aH9Gixi.jpg").build()).setEphemeral(true).queue()
		} else if (target.isBot) {
			return event.reply("robots can't feel pleasure").setEphemeral(true).queue()
		}

		if (!canSend(event.user.idLong)) {
			return event.reply("you've already pleasured someone today").setEphemeral(true).queue()
		}

		event.deferReply(true).queue{ hook ->
			target.openPrivateChannel().queue({ channel ->
				hook.editOriginal("pleasuring...").queue()
				doPleasure(channel, if (signed) event.member?.effectiveName ?: event.user.name else null).queue {
					hook.editOriginal("pleasured.").queue()
				}
				recordPleasure(event.user.idLong, target.idLong)
			}, {
				hook.editOriginal("something went wrong")
			})
		}
	}
}

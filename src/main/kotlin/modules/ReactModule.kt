package modules

import bot.*
import bot.commands.CMDParam
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import okhttp3.internal.wait
import java.io.FileReader
import java.lang.Exception

@ModuleID("React")
class ReactModule : ListenerModule() {

	override val name = "Wiggle"

	/*override fun onStartup(bot: Bot): Boolean {
		val dotaChannel = bot.getGuild().textChannels.find { it.name == "dota" }!!
		Logger.debug("dota_channel : $dotaChannel")
		val fr = FileReader("resources/emoticons.txt")
		val lines = fr.readLines()
		val filenameDict = HashMap<String, String>()
		val emoteEntries = ArrayList<String>()
		lines.forEach {
			val segs = it.split("!")
			if (segs.size >= 4) {
				emoteEntries.add(segs[1])
				emoteEntries.add(segs[2])
				emoteEntries.add(segs[3])
			}
		}
		emoteEntries.forEach {
			val filename = it.substringAfter("gifs/").substringBefore(")|")
			val emoteName = it.substringAfter(":").substringBefore(":")
			filenameDict[filename] = emoteName
		}
		val history = dotaChannel.getHistoryBefore(1229879938262499369L, 10).complete().retrievedHistory
		for (message in history) {
			for (attachment in message.attachments.filter { it.isImage }) {
				val icon = attachment.proxy.downloadAsIcon()
				Logger.debug("found icon attachment ${attachment.fileName}")
				val emoteName = filenameDict[attachment.fileName]
				if (emoteName == null) {
					Logger.debug("failed to find [${attachment.fileName}]")
					continue
				}
				if (bot.getGuild().emojis.any { it.name == emoteName }) {
					Logger.debug("$emoteName (${attachment.fileName}) already uploaded")
					continue
				}
				icon.thenAccept {
					Logger.debug("icon: ${attachment.fileName}")
					try {
						val result = bot.getGuild().createEmoji(emoteName, it).complete()
						Logger.debug("created icon: $result")
					} catch (e : Throwable) {
						Logger.logError(e)
					}
				}
				Thread.sleep(500)
			}
		}
		return super.onStartup(bot)
	}*/

	@SlashCommand("create an emoji reaction", "adds the specified reaction to the specified message that gets dismissed automatically")
	fun react(e : SlashCommandInteractionEvent, bot : Bot,
	           @CMDParam("name or id of emoji for the reaction") emojiName : String,
	           @CMDParam("id of the message you wish to react to") messageId : Long = 0L) {
		var target = e.channel.getHistoryAround(e.messageChannel.latestMessageId, 1).complete().retrievedHistory[0]
		target = e.channel.getHistoryAround(messageId, 1).complete().getMessageById(messageId) ?: target

		val simplifierF = {s : String ->
			s.lowercase().replace(" ", "").replace("_", "")
		}

		val emote = e.guild?.retrieveEmojis()?.complete()?.find {
			it.isAnimated && simplifierF(it.name) == simplifierF(emojiName)
		}

		if (emote == null) {
			e.reply("unknown emote").setEphemeral(true).complete()
			return
		}

		target.addReaction(emote).complete()

		e.reply("reacted").setEphemeral(true).complete()
	}

	override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
		if (event.user!!.isBot || event.reaction.emoji.type != Emoji.Type.CUSTOM) return
		if (event.reaction.emoji.asCustom().isAnimated) {
			event.reaction.removeReaction().complete()
		}
	}
}
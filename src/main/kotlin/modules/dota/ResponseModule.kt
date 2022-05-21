package modules.dota

import bot.commands.CMDParam
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.File
import java.io.FileReader
import kotlin.io.path.ExperimentalPathApi

@ExperimentalPathApi
@ModuleID("Dota Responses")
class ResponseModule(private val basePath : String = "resources/responses/",
                     dictPath : String = "resources/response-data.txt") : ListenerModule() {

	override val name: String = "Dota Responses"
	val dictionary = FileReader(dictPath).readLines().map {l ->
		val segments = l.split(",").map { it.trim() }
		val heroName = segments[0]
		val text = segments[1]
		val id = segments[2].toInt()
		ResponseEntry(text, heroName, id)
	}

	override fun onMessageReceived(event: MessageReceivedEvent) {
		if (event.author.isBot) return
		val entry = getRandomEntry(event.message.contentRaw)
		if (entry != null) {
			event.channel.sendFile(File(entry.path(basePath)), "${entry.heroName}${entry.id}.mp3").complete()
		}
	}

	private fun getRandomEntry(message : String) : ResponseEntry? {
		val asSimple = toSimpleText(message)
		return dictionary.filter { it.simpleText == asSimple }.randomOrNull()
	}

	@SlashCommand("searches for a response",
		"Finds a response based on the information specified. None of the following parameters need to be" +
				"specified, however only queries that return less than one thousand voice lines will be successful. " +
				"For perspective, axe has over 750 voice lines.\n\n" +
				"The phrase parameter specifies a phrase that will be contained in all of the voice lines returned. " +
				"the text in this parameter is not case sensitive and it is not important to include special " +
				"characters such as apostrophes.\n\n" +
				"The words parameter operates the same as the phrase parameter, however the order of the specified " +
				"words don't matter. \n\n" +
				"Surprisingly, the hero parameter specifies which hero's voice lines you are searching for. Some " +
				"special formatting is supported, however to ensure your query works, you should specify the id of " +
				"the hero as valve stores them. Generally this is the hero's name with underscores for spaces, but a " +
				"few exceptions include:\n\n" +
				"Shadow Fiend = nevermore\n" +
				"Zeus = zuus\n" +
				"Underlord = abbyssal_underlord\n" +
				"Vengeful Spirit = vengefulspirit\n" +
				"Quop = queenofpain\n\n" +
				"The response ID is only how this bot refers to the voice line, it has nothing to do with how Dota " +
				"stores the response. This ID is only unique to the hero (multiple heroes have voice lines with the " +
				"same ID).")
	fun findResponse(e : SlashCommandInteractionEvent,
		@CMDParam("phrase you are looking for") phrase : String? = null,
		@CMDParam("key words that you are looking for") words : String? = null,
		@CMDParam("hero whos voiceline you are looking for") hero : String? = null,
		@CMDParam("internal id of the voiceline you are looking for") responseId : Int? = null
	) {
		val reply = e.deferReply().complete()
		var retList = dictionary
		if (responseId != null) {
			retList = retList.filter { it.id == responseId }
		}
		if (hero != null) {
			val simpleHero = toSimpleText(hero)
			retList = retList.filter { it.heroName.replace("_", "") == simpleHero }
		}
		if (phrase != null) {
			retList = retList.filter { it.simpleText.contains(toSimpleText(phrase)) }
		}
		if (words != null) {
			val wordList = toSimpleText(words).split(" ")
			retList = retList.filter { line -> wordList.all { line.simpleText.contains(it) }}
		}
		if (retList.isEmpty()) {
			reply.editOriginal("no voice lines were found.").complete()
			return
		}
		if (retList.size > 1000) {
			reply.editOriginal("${retList.size} voice lines were found, please narrow your search").complete()
			return
		}
		val ret = retList.map { "${it.heroName} ${it.id} : ${it.simpleText}" }.reduce {acc, line -> "$acc\n$line"}
		if (ret.length >= 2000) {
			reply.editOriginal(ret.toByteArray(), "responses.txt").complete()
		} else {
			reply.editOriginal(ret).complete()
		}
	}

	@SlashCommand("Sends a specified voice line",
		"Sends a voice line based on an exact hero name and voice line ID. You can find the id by using" +
				"`/find-response`, or by using the hero name and id listed by a previously sent voice line.")
	fun sendResponse(e : SlashCommandInteractionEvent,
		@CMDParam("name of the hero") hero : String,
		@CMDParam("id of the voice line") id : Int
	) {
		val voiceLine = dictionary.filter { it.id == id &&
				it.heroName.replace("_", "") == toSimpleText(hero) }
		if (voiceLine.isEmpty()) {
			e.reply("no voice line found").complete()
		} else if (voiceLine.size > 1) {
			e.reply("multiple voice lines found").complete()
		} else {
			e.replyFile(File(voiceLine.first().path(basePath))).complete()
		}
	}
}

private fun toSimpleText(str : String) : String {
	return str.lowercase().replace(Regex("([^a-z ])"), "").trim()
}

data class ResponseEntry(val simpleText : String, val heroName : String, val id : Int) {
	fun path(basePath : String) : String {
		return "$basePath$heroName/$id.mp3"
	}
}

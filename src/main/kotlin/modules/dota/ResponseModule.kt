package modules.dota

import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.io.File
import java.io.FileReader
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension

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
			event.channel.sendFile(File(entry.path()), "${entry.heroName}${entry.id}.mp3").complete()
		}
	}

	private fun getRandomEntry(message : String) : ResponseEntry? {
		val asSimple = message.lowercase().replace(Regex("([^a-z ])"), "").trim()
		return dictionary.filter { it.simpleText == asSimple }.randomOrNull()
	}
}

data class ResponseEntry(val simpleText : String, val heroName : String, val id : Int) {
	fun path() : String {
		return "resources/responses/$heroName/$id.mp3"
	}
}

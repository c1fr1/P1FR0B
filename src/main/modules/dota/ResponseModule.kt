package modules.dota

import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.extension

@ExperimentalPathApi
@ModuleID("Dota Responses")
class ResponseModule(private val basePath : String = "resources/responses/") : ListenerModule() {

	override val name: String = "Dota Responses"

	override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
		if (event.author.isBot) return
		val path = getRandomPath(event.message.contentRaw)
		if (path != null) {
			event.channel.sendFile(path).complete()
		}
	}

	private fun getRandomPath(message : String) : File?
		= getRandomPath(message.toLowerCase().replace(Regex("([^a-z ])"), "").split(" "))

	private fun getRandomPath(words : List<String>) : File? {
		var strPath = basePath
		for (word in words) {
			strPath += "$word/"
			if (!Files.exists(Path.of(strPath))) {
				return null
			}
		}
		val heroList = ArrayList<Path>()
		for (child in Files.list(Path.of(strPath))) {
			if (child.extension == "mp3") {
				heroList.add(child)
			}
		}
		if (heroList.isEmpty()) {
			return null
		}
		return heroList.random().toFile()
	}

}

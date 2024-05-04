package modules.nyt

import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

@ModuleID("New York Times Module")
class NYTModule : ListenerModule() {
	override val name : String = "New York Times Module"
	override fun onMessageReceived(event: MessageReceivedEvent) {
		if (event.author.isBot) return
		val message = event.channel.retrieveMessageById(event.messageId).complete()
		Connections.handleMessage(message)
	}
}
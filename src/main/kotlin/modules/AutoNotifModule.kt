package modules

import bot.Logger
import bot.modules.BotModule
import bot.modules.ModuleID
import modules.moduleCommunications.ContactableModule

@ModuleID("Automatic Notification Server")
class AutoNotifModule : BotModule(), ContactableModule {
	override val name : String = "Automatic Notification Server"
	override fun receiveMessage(message : String) : String? {
		val userSnowflake = message.substringBefore("|")
		val sourceId = message.substringAfter("|").substringBefore(":")
		val sentMessage = message.substringAfter(":")
		val formattedMessage = "${sentMessage}\n\nmessage was sent from `${sourceId}`"
		getBot().jda.getUserById(userSnowflake)?.openPrivateChannel()?.complete()
			?.sendMessage(formattedMessage)?.complete() ?: Logger.warn(
			"failed to send message, target: $userSnowflake | source ID: $sourceId, : message: $sentMessage"
		)
		return null
	}

}
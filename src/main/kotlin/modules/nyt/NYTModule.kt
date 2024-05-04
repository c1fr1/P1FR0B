package modules.nyt

import bot.Bot
import bot.IDS
import bot.Logger
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder
import net.dv8tion.jda.api.utils.messages.MessageCreateData
import java.lang.RuntimeException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import kotlin.concurrent.thread

@ModuleID("New York Times Module")
class NYTModule : ListenerModule() {
	override val name : String = "New York Times Module"

	var socket : ServerSocket? = null
	var serverThread : Thread? = null

	override fun onStartup(bot : Bot) : Boolean {
		try {
			val address = IDS["SERVER_ADDRESS"].let { InetAddress.getByName(it) } ?: InetAddress.getLocalHost()
			socket = ServerSocket(IDS["NYT_GAMES_PORT"]!!.toInt(), 50, address)
			Logger.verbose("socket addr: ${socket?.localSocketAddress}")
			startServerThread()
		} catch (e : Throwable) {
			Logger.error("failed to start NYT Games server thread")
		}
		return super.onStartup(bot)
	}

	override fun onMessageReceived(event: MessageReceivedEvent) {
		if (event.author.isBot) return
		val message = event.channel.retrieveMessageById(event.messageId).complete()
		Connections.handleMessage(message)
	}

	fun sendGame(userID : String, gameSummary : String) {
		val channel = getBot().getGuild().getTextChannelById(IDS["NYT_GAMES_CHANNEL"]!!)
			?: throw RuntimeException("unable to find NYT Games channel")
		val messageBuilder = SilentMessageDataBuilder()
		messageBuilder.addContent("game summary from <@$userID>\n$gameSummary")
		messageBuilder.allowedMentions.clear()
		val message = channel.sendMessage(messageBuilder.build()).complete()
		Connections.handleGameSubmission(userID, gameSummary, message)
	}

	fun startServerThread() {
		serverThread = thread {

			Logger.verbose("starting nyt games server thread on port ${socket!!.localPort}")
			try {
				while (serverThread != null && socket != null) {
					val conn = socket?.accept()
					if (conn == null) {
						Logger.verbose("failed to connect")
						continue
					}
					try {
						val messageString = String(conn.getInputStream().readAllBytes())
						val userSnowflake = messageString.substringBefore("|")
						val gameSummary = messageString.substringAfter("|")
						if (getBot().getGuild().getMemberById(userSnowflake) != null && gameSummary.isNotEmpty()) {
							sendGame(userSnowflake, gameSummary)
						}
					} catch (e : SocketException) {
						Logger.warn("Socket error thrown when receiving message from ${conn.inetAddress}")
					} catch (e : Throwable) {
						Logger.warn("Unknown error thrown when receiving message from ${conn.inetAddress}")
						Logger.logError(e)
					}
				}
			} catch (e : Throwable) {
				Logger.error("auto notif server thread threw an unexpected error")
				Logger.logError(e)
			}
		}
	}
}

class SilentMessageDataBuilder : MessageCreateBuilder() {
	override fun build(): MessageCreateData {
		messageFlags = messageFlags or 4096
		return super.build()
	}
}
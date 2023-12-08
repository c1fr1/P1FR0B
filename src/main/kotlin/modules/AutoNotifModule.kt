package modules

import bot.Bot
import bot.IDS
import bot.Logger
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import java.net.InetAddress
import java.net.ServerSocket
import kotlin.concurrent.thread

@ModuleID("Automatic Notification Server")
class AutoNotifModule(val port : Int = 4730) : ListenerModule() {
	override val name : String = "Automatic Notification Server"
	var socket : ServerSocket? = null

	var serverThread : Thread? = null

	override fun onStartup(bot : Bot) : Boolean {
		val address = IDS["NOTIF_SERVER_ADDRESS"].let { InetAddress.getByName(it) } ?: InetAddress.getLocalHost()
		socket = ServerSocket(4730, 50, address)
		Logger.verbose("socket addr: ${socket?.localSocketAddress}")
		serverThread = thread {
			while (serverThread != null && socket != null) {
				val conn = socket?.accept()
				if (conn == null) {
					Logger.verbose("failed to connect")
					continue
				}
				val messageString = String(conn.getInputStream().readAllBytes())
				val userSnowflake = messageString.substringBefore("|")
				val sourceId = messageString.substringAfter("|").substringBefore(":")
				val message = messageString.substringAfter(":")
				val formattedMessage = "${message}\n\nmessage was sent from `${sourceId}`"
				bot.jda.getUserById(userSnowflake)?.openPrivateChannel()?.complete()?.sendMessage(formattedMessage)?.complete() ?: Logger.warn(
					"failed to send message, target: $userSnowflake | source ID: $sourceId, : message: $message")
			}
		}

		return super.onStartup(bot)
	}

	@SlashCommand("returns the port the server is advertising on", "returns the port the server is advertising on")
	fun getPort() : String {
		val threadInfo = if (serverThread != null) {
			"Server thread is ${if (serverThread!!.isAlive) "alive" else "dead"}"
		} else {
			"server thread is null"
		}
		return "Notification server is running on port $port. $threadInfo"
	}
}
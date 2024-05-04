package modules

import bot.Bot
import bot.IDS
import bot.Logger
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import kotlin.concurrent.thread

@ModuleID("Automatic Notification Server")
class AutoNotifModule(val port : Int = IDS["NOTIF_SERVER_PORT"]!!.toInt()) : ListenerModule() {
	override val name : String = "Automatic Notification Server"

	var socket : ServerSocket? = null
	var serverThread : Thread? = null

	override fun onStartup(bot : Bot) : Boolean {
		val address = IDS["NOTIF_SERVER_ADDRESS"].let { InetAddress.getByName(it) } ?: InetAddress.getLocalHost()
		socket = ServerSocket(port, 50, address)
		Logger.verbose("socket addr: ${socket?.localSocketAddress}")
		startServerThread()

		return super.onStartup(bot)
	}

	fun startServerThread() {
		serverThread = thread {

			Logger.verbose("starting notif server thread on port $port")
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
						val sourceId = messageString.substringAfter("|").substringBefore(":")
						val message = messageString.substringAfter(":")
						val formattedMessage = "${message}\n\nmessage was sent from `${sourceId}`"
						getBot().jda.getUserById(userSnowflake)?.openPrivateChannel()?.complete()
							?.sendMessage(formattedMessage)?.complete() ?: Logger.warn(
							"failed to send message, target: $userSnowflake | source ID: $sourceId, : message: $message"
						)
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
package modules

import bot.Bot
import bot.Logger
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.ShutdownEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.net.InetAddress
import java.net.ServerSocket
import kotlin.concurrent.thread

@ModuleID("Automatic Notification Server")
class AutoNotifModule(val port : Int = 4730) : ListenerModule() {
	override val name : String = "Automatic Notification Server"
	var socket : ServerSocket? = null

	var serverThread : Thread? = null

	override fun onStartup(bot : Bot) : Boolean {
		socket = ServerSocket(4730, 50, InetAddress.getLocalHost())
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
				val formattedMessage = "message from automatic notification server, source id `${sourceId}\n\n${message}`"
				bot.jda.getUserById(userSnowflake)?.openPrivateChannel()?.complete()?.sendMessage(formattedMessage)?.complete()
			}
		}

		return super.onStartup(bot)
	}

	@SlashCommand("returns the port the server is advertising on", "returns the port the server is advertising on")
	fun getPort(e : SlashCommandInteractionEvent) : String {
		return "Notification server is running on port ${port}."
	}

	override fun onShutdown(event : ShutdownEvent) {
		socket?.close()
		socket = null
		serverThread = null
	}
}
package modules.moduleCommunications

import bot.Bot
import bot.IDS
import bot.Logger
import bot.commands.SlashCommand
import bot.modules.BotModule
import bot.modules.ListenerModule
import bot.modules.ModuleID
import okhttp3.internal.wait
import okio.ByteString.Companion.readByteString
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import kotlin.concurrent.thread

@ModuleID("Module Communication Server")
class ModuleCommunicatorModule(val port : Int = IDS["MODULE_COMMUNICATOR_PORT"]!!.toInt()) : BotModule() {
	override val name : String = "Module Communication Server"

	var socket : ServerSocket? = null
	var serverThread : Thread? = null

	val handlerThreads = ArrayList<Pair<Thread, Long>>()

	val threadTimeout = 30000

	override fun onStartup(bot : Bot) : Boolean {
		val address = IDS["SERVER_ADDRESS"].let { InetAddress.getByName(it) } ?: InetAddress.getLocalHost()
		socket = ServerSocket(port, 50, address)
		Logger.verbose("socket addr: ${socket?.localSocketAddress}")
		startServerThread()

		return super.onStartup(bot)
	}

	fun startHandlerThread(f : () -> Unit) {
		val t = thread(block = f)
		handlerThreads.add(Pair(t, System.currentTimeMillis()))
	}

	fun pruneThreads() {
		handlerThreads.removeIf { (t, time) -> !t.isAlive }
		handlerThreads.removeIf { (t, time) -> if (System.currentTimeMillis() - time > threadTimeout) {
			t.interrupt()
			true
		} else false}
	}

	fun startServerThread() {
		serverThread = thread {

			Logger.verbose("starting communicator server thread on port $port")
			try {
				while (serverThread != null && socket != null) {
					pruneThreads()
					val conn = socket?.accept()
					if (conn == null) {
						Logger.verbose("failed to connect")
						continue
					}
					try {
						startHandlerThread {
							try {
								var messageString = ""
								while (conn.isConnected) {
									val nextByte = conn.getInputStream().read()
									if (nextByte != 0) {
										messageString += nextByte.toChar()
									} else break
								}
								val targetModuleID = messageString.substringBefore("|").lowercase().replace(" ", "-")
								val payload = messageString.substringAfter("|")
								Logger.verbose("received message for $targetModuleID")
								getBot().getModules().firstOrNull { it.id == targetModuleID }?.let { targetModule ->
									if (targetModule is ContactableModule) {
										targetModule.receiveMessage(payload)?.let { response ->
											conn.getOutputStream().write(response.toByteArray())
										}
									} else {
										Logger.warn("attempt was made to contact \"$targetModuleID\" which is not a ContactableModule")
									}
								} ?: {
									Logger.warn("attempt was made to contact \"$targetModuleID\" which is not a known module")
								}
								conn.close()
							} catch (e : SocketException) {
								Logger.warn("Socket error thrown when receiving message from ${conn.inetAddress}")
								Logger.logError(e)
							} catch (e : Throwable) {
								Logger.warn("Unknown error thrown when receiving message from ${conn.inetAddress}")
								Logger.logError(e)
							}
						}

					} catch (e : SocketException) {
						Logger.warn("Socket error thrown when receiving message from ${conn.inetAddress}")
						Logger.logError(e)
					} catch (e : Throwable) {
						Logger.warn("Unknown error thrown when receiving message from ${conn.inetAddress}")
						Logger.logError(e)
					}
				}
			} catch (e : Throwable) {
				Logger.error("module communicator server thread threw an unexpected error")
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
		return "Communicator server is running on port $port. $threadInfo"
	}

	@SlashCommand("lists number of active handler threads", "lists number of active handler threads")
	fun countThreads() : String {
		pruneThreads()
		return "there are currently ${handlerThreads.size} active threads"
	}
}
package bot

import bot.commands.GeneralCommandModule
import bot.modules.IModule
import bot.modules.ListenerAdapter
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.requests.GatewayIntent
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * primary class to represent the Discord bot
 * @author Varas#5480
 */
@Suppress("UNCHECKED_CAST")
class Bot(targetGuildSnowflake : String) {

	private var jdaBuilder : JDABuilder? = JDABuilder.createDefault(IDS.getID("API_KEY"))
		.enableIntents(GatewayIntent.GUILD_MEMBERS)

	private var jdaObj : JDA? = null

	private val modules : LinkedList<IModule> = LinkedList()

	private val guild = targetGuildSnowflake

	val jda : JDA get() { return jdaObj!! }

	init {
		addModule(GeneralCommandModule())
	}

	/**
	 * starts the bot, taking it online
	 */
	fun startup() {
		if (isRunning()) {
			Logger.warn("tried to start a bot that is already running")
		}
		jdaObj = jdaBuilder!!.build().awaitReady()
		jdaBuilder = null

		for (module in modules.clone() as List<*>) {
			(module as IModule).onStartup(this)
		}

		Logger.info("bot is now running!")
	}

	/**
	 * checks if the bot is running or not
	 * @return true if the bot is running
	 */
	fun isRunning() : Boolean {
		if ((jdaBuilder == null) == (jdaObj == null)) {
			throw Exception("this Bot is in an invalid state")
		} else {
			return jdaObj != null
		}
	}

	/**
	 * adds a module to the bot
	 * @param module a module that will be started now or when the bot starts
	 */
	fun addModule(module : IModule) : Boolean {
		modules.add(module)
		try {
			if (!module.load()) {
				Logger.info("failed to load module ${module.id}")
				modules.remove(module)
			}
		} catch (e : Throwable) {
			Logger.warn("failed to load module ${module.id}")
			modules.remove(module)
			Logger.logError(e)
		}
		if (isRunning()) {
			return if (module.onStartup(this)) {
				for (m in modules) {
					m.onAddModule(module)
					module.onAddModule(m)
				}
				Logger.verbose("added module ${module.id}")
				true
			} else {
				Logger.warn("failed to load module ${module.id}")
				modules.remove(module)
				false
			}
		}
		Logger.verbose("added module ${module.id}")
		return true
	}

	/**
	 * removes an existing module from the bot
	 * @param module a module currently on the bot that will be removed
	 */
	fun removeModule(module : IModule) {
		module.stop(this)
		modules.remove(module)
		for (m in modules) {
			m.onRemoveModule(module)
		}
	}

	/**
	 * adds a module to the bot if there is not one of the same type serving the bot
	 * @param module module that gets added if there is not one like it already serving the bot
	 * @return the module that is either on the bot or was just added to the bot, null if starting the module fails
	 */
	fun resolveDependency(module : IModule) : IModule? {
		for (m in modules) {
			if (m.id == module.id) {
				return m
			}
		}

		addModule(module)

		for (m in modules) {
			if (m.id == module.id) {
				return m
			}
		}

		return null
	}

	/**
	 * ensures that there is a specific module serving this bot
	 * @return the module that is either on the bot or was just added to the bot, null if the module doesn't have a
	 * constructor or the module fails to start
	 */
	inline fun <reified T : IModule> resolveDependency() : T? {
		return resolveDependency(T::class)
	}

	/**
	 * ensures that there is a specific module serving this bot
	 * @param module type of the module object that must be on this bot
	 * @return the module that is either on the bot or was just added to the bot, null if the module doesn't have a
	 * constructor or the module fails to start
	 */
	fun <T : IModule> resolveDependency(module : KClass<T>) : T? {
		for (m in modules) {
			if (m::class == module) {
				Logger.verbose("dependency module already running")
				return m as T
			}
		}
		var constructor : KFunction<T>? = null
		for (c in module.constructors) {
			if (c.parameters.isEmpty()) {
				constructor = c
				break
			}
		}
		if (constructor == null) {
			Logger.warn("tried to start a module without the required parameter")
			return null
		}
		val ret = constructor.call()
		addModule(ret)
		for (m in modules) {
			if (m.id == ret.id) {
				return m as T
			}
		}
		return null
	}

	/**
	 * adds a listener adapter to the bot
	 * @param listenerAdapter manager for events that the bot produces
	 */
	fun addListenerAdapter(listenerAdapter : ListenerAdapter) {
		if (isRunning()) {
			jdaObj!!.addEventListener(listenerAdapter)
		} else {
			jdaBuilder!!.addEventListeners(listenerAdapter)
		}
		Logger.verbose("added listener adapter to bot $listenerAdapter")
	}

	/**
	 * removes a listener adapter from the bot
	 * @param listenerAdapter manager on the bot that should be removed
	 */
	fun removeListenerAdapter(listenerAdapter: ListenerAdapter) {
		if (isRunning()) {
			jdaObj!!.removeEventListener(listenerAdapter)
		} else {
			jdaBuilder!!.removeEventListeners(listenerAdapter)
		}
		Logger.verbose("removed listener adapter $listenerAdapter")
	}

	/**
	 * gets the main guild that the bot is in
	 * @return primary guild the bot serves
	 */
	fun getGuild() : Guild {
		if (!isRunning()) {
			throw Exception("Tried to get guild while the guild wasn't running")
		}
		return jdaObj!!.getGuildById(guild)
			?: throw Exception("Bot object was given an invalid server snowflake $guild")
	}

	/**
	 * gets a list of all the modules that are currently serving this bot
	 * @return all the modules serving this
	 */
	fun getModules() : List<IModule> {
		return modules
	}
}
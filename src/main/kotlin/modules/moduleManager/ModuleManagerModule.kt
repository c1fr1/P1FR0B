package modules.moduleManager

import bot.Bot
import bot.IDS
import bot.Logger
import bot.commands.CMDParam
import bot.commands.SlashCommand
import bot.modules.BotModule
import bot.modules.IModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.User
import org.reflections.Reflections
import kotlin.reflect.KClass

@ModuleID("Module Manager")
class ModuleManagerModule : BotModule() {

	override val name = "Module Manager"

	private var manager : User? = null

	companion object {
		private val reflections = Reflections("")
	}

	override fun load(): Boolean {
		return IDS["MANAGER"] != null
	}

	override fun onStartup(bot : Bot) : Boolean {
		this.manager = bot.getGuild().jda.retrieveUserById(IDS["MANAGER"]!!).complete()

		if (this.manager == null)
			Logger.error("Module Manager failed to find a MANAGER to send messages to")

		sendManagerMessage("Module \"$name\" has been added\nid: $id")
		return super.onStartup(bot)
	}

	@SlashCommand("lists the modules this bot uses",
		"shows a list of all of the modules currently serving this bot")
	fun listActiveModules(bot : Bot) : String {
		val moduleStatus = StringBuilder()
		moduleStatus.append("**currently running modules**\n\n")
		for (module in bot.getModules()) {
			moduleStatus.append(" - ${module.name} (`${module.id}`)\n")
		}
		return moduleStatus.toString()
	}

	@SlashCommand("lists all modules including those that can be added",
		"lists all of the functions that are either running or can be started at runtime")
	fun listAvailableModules(bot : Bot) : String {
		val stringBuilder = StringBuilder("**available modules**\n")
		for (module in getAvailableModules()) {
			val mID = ModuleID.getID(module.kotlin)
			if (bot.getModules().any {it::class == module.kotlin}) {
				stringBuilder.append(" - `$mID` running\n")
			} else if (mID != null) {
				stringBuilder.append(" - `$mID` available\n")
			}
		}
		return stringBuilder.toString()
	}

	@SlashCommand("adds an available module to the bot",
		"starts running the specified module on this bot\n\n" +
				"`/start-module <module id> <constructor parameters>`", true)
	fun startModule(bot : Bot,
		@CMDParam("identifier of the module") module : String,
		@CMDParam("parameters for the constructor of the module") parameters : String = "") : String {

		var moduleClass : KClass<out IModule>? = null
		for (m in getAvailableModules()) {
			if (ModuleID.getID(m.kotlin) == module) {
				moduleClass = m.kotlin
			}
		}

		if (moduleClass == null) {
			return "unable to find module `$module`, try `/list available modules`"
		}

		val params = (parameters).split(" ").filter { it.isNotEmpty() }
		val ret = constructModule(moduleClass, params) ?: return "failed to create `$module` module"
		return if (bot.addModule(ret)) {
			"module added!"
		} else {
			"failed to start $module"
		}
	}

	@SlashCommand("removes a module from this bot",
		"removes the specified module from this bot\n\n" +
				"`/stop-module <module id>`\n" +
				"`/stop-module <module name>`", true)
	fun stopModule(bot : Bot, @CMDParam("identifier of the module") module : String) : String {
		for (m in bot.getModules().filter {it.id == ModuleID.getID(module)}) {
			bot.removeModule(m)
			return "removed module `${m.id}`"
		}
		return "there was not a `$module` module running"
	}

	override fun stop(bot: Bot) {
		super.stop(bot)
		sendManagerMessage("Module \"$name\" has been stopped\nid: $id")
	}

	override fun onAddModule(module: IModule) {
		sendManagerMessage("Module \"${module.name}\" has been added\nid: ${module.id}")
	}

	override fun onRemoveModule(module: IModule) {
		sendManagerMessage("module \"${module.name}\" has been removed\n" +
			"id: ${module.id}")
	}

	private fun sendManagerMessage(m : String) {
		if (manager == null) {
			Logger.info("failed to send info to the manager")
			return
		}
		manager!!.openPrivateChannel().complete().sendMessage(m).complete()
	}

	private fun <T : IModule> constructModule(module : KClass<out T>, params : List<String>) : T? {
		for (constructor in module.constructors.filter {c -> c.parameters.all {it.type.classifier == String::class}}) {
			if (constructor.parameters.size == params.size) {
				return constructor.call(*params.toTypedArray())
			}
		}
		return null
	}

	private fun getAvailableModules() : Set<Class<out IModule>> {
		return reflections.getSubTypesOf(IModule::class.java)
	}
}
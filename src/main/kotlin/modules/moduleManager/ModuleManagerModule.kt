package modules.moduleManager

import bot.*
import bot.commands.Command
import bot.commands.CommandParameter
import bot.commands.GeneralCommandModule
import bot.modules.BotModule
import bot.modules.IModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.interactions.commands.OptionType
import org.reflections.Reflections
import kotlin.reflect.KClass
import kotlin.text.StringBuilder

@ModuleID("Module Manager")
class ModuleManagerModule : BotModule() {

	override val name: String
		get() = "Module Manager"

	private var manager : User? = null

	companion object {
		private val reflections = Reflections("")
	}

	override fun load(): Boolean {
		return IDS.getID("MANAGER") != null
	}

	override fun onStartup(bot: Bot) : Boolean {
		val manager = bot.getGuild().jda.getUserById(IDS.getID("MANAGER")!!)
		if (manager == null) {
			Logger.error("Module Manager failed to find a MANAGER to send messages to")
		} else {
			this.manager = manager
		}
		val commandModule = bot.resolveDependency(GeneralCommandModule::class)
		if (commandModule == null) {
			Logger.error("Module Manager failed resolve the general command module dependency")
			return false
		}
		commandModule.addCommands(
			Command("list-active-modules",
				"lists the modules this bot uses",
				"shows a all of the modules that are currently serving this bot",
				this)
			{event, _ ->
				val moduleStatus = StringBuilder()
				moduleStatus.append("**currently running modules**\n\n")
				for (module in getBot(event).getModules()) {
					moduleStatus.append(" - ${module.name} (`${module.id}`)\n")
				}
				event.reply(moduleStatus.toString()).complete()
			},
			Command("list-available-modules",
				"lists all modules including those that can be added",
				"lists all of the functions that are either running or can be started at runtime",
				this)
			{event, _ ->
				val stringBuilder = StringBuilder("**available modules**\n")
				for (module in getAvailableModules()) {
					val mID = ModuleID.getID(module.kotlin)
					if (bot.getModules().any {it::class == module.kotlin}) {
						stringBuilder.append(" - `$mID` running\n")
					} else if (mID != null) {
						stringBuilder.append(" - `$mID` available\n")
					}
				}
				event.reply(stringBuilder.toString()).complete()
			},
			Command("start-module",
				"adds an available modue to the bot",
				"starts running the specified module on this bot\n\n" +
				"`/start-module <module id> <constructor parameters>`",
				this, true,
				CommandParameter(OptionType.STRING, "module", "identifier of the module", true),
				CommandParameter(OptionType.STRING, "parameters", "parameters for the constructor of the module")
			)
			{event, _ ->
				var moduleClass : KClass<out IModule>? = null
				val mID = event.getOption("module")?.asString
				for (m in getAvailableModules()) {
					if (ModuleID.getID(m.kotlin) == mID) {
						moduleClass = m.kotlin
					}
				}

				if (moduleClass == null) {
					event.reply("unable to find module `$mID`, try `/list available modules`").complete()
					return@Command
				}

				val params = (event.getOption("parameters")?.asString ?: "").split(" ").filter { it.length > 0 }
				val ret = constructModule(moduleClass, params)
				if (ret == null) {
					event.reply("failed to create `$mID` module").complete()
					return@Command
				}
				if (bot.addModule(ret)) {
					event.reply("module added!").complete()
				} else {
					event.reply("failed to start $mID").complete()
				}
			},
			Command("stop-module",
				"removes a module from this bot",
				"removes the specified module from this bot\n\n" +
				"`/stop-module <module id>`\n" +
				"`/stop-module <module name>`",
				this, true,
				CommandParameter(OptionType.STRING, "module", "identifier of the module", true)
			)
			{event, cBot ->
				val mID = event.getOption("module")?.asString ?: ""
				for (module in cBot.getModules().filter {it.id == ModuleID.getID(mID)}) {
					cBot.removeModule(module)
					event.reply("removed module `${module.id}`").complete()
					return@Command
				}
				event.reply("there was not a `${ModuleID.getID(mID)}` module running").complete()
			}
		)
		sendManagerMessage("Module \"$name\" has been added\nid: $id")
		return super.onStartup(bot)
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
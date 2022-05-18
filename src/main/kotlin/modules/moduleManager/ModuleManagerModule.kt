package modules.moduleManager

import bot.*
import bot.commands.Command
import bot.commands.GeneralCommandModule
import bot.modules.BotModule
import bot.modules.IModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.User
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
			Command("list active modules",
				"shows a all of the modules that are currently serving this bot",
				this)
			{ _, event, _ ->
				val moduleStatus = StringBuilder()
				moduleStatus.append("**currently running modules**\n\n")
				for (module in getBot(event).getModules()) {
					moduleStatus.append(" - ${module.name} (`${module.id}`)\n")
				}
				event.channel.sendMessage(moduleStatus.toString()).complete()
			},
			Command("list available modules",
				"lists all of the functions that are either running or can be started at runtime",
				this)
			{_, event, _ ->
				val stringBuilder = StringBuilder("**available modules**\n")
				for (module in getAvailableModules()) {
					val mID = ModuleID.getID(module.kotlin)
					if (bot.getModules().any {it::class == module.kotlin}) {
						stringBuilder.append(" - `$mID` running\n")
					} else if (mID != null) {
						stringBuilder.append(" - `$mID` available\n")
					}
				}
				event.channel.sendMessage(stringBuilder).complete()
			},
			Command("start module",
				"starts running the specified module on this bot\n\n" +
				"`${commandModule.prefix}start module <module id> <constructor parameters>`",
				this, true)
			{params, event, _ ->
				var moduleClass : KClass<out IModule>? = null
				val aParams = params.trim().split(' ')
				val mID = ModuleID.getID(aParams[0])
				for (m in getAvailableModules()) {
					if (ModuleID.getID(m.kotlin) == mID) {
						moduleClass = m.kotlin
					}
				}

				if (moduleClass == null) {
					event.channel.sendMessage("unable to find module `$mID`, try `${commandModule.prefix}list" +
							" available modules`").complete()
					return@Command
				}

				val ret = constructModule(moduleClass, aParams.drop(1))
				if (ret == null) {
					event.channel.sendMessage("failed to create `$mID` module, try" +
							" `${commandModule.prefix}module constructors `$mID`").complete()
					return@Command
				}
				if (bot.addModule(ret)) {
					event.channel.sendMessage("module added!").complete()
				} else {
					event.channel.sendMessage("failed to start $mID").complete()
				}
			},
			Command("stop module",
				"removes the specified module from this bot\n\n" +
				"`${commandModule.prefix}stop module <module id>`\n" +
				"`${commandModule.prefix}stop module <module name>`",
				this, true)
			{param, event, cBot ->
				for (module in cBot.getModules().filter {it.id == ModuleID.getID(param)}) {
					cBot.removeModule(module)
					event.channel.sendMessage("removed module `${module.id}`").complete()
					return@Command
				}
				event.channel.sendMessage("there was not a `${ModuleID.getID(param)}` module running").complete()
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
			if (constructor.parameters.size < params.size) {
				continue
			}
		}
		return null
	}

	private fun getAvailableModules() : Set<Class<out IModule>> {
		return reflections.getSubTypesOf(IModule::class.java)
	}
}
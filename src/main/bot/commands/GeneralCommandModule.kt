package bot.commands

import bot.modules.IModule
import bot.modules.ListenerModule
import bot.Logger
import bot.modules.ModuleID
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import java.util.*

@ModuleID("General Commands")
class GeneralCommandModule(val prefix : String) : ListenerModule() {

	constructor() : this("!")

	override val name: String = "General Commands"

	private val commands : LinkedList<Command> = LinkedList()

	override fun load(): Boolean {
		commands.add(Command("man",
			"Sends information about a function or group of functions that are owned by a module, including"+
					"the functions name, the module it is a part of, what it does and how to use it.\n" +
					"\n" +
					"`${prefix}man <module id>`\n" +
					"`${prefix}man <function name>`\n",
			this)
		{text, event, _ ->
			val builder = StringBuilder()
			for (command in commands) {
				if (command.ownerModule.id == text.trim()) {
					builder.append("> `$prefix${command.functionName}`\n> \n" +
							"> ${command.description.replace("\n", "\n> ")}\n\n")
				}
			}
			for (command in commands) {
				if (command.functionName == text.trim()) {
					builder.append("> `$prefix${command.functionName}` from **${command.ownerModule.name}**\n>" +
							"\n>" + command.description.replace("\n", "\n> "))
				}
			}
			if (builder.toString() != "") {
				event.channel.sendMessage(builder).complete()
			} else {
				event.channel.sendMessage("unable to find function or module called ${text.trim()}").complete()
			}
		})
		return super.load()
	}

	override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
		if (event.author.isBot) return
		for (command in commands.filter {!it.requiresAdmin || event.member!!.hasPermission(Permission.ADMINISTRATOR)}) {
			val commandPrefix = "$prefix${command.functionName}"
			val content = event.message.contentRaw
			if (content.startsWith(commandPrefix)) {
				Logger.verbose("executing command ${command.functionName}")
				command.function(content.removePrefix(commandPrefix), event, getBot(event))
			}
		}
	}

	fun addCommands(vararg command : Command) {
		for (cmd in command) {
			commands.add(cmd)
		}
	}

	fun removeCommands(vararg command : Command) {
		for (cmd in command) {
			commands.remove(cmd)
		}
	}

	override fun onRemoveModule(module: IModule) {
		commands.removeIf {it.ownerModule == module}
	}
}
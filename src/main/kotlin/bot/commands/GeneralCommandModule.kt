package bot.commands

import bot.Bot
import bot.modules.IModule
import bot.modules.ListenerModule
import bot.Logger
import bot.modules.ModuleID
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.*
import kotlin.reflect.full.functions

@ModuleID("General Commands")
class GeneralCommandModule : ListenerModule() {

	override val name : String = "General Commands"

	private val commands : LinkedList<Command> = LinkedList()
	private var jdas = ArrayList<JDA>()

	override fun onStartup(bot : Bot) : Boolean {

		jdas.add(bot.getGuild().jda)
		for (cmd in commands) {
			bot.getGuild().jda.upsertCommand(cmd.functionName, cmd.shortDescription).complete()
		}

		addCommands(Command("man",
			"Retrieves information about a module",
			"Sends information about a function or group of functions that are owned by a module, including"+
					"the functions name, the module it is a part of, what it does and how to use it.\n" +
					"\n" +
					"`/man <module id>`\n" +
					"`/man <function name>`\n",
			this, false, CommandParameter(OptionType.STRING, "identifier", "identifier for function or module", true))
		{event, _ ->
			val builder = StringBuilder()
			val identifier = event.getOption("identifier")?.asString ?: ""
			for (command in commands.filter { it.ownerModule.id == identifier }) {
				builder.append("> `/${command.functionName}`\n> \n" +
						"> ${command.description.replace("\n", "\n> ")}\n\n")
			}
			for (command in commands.filter { it.functionName == identifier }) {
				builder.append("> `/${command.functionName}` from **${command.ownerModule.name}**\n>" +
						"\n>" + command.description.replace("\n", "\n> "))
			}
			if (builder.toString() != "") {
				event.reply(builder.toString()).complete()
			} else {
				event.reply("unable to find function or module called $identifier").complete()
			}
		})
		return super.onStartup(bot)
	}

	override fun onSlashCommandInteraction(event : SlashCommandInteractionEvent) {
		if (event.user.isBot) return

		Logger.verbose("received slash command ${event.commandString}")

		val calledCommands = commands
				.filter { !it.requiresAdmin || event.member!!.hasPermission(Permission.ADMINISTRATOR) }
				.filter { it.functionName == event.name }

		for (command in calledCommands) {
			Logger.verbose("executing command ${command.functionName} for ${event.member?.nickname}")
			event.deferReply(true)
			command.function(event, getBot(event))
		}
	}

	fun addCommands(vararg command : Command) {
		for (cmd in command) {
			jdas.forEach { jda ->
				jda.upsertCommand(cmd.functionName, cmd.shortDescription).flatMap { c ->
					val edit = c.editCommand()
					cmd.parameters.forEach {
						edit.addOption(it.type, it.name, it.description, it.required)
					}
					edit
				}.complete()

				Logger.verbose("upserted command ${cmd.functionName}")
			}
			commands.add(cmd)
		}
	}

	fun removeCommands(vararg command : Command) {
		for (cmd in command) {
			commands.remove(cmd)
		}
	}

	override fun onRemoveModule(module : IModule) {
		commands.removeIf {it.ownerModule == module}
	}
}
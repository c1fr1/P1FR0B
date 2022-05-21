package bot.commands

import bot.Bot
import bot.modules.IModule
import bot.modules.ListenerModule
import bot.Logger
import bot.modules.ModuleID
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Channel
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.*
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.functions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

@ModuleID("General Commands")
class GeneralCommandModule : ListenerModule() {

	override val name : String = "General Commands"

	private val commands : LinkedList<Command> = LinkedList()
	private var jdas = ArrayList<JDA>()

	override fun onStartup(bot : Bot) : Boolean {

		jdas.add(bot.getGuild().jda)

		for (module in bot.getModules()) {
			for (cmd in module::class.functions.filter { it.hasAnnotation<SlashCommand>() }) {
				addCommand(cmd, module)
			}
		}

		upsertAll()

		return super.onStartup(bot)
	}

	override fun onSlashCommandInteraction(event : SlashCommandInteractionEvent) {
		if (event.user.isBot) return

		Logger.verbose("received slash command ${event.commandString}")

		val calledCommands = commands
				.filter { it.functionName == event.name }

		for (command in calledCommands) {
			if (command.requiresAdmin && !event.member!!.hasPermission(Permission.ADMINISTRATOR)) {
				event.reply("you do not have permission to use this command").setEphemeral(true).complete()
				return
			}
			Logger.verbose("executing command ${command.functionName} for ${event.member?.nickname}")
			command.function(event, getBot(event))
		}
	}

	@SlashCommand("retrieves information about a module or command",
		"Sends information about a command or group of commands that are owned by a module, including"+
			"the functions name, the module it is a part of, what it does and how to use it.\n" +
			"\n" +
			"`/man <module id>`\n" +
			"`/man <command name>`")
	fun man(@CMDParam("identifier for function or module") identifier : String) : String {
		val builder = StringBuilder()

		if (getBot().getModules().any { it.id == identifier }) {
			for (command in commands.filter { it.ownerModule.id == identifier }) {
				builder.append(
					"> `/${command.functionName}`\n> \n" +
							"> ${command.description.replace("\n", "\n> ")}\n\n"
				)
			}
			if (builder.toString() == "") {
				return "no functions associated with `$identifier`"
			}
		}
		for (command in commands.filter { it.functionName == identifier }) {
			builder.append("> `/${command.functionName}` from **${command.ownerModule.name}**\n> " +
					"\n> " + command.description.replace("\n", "\n> "))
		}
		return if (builder.toString() != "") {
			builder.toString()
		} else {
			"unable to find function or module called `$identifier`"
		}
	}

	fun addCommands(vararg command : Command) {
		commands.addAll(command)
	}

	@SlashCommand("upserts specified commands",
		"uploads the function signatures of the specified commands to Discord's servers, this may result in " +
				"the commands being unusable for a significant period of time.\n" +
				"when no commands are specified, all commands will be upserted.\n" +
				"commands that already have a correct signature will not be upserted, this can be ovewritten using " +
				"`force-upsert`\n\n" +
				"`/upsert <command names>`", true)
	fun upsert(bot : Bot,
	           @CMDParam("space seperated list of command names") commands : String = "") : String {
		val commandList = commands.split(" ").filter { it.isNotEmpty() }
		val jda = bot.jda
		val ret = StringBuilder()
		if (commandList.isEmpty()) {
			val existingCommands = jda.retrieveCommands().complete()
			this.commands.forEach {c ->
				if (existingCommands.any { c == it as Any}) {
					ret.append("found existing correct signature for `${c.functionName}`, skipped\n")
				} else {
					upsert(c)
					ret.append("upserted `${c.functionName}`\n")
				}
			}
		} else {
			for (cmdName in commandList) {
				val command = this.commands.find { it.functionName == cmdName}
				if (command == null) {
					ret.append("could not find `$cmdName`, make sure the module responsible for it is activated\n")
					continue
				}
				val existingCommands = jda.retrieveCommands().complete()
				if (existingCommands.any { command == it as Any }) {
					ret.append("found existing correct signature for `$cmdName`, skipped\n")
				} else {
					upsert(command)
					ret.append("upserted `$cmdName`\n")
				}
			}
		}
		return ret.toString()
	}

	@SlashCommand("upserts specified commands",
		"uploads the function signatures of the specified commands to Discord's servers, this may result in " +
				"the commands being unusable for a significant period of time.\n" +
				"when no commands are specified, all commands will be upserted.\n\n" +
				"`/force-upsert <command names>`", true)
	fun forceUpsert(@CMDParam("space seperated list of command names") commands : String = "") : String {
		val commandList = commands.split(" ").filter { it.isNotEmpty() }
		val ret = StringBuilder()
		if (commandList.isEmpty()) {
			this.commands.forEach {
				upsert(it)
				ret.append("upserted `${it.functionName}`")
			}
		} else {
			for (cmdName in commandList) {
				val command = this.commands.find { it.functionName == cmdName}
				if (command == null) {
					ret.append("could not find `$cmdName`, make sure the module responsible for it is activated\n")
					continue
				}
				upsert(command)
				ret.append("upserted `$cmdName`\n")
			}
		}
		return ret.toString()
	}

	@SlashCommand("removes specified commands from this bots",
		"removes the specified commands from discords list of functions for this bot\n\n" +
				"`/unsert <command names>`", true)
	fun unsert(bot : Bot,
	           @CMDParam("space seperated list of command names") commands : String) : String {
		val uploadedCommands = bot.jda.retrieveCommands().complete()
		val names = commands.split(" ")

		val ret = StringBuilder()
		for (name in names) {
			val cmd = uploadedCommands.find { it.name == name }
			if (cmd == null) {
				ret.append("no command found called `$name`\n")
			} else {
				bot.jda.deleteCommandById(cmd.id).complete()
				ret.append("removed `$name`\n")
			}
		}
		return ret.toString()
	}

	fun upsertAll() {
		for (jda in jdas) {
			val existingCommands = jda.retrieveCommands().complete()
			commands.filter { c -> !existingCommands.any { c == it as Any} }.forEach { upsert(it) }
		}
	}

	fun upsert(vararg cmds : Command) {
		for (cmd in cmds) {
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
		}
	}

	fun addCommand(cmd : KFunction<*>, module : IModule) {

		val annotation = cmd.findAnnotation<SlashCommand>()!!
		val cmdName = cmd.name.toAPIRegex()
		val cmdFun = {e : SlashCommandInteractionEvent, bot : Bot ->
			val params = cmd.parameters.associateWith {
				when (it.type) {
					typeOf<SlashCommandInteractionEvent>() -> e
					typeOf<Bot>() -> bot
					else -> if (it.kind == KParameter.Kind.INSTANCE) module else
						CMDParam.getParamAsAny(e.getOption(it.name!!), it.type)
				}
			}.filter { !it.key.isOptional || it.value != null }
			val reply = if (cmd.returnType == typeOf<String>() || cmd.returnType.isSubtypeOf(typeOf<Message>())) {
				e.deferReply().complete()
			} else null
			val ret = cmd.callBy(params)
			if (cmd.returnType == typeOf<String>()) {
				reply!!.setEphemeral(false).editOriginal(ret as String).complete()
			}
			if (cmd.returnType.isSubtypeOf(typeOf<Message>())) {
				reply!!.setEphemeral(false).editOriginal(ret as Message).complete()
			}
		}
		val params = cmd.parameters.filter { it.kind != KParameter.Kind.INSTANCE }
		if (!params.all { it.hasAnnotation<CMDParam>() ||
					it.type == typeOf<SlashCommandInteractionEvent>() || it.type == typeOf<Bot>()}) {
			Logger.error("$cmdName in ${module.name} missing parameter annotation")
			return
		}
		val options = params
			.filter { it.type != typeOf<SlashCommandInteractionEvent>() && it.type != typeOf<Bot>() }
			.map {
				val type = when {
					it.type == typeOf<String>() || it.type == typeOf<String?>() -> OptionType.STRING
					it.type == typeOf<Long>() || it.type == typeOf<Long?>()-> OptionType.STRING
					it.type == typeOf<Int>() || it.type == typeOf<Int?>()-> OptionType.INTEGER
					it.type == typeOf<Boolean>() || it.type == typeOf<Boolean?>()-> OptionType.BOOLEAN
					it.type.isSubtypeOf(typeOf<User>()) -> OptionType.USER
					it.type.isSubtypeOf(typeOf<Channel>()) -> OptionType.CHANNEL
					it.type.isSubtypeOf(typeOf<Role>()) -> OptionType.ROLE
					it.type.isSubtypeOf(typeOf<IMentionable>()) -> OptionType.MENTIONABLE
					it.type.isSubtypeOf(typeOf<Number>()) -> OptionType.NUMBER
					it.type.isSubtypeOf(typeOf<Attachment>()) -> OptionType.ATTACHMENT
					else -> OptionType.UNKNOWN
				}
				val paramAnnotation = it.findAnnotation<CMDParam>()!!
				CommandParameter(type, it.name!!.toAPIRegex(), paramAnnotation.description, !it.isOptional)
			}.toTypedArray()
		val command = Command(cmdName,
			annotation.shortDescription,
			annotation.description,
			module,
			annotation.requiresAdmin,
			*options
		) {e, b -> cmdFun(e, b)}
		addCommands(command)
	}

	fun removeCommands(vararg command : Command) {
		for (cmd in command) {
			commands.remove(cmd)
		}
	}

	override fun onAddModule(module : IModule) {
		val cmds = module::class.functions.filter { it.hasAnnotation<SlashCommand>() }
		for (cmd in cmds) {
			addCommand(cmd, module)
		}
	}

	override fun onRemoveModule(module : IModule) {
		commands.removeIf {it.ownerModule == module}
	}
}

private fun String.toAPIRegex() = fold("") {acc, c -> if (c.isUpperCase()) "$acc-${c.lowercase()}" else "$acc$c" }
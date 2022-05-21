package bot.commands

import bot.Bot
import bot.modules.IModule
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.Command as JCommand

class CommandParameter(
	val type : OptionType,
	val name : String,
	val description : String,
	val required : Boolean = false
) {
	override fun equals(other : Any?) : Boolean {
		return when (other) {
			is JCommand.Option ->
				other.name == name && other.type == type && other.description == description &&
						other.isRequired == required
			else -> false
		}
	}
}

class Command(
	val functionName : String,
	val shortDescription : String,
	val description : String,
	val ownerModule : IModule,
	val requiresAdmin : Boolean = false,
	vararg val parameters : CommandParameter,
	val function : (SlashCommandInteractionEvent, Bot) -> Unit
) {
	override fun equals(other : Any?) : Boolean {
		return when (other) {
			is JCommand -> functionName == other.name && shortDescription == other.description &&
					parameters.all { p -> other.options.any { p == it as Any } }
			else -> false
		}
	}
}

package bot.commands

import bot.Bot
import bot.modules.IModule
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.Dictionary

class CommandParameter(
	val type : OptionType,
	val name : String,
	val description : String,
	val required : Boolean = false
)

class Command(
	val functionName : String,
	val shortDescription : String,
	val description : String,
	val ownerModule : IModule,
	val requiresAdmin : Boolean = false,
	vararg val parameters : CommandParameter,
	val function : (SlashCommandInteractionEvent, Bot) -> Unit
)

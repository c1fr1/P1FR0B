package bot.commands

import bot.Bot
import bot.modules.IModule
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

data class Command(
	val functionName : String,
	val description : String = "no description for this command",
	val ownerModule : IModule,
	val requiresAdmin : Boolean = false,
	val function : (String, GuildMessageReceivedEvent, Bot) -> Unit)

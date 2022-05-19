package modules

import bot.*
import bot.commands.Command
import bot.commands.CommandParameter
import bot.commands.GeneralCommandModule
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.interactions.commands.OptionType

@ModuleID("Wiggle")
class WiggleModule : ListenerModule() {

	override val name: String
		get() = "Wiggle"

	override fun load() : Boolean {
		return super.load() && IDS.getID("WIGGLE") != null
	}

	override fun onStartup(bot : Bot) : Boolean {
		val commandModule = bot.resolveDependency<GeneralCommandModule>() ?: return false
		commandModule.addCommands(
			Command("wiggle", "wiggle", "woggle", this, false,
			CommandParameter(OptionType.STRING, "message-id", "id of the message you wish to wiggle"))
		{e, _ ->
			val mID = e.getOption("message-id")?.asString ?: ""
			var target = e.channel.getHistoryAround(e.messageChannel.latestMessageId, 1).complete().retrievedHistory[0]
			if (mID.toLongOrNull() != null) {
				target = e.channel.getHistoryAround(mID, 1).complete().getMessageById(mID) ?: target
			}
			target.addReaction(bot.getGuild().getEmoteById(IDS.getID("WIGGLE")!!)!!).complete()
			e.reply("woggle").flatMap { it.deleteOriginal() }.complete()
		})
		return super.onStartup(bot)
	}

	override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
		if (event.user!!.isBot) return
		val m = event.channel.getHistoryAround(event.messageId, 1).complete()
			.getMessageById(event.messageId) ?: return
		for (reaction in m.reactions.filter {it.reactionEmote.id == IDS.getID("WIGGLE")!!}) {
			reaction.removeReaction().complete()
		}
	}
}
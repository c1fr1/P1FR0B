package modules

import bot.*
import bot.commands.*
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent

@ModuleID("Wiggle")
class WiggleModule : ListenerModule() {

	override val name: String
		get() = "Wiggle"

	override fun load() : Boolean {
		return super.load() && IDS.getID("WIGGLE") != null
	}

	@SlashCommand("wiggle", "wOggle")
	fun wiggle(e : SlashCommandInteractionEvent, bot : Bot,
	           @CMDParam("id of the message you wish to wiggle") messageId : Long = 0L) {
		var target = e.channel.getHistoryAround(e.messageChannel.latestMessageId, 1).complete().retrievedHistory[0]
		target = e.channel.getHistoryAround(messageId, 1).complete().getMessageById(messageId) ?: target
		target.addReaction(bot.getGuild().getEmoteById(IDS.getID("WIGGLE")!!)!!).complete()
		e.reply("w0ggle").flatMap { it.deleteOriginal() }.complete()
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
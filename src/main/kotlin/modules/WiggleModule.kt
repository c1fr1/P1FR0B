package modules

import bot.*
import bot.commands.*
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.emoji.CustomEmoji
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.entities.emoji.EmojiUnion
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent

@ModuleID("Wiggle")
class WiggleModule : ListenerModule() {

	override val name = "Wiggle"

	override fun load() : Boolean {
		return super.load() && IDS["WIGGLE"] != null
	}

	@SlashCommand("wiggle", "wOggle")
	fun wiggle(e : SlashCommandInteractionEvent, bot : Bot,
	           @CMDParam("id of the message you wish to wiggle") messageId : Long = 0L) {
		var target = e.channel.getHistoryAround(e.messageChannel.latestMessageId, 1).complete().retrievedHistory[0]
		target = e.channel.getHistoryAround(messageId, 1).complete().getMessageById(messageId) ?: target
		target.addReaction(bot.getGuild().getEmojiById(IDS["WIGGLE"]!!)!!).complete()
		e.reply("w0ggle").flatMap { it.deleteOriginal() }.complete()
	}

	override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
		if (event.user!!.isBot) return
		val m = event.channel.getHistoryAround(event.messageId, 1).complete()
			.getMessageById(event.messageId) ?: return
		for (reaction in m.reactions.filter { it.emoji.type == Emoji.Type.CUSTOM }
			.filter {it.emoji.asCustom().id == IDS["WIGGLE"]!!}) {
			reaction.removeReaction().complete()
		}
	}
}
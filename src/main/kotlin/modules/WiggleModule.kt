package modules

import bot.*
import bot.commands.Command
import bot.commands.GeneralCommandModule
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent

@ModuleID("Wiggle")
class WiggleModule : ListenerModule() {

	override val name: String
		get() = "Wiggle"

	override fun load(): Boolean {
		return super.load() && IDS.getID("WIGGLE") != null
	}

	override fun onStartup(bot: Bot): Boolean {
		val commandModule = bot.resolveDependency(GeneralCommandModule::class) ?: return false
		commandModule.addCommands(
			Command("wiggle", "wiggle", this)
		{m, e, b ->
			var target = e.channel.getHistoryBefore(e.messageId, 1).complete().retrievedHistory[0]
			if (m.trim().toLongOrNull() != null) {
				target = e.channel.getHistoryAround(m.trim(), 1).complete().getMessageById(m.trim()) ?: target
			}
			target.addReaction(e.guild.getEmoteById(IDS.getID("WIGGLE")!!)!!).complete()
			e.message.delete().complete()
		}
		)
		return super.onStartup(bot)
	}

	override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
		if (event.user.isBot) return
		val m = event.channel.getHistoryAround(event.messageId, 1).complete()
			.getMessageById(event.messageId) ?: return
		for (reaction in m.reactions.filter {it.reactionEmote.id == IDS.getID("WIGGLE")!!}) {
			reaction.removeReaction().complete()
		}
	}
}
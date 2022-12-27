package modules

import bot.commands.CMDParam
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.concurrent.TimeUnit

@ModuleID("pleasure")
class PleasureModule : ListenerModule() {
    override val name = "pleasure"

    @SlashCommand("pleasure", "send pleasure someone's way")
    fun pleasure(event : SlashCommandInteractionEvent, @CMDParam("who to pleasure") playerName: String? = null) {
        val user = if (playerName == null) {
            getBot().jda.getUserById(258485243038662657L) /* ebet */
        } else {
            val searchName = playerName.lowercase()
            getBot().getGuild().members.find { (it.nickname ?: it.user.name).lowercase().contains(searchName) }?.user
        }

        if (user == null) {
            event.reply("Can't pleasure that person").queue {
                it.deleteOriginal().queueAfter(1, TimeUnit.SECONDS)
            }
            return
        }

        event.reply("pleasuring...").and(user.openPrivateChannel()) { hook, channel ->
            hook.deleteOriginal().queue()
            for (i in 0 until 5) {
                channel.sendMessage("augh you bitch").queue()
            }
        }.queue()
    }
}

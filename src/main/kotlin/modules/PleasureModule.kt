package modules

import bot.commands.CMDParam
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent

@ModuleID("pleasure")
class PleasureModule : ListenerModule() {
    override val name = "pleasure"

    @SlashCommand("pleasure", "send pleasure someone's way")
    fun pleasure(event : SlashCommandInteractionEvent, @CMDParam("who to pleasure") playerName: String? = null) {
        val jda = getBot().jda

        val user = if (playerName == null) {
            jda.getUserById(258485243038662657L) /* ebet */
        } else {
            getBot().getGuild().loadMembers()
            getBot().getGuild().jda.getUsersByName(playerName, true).firstOrNull()
        }

        if (user == null) {
            event.reply("Can't find that person").complete()
            return
        }


    }
}
package modules

import bot.*
import bot.commands.Command
import bot.commands.CommandParameter
import bot.commands.GeneralCommandModule
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader

@ModuleID("Role Manager")
class RoleManagerModule(private val reactionMessageID : String, private val reactionMessageChannel : String, private val roleNameFile : File) : ListenerModule() {

	constructor(reactionMessageID: String, reactionMessageChannel : String, roleNameFile: String) : this(reactionMessageID, reactionMessageChannel, File(roleNameFile))

	override val name: String
		get() = "Role Manager"

	override fun load(): Boolean {
		return roleNameFile.isFile && super.load()
	}

	override fun onStartup(bot: Bot): Boolean {
		val commandModule = bot.resolveDependency(GeneralCommandModule::class) ?: return false
		commandModule.addCommands(
			Command("add-role",
				"adds a manageable role",
			"adds a role to the list of manageable roles\n\n" +
					"`/add-role <new role>`",
			this, true,
			CommandParameter(OptionType.STRING, "name", "name of the new role", true)
			)
			{e, _ ->
				val roleName = e.getOption("name")!!.asString
				if (isManageable(roleName)) {
					e.reply("role is already being managed").complete()
					return@Command
				}
				if (!bot.getGuild().emotes.any {it.name.equals(roleName, true)}) {
					e.reply("there must be an emote for this role first").complete()
					return@Command
				}
				bot.getGuild().getTextChannelById(reactionMessageChannel)!!.getHistoryAround(reactionMessageID, 1).complete().retrievedHistory[0]!!
					.addReaction(bot.getGuild().emotes.find { it.name.equals(roleName, true) }!!).complete()
				val fos = FileOutputStream(roleNameFile, true)
				fos.write("\n$roleName".encodeToByteArray())
				fos.close()
				if (!bot.getGuild().roles.any { it.name == roleName }) {
					bot.getGuild().createRole().setName(roleName).setPermissions().complete()
				}
				e.reply("Role added!").complete()
			}
		)
		return super.onStartup(bot)
	}

	override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
		if (event.user!!.isBot) return
		if (event.messageId == reactionMessageID && isManageable(event.reactionEmote.name)) {
			event.reaction.removeReaction(event.user!!).complete()
			val role = getRole(event.guild, event.reactionEmote.name) ?: return
			if (event.member!!.roles.any {it.name == role.name}) {
				event.guild.removeRoleFromMember(event.member!!, role).complete()
			} else {
				event.guild.addRoleToMember(event.member!!, role).complete()
			}
		}
	}

	private fun getRole(guild : Guild, name : String) : Role?
		= guild.roles.find {it.name.equals(name, true)}

	private fun isManageable(roleName : String) : Boolean {
		return FileReader(roleNameFile).readLines().any {it.equals(roleName, true)}
	}
}
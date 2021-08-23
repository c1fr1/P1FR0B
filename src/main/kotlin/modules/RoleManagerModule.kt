package modules

import bot.*
import bot.commands.Command
import bot.commands.GeneralCommandModule
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
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
			Command("addRole",
			"adds a role to the list of manageable roles\n\n" +
					"`${commandModule.prefix}addRole <new role>`",
			this, true)
			{message, e, _ ->
				if (isManageable(message.trim())) {
					e.channel.sendMessage("role is already being managed").complete()
					return@Command
				}
				if (!e.guild.emotes.any {it.name.equals(message.trim(), true)}) {
					e.channel.sendMessage("there must be an emote for this role first").complete()
					return@Command
				}
				e.guild.getTextChannelById(reactionMessageChannel)!!.getHistoryAround(reactionMessageID, 1).complete().retrievedHistory[0]!!
					.addReaction(e.guild.emotes.find { it.name.equals(message.trim(), true) }!!).complete()
				val fos = FileOutputStream(roleNameFile, true)
				fos.write("\n${message.trim()}".encodeToByteArray())
				fos.close()
				if (!e.guild.roles.any {it.name.equals(message.trim())}) {
					e.guild.createRole().setName(message.trim()).setPermissions().complete()
				}
				e.channel.sendMessage("Role added!").complete()
			}
		)
		return super.onStartup(bot)
	}

	override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
		if (event.user.isBot) return
		if (event.messageId == reactionMessageID && isManageable(event.reactionEmote.name)) {
			event.reaction.removeReaction(event.user).complete()
			val role = getRole(event.guild, event.reactionEmote.name) ?: return
			if (event.member.roles.any {it.name == role.name}) {
				event.guild.removeRoleFromMember(event.member, role).complete()
			} else {
				event.guild.addRoleToMember(event.member, role).complete()
			}
		}
	}

	private fun getRole(guild : Guild, name : String) : Role?
		= guild.roles.find {it.name.equals(name, true)}

	private fun isManageable(roleName : String) : Boolean {
		return FileReader(roleNameFile).readLines().any {it.equals(roleName, true)}
	}
}
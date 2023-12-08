package modules

import bot.*
import bot.commands.*
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader

@ModuleID("Role Manager")
class RoleManagerModule(private val reactionMessageID : String, private val reactionMessageChannel : String, private val roleNameFile : File) : ListenerModule() {

	constructor(reactionMessageID: String, reactionMessageChannel : String, roleNameFile: String) : this(reactionMessageID, reactionMessageChannel, File(roleNameFile))

	override val name = "Role Manager"

	override fun load(): Boolean {
		return roleNameFile.isFile && super.load()
	}

	@SlashCommand(
		"adds a manageable role",
		"adds a role to the list of manageable roles\n\n" +
				"`/add-role <name>`",
		true)
	fun addRole(bot : Bot,  @CMDParam("name of the new role") name : String) : String {
		if (isManageable(name)) {
			return "role is already being managed"
		}
		if (!bot.getGuild().emojis.any {it.name.equals(name, true)}) {
			return "there must be an emote for this role first"
		}
		bot.getGuild().getTextChannelById(reactionMessageChannel)!!.getHistoryAround(reactionMessageID, 1)
			.complete().retrievedHistory[0]!!.addReaction(bot.getGuild().emojis
			.find { it.name.equals(name, true) }!!).complete()
		val fos = FileOutputStream(roleNameFile, true)
		fos.write("\n$name".encodeToByteArray())
		fos.close()
		if (!bot.getGuild().roles.any { it.name == name }) {
			bot.getGuild().createRole().setName(name).setPermissions().complete()
		}
		return "Role added!"
	}

	@SlashCommand(
		"resets reactions",
		"clears all of the reactions on the reaction message, then adds them again for each manageable role" +
				"`/reset-reactions`",
		true)
	fun resetReactions(bot : Bot) : String {
		val message = bot.getGuild().getTextChannelById(reactionMessageChannel)!!
			.getHistoryAround(reactionMessageID, 1).complete().retrievedHistory[0]!!
		message.reactions.map { it.removeReaction().complete() }

		FileReader(roleNameFile).readLines().map { roleName ->
			bot.getGuild().emojis.find { it.name.lowercase() == roleName.lowercase() }
				?.let { message.addReaction(it).complete() }
		}
		return "reactions reset"
	}

	override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
		if (event.user!!.isBot) return
		if (event.messageId == reactionMessageID && isManageable(event.emoji.name)) {
			event.reaction.removeReaction(event.user!!).complete()
			val role = getRole(event.guild, event.emoji.name) ?: return
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
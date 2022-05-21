package bot.commands

import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import kotlin.reflect.KType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class CMDParam(val description : String) {
	companion object {
		fun getParamAsAny(value : OptionMapping?, type : KType) : Any? {
			value ?: return null
			return when {
				type == typeOf<String>() || type == typeOf<String?>() -> value.asString
				type == typeOf<Boolean>() || type == typeOf<Boolean?>() -> value.asBoolean
				type == typeOf<Long>() || type == typeOf<Long?>() -> value.asString.toLongOrNull()
				type == typeOf<Int>() || type == typeOf<Int?>() -> value.asInt
				type == typeOf<Double>() || type == typeOf<Double?>() -> value.asDouble

				type.isSupertypeOf(typeOf<Member>()) -> value.asMember
				type.isSupertypeOf(typeOf<User>()) -> value.asUser
				type.isSupertypeOf(typeOf<Role>()) -> value.asRole

				type.isSupertypeOf(typeOf<NewsChannel>()) -> value.asNewsChannel
				type.isSupertypeOf(typeOf<ThreadChannel>()) -> value.asThreadChannel
				type.isSupertypeOf(typeOf<TextChannel>()) -> value.asGuildChannel

				type.isSupertypeOf(typeOf<StageChannel>()) -> value.asStageChannel
				type.isSupertypeOf(typeOf<VoiceChannel>()) -> value.asVoiceChannel
				type.isSupertypeOf(typeOf<AudioChannel>()) -> value.asAudioChannel

				type.isSupertypeOf(typeOf<GuildMessageChannel>()) -> value.asMessageChannel
				type.isSupertypeOf(typeOf<GuildChannel>()) -> value.asGuildChannel
				type.isSupertypeOf(typeOf<IMentionable>()) -> value.asMentionable

				type.isSupertypeOf(typeOf<Attachment>()) -> value.asAttachment
				else -> null
			}
		}
	}
}

@Target(AnnotationTarget.FUNCTION)
annotation class SlashCommand(
	val shortDescription : String,
    val description : String,
	val requiresAdmin : Boolean = false
)

package bot.commands

import net.dv8tion.jda.api.JDA

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class CMDParam(val description : String)

@Target(AnnotationTarget.FUNCTION)
annotation class CommandFunction(
	val shortDescription : String,
    val description : String,
	val requiresAdmin : Boolean = false
)

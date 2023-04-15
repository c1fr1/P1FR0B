package modules

import bot.commands.CMDParam
import bot.commands.SlashCommand
import bot.modules.ListenerModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.floor

@ModuleID("commis")
class CommisModule() : ListenerModule() {
	override val name = "commis"

	@SlashCommand("starts commissions",
	"starts a round of commissions")
	fun startCommis(e : SlashCommandInteractionEvent,
		@CMDParam("number of minutes for this round") minutes : Int = 5,
		@CMDParam("number of additional seconds for this round") seconds : Int = 0) {
		val totalDuration = minutes * 60 + seconds
		var remainingSeconds = totalDuration

		val getMessageString = {
			val mValue = remainingSeconds / 60
			var sValue = "${remainingSeconds - mValue * 60}"
			while (sValue.length < 2) {
				sValue = "0$sValue"
			}
			//val barOptions = " ▏▎▍▌▋▊▉█"
			val barOptions = "█"
			val barLength = 40
			val percentRemaining = remainingSeconds.toFloat() / totalDuration.toFloat()

			val barText = StringBuilder()
			for (i in 0 until barLength) {
				val thisSegment = (percentRemaining) * 40 - i
				barText.append(if (thisSegment >= 1) "█" else if (thisSegment <= 0) " " else {
						barOptions[floor(thisSegment * barOptions.length).toInt()]
				})
			}

			"starting commissions round!\nremaining time: $mValue:$sValue\n`$barText`"
		}
		val r = e.reply(getMessageString()).complete()
		timer(null, false, Date(), 1000) {
			remainingSeconds -= 1
			r.editOriginal(getMessageString()).complete()
			if (remainingSeconds <= 0) {
				e.channel.sendMessage("TIME UP!").complete()
				this.cancel()
			}
		}
	}
}
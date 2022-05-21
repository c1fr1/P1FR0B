package modules

import bot.*
import bot.commands.*
import bot.modules.BotModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.Region
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.*

@ModuleID("Pseudo Admin")
class PseudoAdminModule : BotModule() {
	override val name: String
		get() = "Pseudo Admin"

	private val primaryServer = Region.US_EAST
	private val secondaryServer = Region.US_CENTRAL

	private val cooldown = 30000000000L

	private var lastCallTime = System.nanoTime() - cooldown

	@CommandFunction("toggles or changes the current region",
		"toggles or changes the current region")
	fun region(e : SlashCommandInteractionEvent,
		@CMDParam("region you wish to change the vcs server to") region : String = "") : String {
		val targetVC =
			e.guild?.voiceChannels?.find { vc -> vc.members.any { member -> member.idLong == e.member?.idLong }}
		if (System.nanoTime() - cooldown <= lastCallTime) {
			val remainingTime = (System.nanoTime() - lastCallTime) / 1e9
			return "changing regions is rate limited, please wait another $remainingTime seconds"
		}
		if (targetVC == null) {
			return "you must join a voice channel first"
		}

		var newRegion = Region.fromKey(region.lowercase(Locale.getDefault()))
		if (newRegion == Region.UNKNOWN) {
			newRegion = if (targetVC.region == primaryServer) {
				secondaryServer
			} else {
				primaryServer
			}
		}
		targetVC.manager.setRegion(newRegion).complete()
		lastCallTime = System.nanoTime()
		return "voice channel region changed to ${newRegion.name}"
	}
}
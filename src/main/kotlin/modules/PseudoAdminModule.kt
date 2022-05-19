package modules

import bot.*
import bot.commands.Command
import bot.commands.CommandParameter
import bot.commands.GeneralCommandModule
import bot.modules.BotModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.Region
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

	override fun onStartup(bot: Bot): Boolean {
		val commandModule = bot.resolveDependency<GeneralCommandModule>() ?: return false
		commandModule.addCommands(Command("region",
			"toggles or changes the current region",
			"toggles or changes the current region",
			this, false,
			CommandParameter(OptionType.STRING, "region", "region you wish to change the vcs server to")
		)
		{e, _ ->
			val targetVC =
				e.guild?.voiceChannels?.find { vc -> vc.members.any { member -> member.idLong == e.member?.idLong }}
			if (System.nanoTime() - cooldown <= lastCallTime) {
				val remainingTime = (System.nanoTime() - lastCallTime) / 1e9
				e.reply("changing regions is rate limited, please wait another $remainingTime seconds")
					.complete()
				return@Command
			}
			if (targetVC == null) {
				e.reply("you must join a voice channel first")
				return@Command
			}

			val rID = e.getOption("region")?.asString ?: ""

			var newRegion = Region.fromKey(rID.lowercase(Locale.getDefault()))
			if (newRegion == Region.UNKNOWN) {
				newRegion = if (targetVC.region == primaryServer) {
					secondaryServer
				} else {
					primaryServer
				}
			}
			targetVC.manager.setRegion(newRegion).complete()
			e.reply("voice channel region changed to ${newRegion.name}").complete()
			lastCallTime = System.nanoTime()
		})
		return super.onStartup(bot)
	}
}
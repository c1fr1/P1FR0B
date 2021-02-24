package modules

import bot.*
import bot.commands.Command
import bot.commands.GeneralCommandModule
import bot.modules.BotModule
import bot.modules.ModuleID
import net.dv8tion.jda.api.Region

@ModuleID("Pseudo Admin")
class PseudoAdminModule : BotModule() {
	override val name: String
		get() = "Pseudo Admin"

	private val primaryServer = Region.US_EAST
	private val secondaryServer = Region.US_CENTRAL

	private val cooldown = 30000000000L

	private var lastCallTime = System.nanoTime() - cooldown

	override fun onStartup(bot: Bot): Boolean {
		val commandModule = bot.resolveDependency(GeneralCommandModule::class) ?: return false
		commandModule.addCommands(Command("region",
			"toggles or changes the current region",
			this)
		{m, e, _ ->
			if (System.nanoTime() - cooldown <= lastCallTime) {
				val remainingTime = (System.nanoTime() - lastCallTime) / 1e9
				e.channel
					.sendMessage("changing regions is rate limited, please wait another $remainingTime seconds")
					.complete()
				return@Command
			}
			var newRegion = Region.fromKey(m.trim().toLowerCase())
			if (newRegion == Region.UNKNOWN) {
				newRegion = if (e.guild.region == primaryServer) {
					secondaryServer
				} else {
					primaryServer
				}
			}
			e.guild.manager.setRegion(newRegion).complete()
			e.channel
				.sendMessage("voice channel region changed to ${newRegion.name} <@!${e.member!!.id}>").complete()
			lastCallTime = System.nanoTime()
		})
		return super.onStartup(bot)
	}
}
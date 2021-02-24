import bot.Bot
import bot.IDS
import bot.Logger
import modules.PseudoAdminModule
import modules.RoleManagerModule
import modules.WelcomeModule
import modules.amongUs.AmongUsModule
import modules.dota.ResponseModule
import modules.moduleManager.ModuleManagerModule
import modules.voiceChannel.VCModule
import java.io.FileReader
import kotlin.io.path.ExperimentalPathApi
import kotlin.system.exitProcess

@ExperimentalPathApi
fun main() {
	try {
		val bot = Bot(IDS.getID("DPACK_GUILD")!!)
		bot.addModule(ModuleManagerModule())
		bot.addModule(ResponseModule())
		bot.addModule(AmongUsModule(IDS.getID("AMONG_US_TEXT_CHANNEL")!!))
		bot.addModule(VCModule(IDS.getID("VC_ROLE")!!, IDS.getID("VC_TEXT_CHANNEL")!!))
		bot.addModule(PseudoAdminModule())
		bot.addModule(RoleManagerModule(IDS.getID("ROLE_TOGGLE_MESSAGE")!!, "resources/roles"))
		bot.addModule(
			WelcomeModule(FileReader("resources/welcome-message.txt").readText(),
				IDS.getID("MEMBER_ROLE")!!))
		bot.startup()
	} catch (e : Exception) {
		Logger.logError(e)
		exitProcess(-1)
	}
}
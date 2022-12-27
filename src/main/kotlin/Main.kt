import bot.Bot
import bot.IDS
import bot.Logger
import modules.PseudoAdminModule
import modules.RoleManagerModule
import modules.WelcomeModule
import modules.WiggleModule
import modules.amongUs.AmongUsModule
import modules.dota.ResponseModule
import modules.moduleManager.ModuleManagerModule
import modules.voiceChannel.VCModule
import java.io.FileReader
import kotlin.io.path.ExperimentalPathApi
import kotlin.system.exitProcess

@ExperimentalPathApi
fun main(args: Array<String>) {
	/* call with a command line argument to redirect the IDS_PUBLIC file */
	IDS.init(args.getOrNull(0))

	try {
		val bot = Bot(IDS.get("DPACK_GUILD")!!)
		bot.addModule(ModuleManagerModule())
		bot.addModule(ResponseModule())
		bot.addModule(AmongUsModule(IDS.get("AMONG_US_TEXT_CHANNEL")!!))
		bot.addModule(VCModule(IDS.get("VC_ROLE")!!, IDS.get("VC_TEXT_CHANNEL")!!))
		bot.addModule(PseudoAdminModule())
		bot.addModule(WiggleModule())
		bot.addModule(RoleManagerModule(IDS.get("ROLE_TOGGLE_MESSAGE")!!, IDS.get("ROLE_TOGGLE_CHANNEL")!!, "resources/roles"))
		bot.addModule(
			WelcomeModule(FileReader("resources/welcome-message.txt").readText(),
				IDS.get("MEMBER_ROLE")!!))
		bot.startup()
	} catch (e : Exception) {
		Logger.logError(e)
		exitProcess(-1)
	}
}
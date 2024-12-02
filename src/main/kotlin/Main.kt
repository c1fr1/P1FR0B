import bot.Bot
import bot.IDS
import bot.Logger
import modules.*
import modules.amongUs.AmongUsModule
import modules.dota.ResponseModule
import modules.moduleCommunications.ModuleCommunicatorModule
import modules.moduleManager.ModuleManagerModule
import modules.nyt.NYTModule
import modules.voiceChannel.VCModule
import java.io.FileReader
import kotlin.io.path.ExperimentalPathApi
import kotlin.system.exitProcess

@ExperimentalPathApi
fun main(args: Array<String>) {
	/* call with a command line argument to redirect the IDS_PUBLIC file */
	IDS.init(args.getOrNull(0))

	try {
		val bot = Bot(IDS["DPACK_GUILD"]!!)
		bot.addModule { ModuleManagerModule() }
		bot.addModule { ModuleCommunicatorModule() }
		bot.addModule { ResponseModule() }
		bot.addModule { AmongUsModule(IDS["AMONG_US_TEXT_CHANNEL"]!!) }
		bot.addModule { VCModule(IDS["VC_ROLE"]!!, IDS["VC_TEXT_CHANNEL"]!!) }
		bot.addModule { PseudoAdminModule() }
		bot.addModule { WiggleModule() }
		bot.addModule {
			RoleManagerModule(
				IDS["ROLE_TOGGLE_MESSAGE"]!!,
				IDS["ROLE_TOGGLE_CHANNEL"]!!,
				"resources/roles"
			)
		}
		bot.addModule {
			WelcomeModule(
				FileReader("resources/welcome-message.txt").readText(),
				IDS["MEMBER_ROLE"]!!
			)
		}
		bot.addModule { PleasureModule(true) }
		bot.addModule { CommisModule() }
		bot.addModule { AutoNotifModule() }
		bot.addModule { ReactModule() }
		bot.addModule { NYTModule() }
		bot.addModule { MCTriviaModule() }

		bot.startup()
	} catch (e : Throwable) {
		Logger.logError(e)
		exitProcess(-1)
	}
}
package bot.modules

import bot.Bot

/**
 * Represents a functionality or set of functionalities which the bot represents.
 * @author Varas#5480
 */
interface IModule {
	/**
	 * Name of the module for use in errors or status messages.
	 */
	val name : String

	/**
	 * When interfacing with a module while the bot is running, use this identifier.
	 */
	val id : String
		get() = ModuleID.getID(name)

	/**
	 * function gets called when the bot goes online or when the module is started.
	 * @return whether or loading was successful, if it was not, then this module cannot be ran.
	 */
	fun load() : Boolean {return true}

	/**
	 * function that gets called after startup has completed, and all modules are loaded, or after the module was added.
	 * @param bot reference to the JDA object of a bot that is currently running.
	 * @return whether or startup was successful, if it was not, then this module cannot be ran.
	 */
	fun onStartup(bot : Bot) : Boolean {return true}

	/**
	 * function to clean up temporary resources
	 * @param bot reference to the JDA object of a bot that is currently running.
	 */
	fun stop(bot : Bot)

	/**
	 * event that gets called whenever another module is added to a bot that this module is serving. This is guaranteed.
	 * to have been called for every other module that is on the bot.
	 * @param module module that has been added to the bot.
	 */
	fun onAddModule(module : IModule) {}

	/**
	 * event that gets called whenever a module has been removed from a bot that this module is serving.
	 * @param module module that has been removed from the bot.
	 */
	fun onRemoveModule(module : IModule) {}
}
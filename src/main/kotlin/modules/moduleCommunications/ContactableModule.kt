package modules.moduleCommunications

interface ContactableModule {
	fun receiveMessage(message : String) : String?
}
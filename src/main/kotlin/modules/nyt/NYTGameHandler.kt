package modules.nyt

import bot.storage.Storage
import bot.storage.createStorage
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import java.time.LocalDate

abstract class NYTGameHandler<T : PerformanceData> {
	abstract val gameName : String

	abstract val storage : Storage<HashMap<Long, ActivityData<T>>>
	abstract val playerHistories : HashMap<Long, ActivityData<T>>

	protected abstract val referenceNumber : Int
	protected open val referenceDate : LocalDate = LocalDate.of(2024, 5, 8)

	abstract val loadPerformanceData : (String) -> T
	fun dateFromNumber(num : Int) = referenceDate.plusDays((num - referenceNumber).toLong())
	fun numberFromDate(date : LocalDate) = referenceNumber + date.toEpochDay() - referenceDate.toEpochDay()
	fun isGameNumber(num : Int) = num <= numberFromDate(LocalDate.now()) + 1

	fun handleGameSubmission(userID : String, summary : String, message : Message) {
		val member = message.guild.getMemberById(userID) ?: return
		val gameNum = updateGameHistory(member, summary) ?: return
		var countSubmitted = 0
		var countActive = 0
		for ((_, history) in playerHistories) {
			if (history.avoidsSpoilers(gameNum)) {
				++countActive
				if (history.hasSubmitted(gameNum)) ++countSubmitted
			}
		}
		if (countSubmitted == countActive) {
			message.reply("All active members has submitted $gameName #$gameNum, you are free to discuss without spoilers").mentionRepliedUser(false).complete()
		} else {
			message.reply("$countSubmitted/$countActive have submitted $gameName #$gameNum").mentionRepliedUser(false).complete()
		}
		storage.save(playerHistories)
	}

	fun loadStorage() = createStorage(
		"NYT${gameName}Activity.txt",
		readData = {
			val lines = it.readLines()
			val ret = HashMap<Long, ActivityData<T>>()
			for (line in lines) {
				if (line.isEmpty()) continue
				val (id, activityData) = line.takeDelimited(":")
				ret[id.toLong()] = ActivityData(activityData, loadPerformanceData)
			}
			ret
		},
		writeData = {file, histories ->
			file.writeText(histories.map {(id, ad) -> "$id:${ad.savableString()}" }.joinToString("\n"))
		},
		createDefault = {HashMap()}
	)

	//return game number
	abstract fun updateGameHistory(user : Member, summary : String) : Int?
}



//game has a summary type, activity data type
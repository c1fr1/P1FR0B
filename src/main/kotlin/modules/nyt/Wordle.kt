package modules.nyt

import net.dv8tion.jda.api.entities.Member
import kotlin.math.min

class WordlePerformanceData(
	val guessDistribution : IntArray = IntArray(7) {0},
	val hardmodeGuessDistribution : IntArray = IntArray(7) {0}
) : PerformanceData {

	companion object {
		val parse : (String) -> WordlePerformanceData = {s ->
			val (normal, hard) = s.split("|")
			WordlePerformanceData(
				normal.split(",").map {it.toInt()}.toIntArray(),
				hard.split(",").map {it.toInt()}.toIntArray()
			)
		}
	}

	override fun savableString() : String = "${guessDistribution.joinToString(",") { "$it" }}|${hardmodeGuessDistribution.joinToString(",") { "$it" }}"
}
class WordleGameHandler : NYTGameHandler<WordlePerformanceData>() {
	override val gameName: String = "Wordle"
	override val loadPerformanceData: (String) -> WordlePerformanceData = WordlePerformanceData.parse
	override val storage = loadStorage()
	override val playerHistories = storage.load()
	override val referenceNumber: Int = 1054

	override fun updateGameHistory(user: Member, summary: String): Int? {
		val simplified = summary.lines().map { it.trim() }.joinToString("\n")
		if (!simplified.contains("Wordle ")) return null
		val (wordleLine, squares) = simplified.substringAfter("Wordle ").takeDelimited("\n\n")

		val (gameNumString, performance) = wordleLine.split(" ")
		val gameNum = gameNumString.replace(",", "").toIntOrNull() ?: return null
		val isHardMode = performance.endsWith("*")
		val guessesTakenStr = performance.substringBefore("/6")
		if (guessesTakenStr !in "123456X" || guessesTakenStr.length != 1) return null

		val guessesTaken = guessesTakenStr.toIntOrNull() ?: 7
		val expectedLines = min(guessesTaken, 6)

		var (currentLine, remaining) = squares.takeDelimited("\n")
		val validSquares = "⬛⬜🟨🟩".replace("\uD83D", "")
		repeat (expectedLines) {
			currentLine = currentLine.replace("\uD83D", "")
			if (currentLine.length != 5 || currentLine.any { it !in validSquares }) return null
			val pair = remaining.takeDelimited("\n")
			currentLine = pair.first
			remaining = pair.second
		}

		if (user.idLong !in playerHistories) {
			playerHistories[user.idLong] = ActivityData(WordlePerformanceData(), gameNum)
		}
		val history = playerHistories[user.idLong]!!
		history.setSubmitted(gameNum)
		if (isHardMode) {
			history.performanceData.hardmodeGuessDistribution[guessesTaken]++
		} else {
			history.performanceData.guessDistribution[guessesTaken]++
		}
		return gameNum
	}
}
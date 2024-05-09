package modules.nyt

import net.dv8tion.jda.api.entities.Member


class ConnectionsPerformanceData(
	val distribution : Array<IntArray> = Array(5) {IntArray(5) {0} }
) : PerformanceData {

	companion object {
		val parse : (String) -> ConnectionsPerformanceData = {s ->
			val counts = s.split(",").map { it.trim().toInt() }
			val ret = ConnectionsPerformanceData()
			repeat(24) {i ->
				ret.distribution[i / 5][i % 5] = counts[i]
			}
			ret
		}
	}

	override fun savableString() : String = distribution.joinToString { it.joinToString(",") { "$it" } }
}

class ConnectionsGameHandler : NYTGameHandler<ConnectionsPerformanceData>() {
	override val gameName: String = "Connections"
	override val loadPerformanceData: (String) -> ConnectionsPerformanceData = ConnectionsPerformanceData.parse
	override val storage = loadStorage()
	override val playerHistories = storage.load()
	override val referenceNumber: Int = 332

	override fun updateGameHistory(user: Member, summary: String): Int? {
		val simplified = summary.lines().map { it.trim() }.joinToString("\n")
		if (!simplified.contains("Connections\nPuzzle #")) return null
		val start = summary.substringAfter("Connections\nPuzzle #")
		val (gameNumStr, linesStart) = start.takeDelimited("\n")
		val gameNum = gameNumStr.replace(",", "").toIntOrNull() ?: return null
		if (!isGameNumber(gameNum)) return null

		var numCorrect = 0
		var numIncorrect = 0
		val validSquares = "🟨🟩🟦🟪".replace("\uD83D", "")
		var (currentLine, remaining) = linesStart.takeDelimited("\n")
		while (numCorrect < 4 && numIncorrect < 4) {
			if (currentLine.length != 8) return null
			currentLine = currentLine.replace("\uD83D", "")
			if (currentLine.length != 4 || currentLine.any { it !in validSquares }) return null
			val first = currentLine[0]
			if (currentLine.any { it != first }) ++numIncorrect else ++numCorrect
			val pair = remaining.takeDelimited("\n")
			currentLine = pair.first
			remaining = pair.second
		}

		if (user.idLong !in playerHistories) {
			playerHistories[user.idLong] = ActivityData(ConnectionsPerformanceData(), gameNum)
		}
		val history = playerHistories[user.idLong]!!
		history.setSubmitted(gameNum)
		history.performanceData.distribution[numCorrect][numIncorrect]++

		return gameNum
	}
}
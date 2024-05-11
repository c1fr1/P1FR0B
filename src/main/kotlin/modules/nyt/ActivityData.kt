package modules.nyt

import kotlin.math.max

class ActivityData<T : PerformanceData> (
	val performanceData : T,
	var lastUpdatedNumber : Int,
	val recentParticipation : BooleanArray = BooleanArray(7) {false},
	var overrideSpoilersAllowed : Boolean? = null
) {
	companion object {
		operator fun<T : PerformanceData> invoke(storedData : String, f : (String) -> T) : ActivityData<T> {
			val (lastNum, a) = storedData.takeDelimited()
			val (participation, b) = a.takeDelimited()
			val (override, c) = b.takeDelimited()
			return ActivityData(
				f(c),
				lastNum.toInt(),
				participation.map { it == 'y' }.toBooleanArray(),
				when (override) {
					"y" -> true
					"n" -> false
					else -> null
				}
			)
		}
	}
	fun updateParticipation(currentNumber : Int, completedToday : Boolean = false) : ActivityData<T> {
		val offset = if (completedToday) currentNumber - lastUpdatedNumber else currentNumber - lastUpdatedNumber - 1
		if (offset <= 0) return this
		repeat(max(7 - offset, 0)) { i ->
			recentParticipation[6 - i] = recentParticipation[6 - i - offset]
		}
		repeat(offset) {i ->
			recentParticipation[i] = false
		}
		if (completedToday) recentParticipation[0] = true
		lastUpdatedNumber = if (completedToday) currentNumber else currentNumber - 1
		return this
	}

	fun setSubmitted(gameNumber : Int) {
		if (lastUpdatedNumber < gameNumber) {
			updateParticipation(gameNumber, true)
			return
		} else if (lastUpdatedNumber - gameNumber in recentParticipation.indices) {
			recentParticipation[lastUpdatedNumber - gameNumber] = true
		}
	}

	fun avoidsSpoilers(currentNumber : Int) : Boolean {
		return overrideSpoilersAllowed ?: (updateParticipation(currentNumber).recentParticipation.count { it } >= 4)
	}

	fun hasSubmitted(number : Int) : Boolean {
		if (lastUpdatedNumber < number) return false
		if (lastUpdatedNumber - number !in recentParticipation.indices) return true
		return recentParticipation[lastUpdatedNumber - number]
	}

	fun savableString() : String {
		val participationRep = recentParticipation.joinToString("") { if (it) "y" else "n" }
		val spoilerOverrideRep = overrideSpoilersAllowed?.let { if (it) "y" else "n" } ?: "?"
		val performanceDataRep = performanceData.savableString()
		return "$lastUpdatedNumber,$participationRep,$spoilerOverrideRep,$performanceDataRep"
	}
}


interface PerformanceData {
	fun savableString() : String
}

//TODO move this somewhere sensible
fun String.takeDelimited(delimiter : String = ",") : Pair<String, String> = Pair(substringBefore(delimiter), if (delimiter in this) substringAfter(delimiter) else "")

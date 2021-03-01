package bot

import java.io.FileNotFoundException
import java.io.FileReader
import java.util.*

/**
 * object that stores constants, specifically Discord snowflakes for the most part
 * @author Varas#5480
 */
object IDS {

	private var names : Array<String>

	private var values : Array<String>

	init {
		val tempNames = ArrayList<String>()
		val tempValues = ArrayList<String>()

		try {
			val fr = FileReader("resources/IDS_PUBLIC")

			fr.forEachLine { line ->
				if (line.contains(":")) {
					tempNames.add(line.substringBefore(':').trim())
					tempValues.add(line.substringAfter(":").trim())
				}
			}
			fr.close()
		} catch (_ : FileNotFoundException) {
			Logger.warn("public IDS file missing, this will likely cause many problems, make sure there is a resources/IDS_PUBLIC in the working directory")
		}

		try {
			val fr = FileReader("resources/IDS_PRIVATE")

			fr.forEachLine { line ->
				if (line.contains(":")) {
					tempNames.add(line.substringBefore(':').trim())
					tempValues.add(line.substringAfter(":").trim())
				}
			}
		} catch (_ : FileNotFoundException) {
			Logger.warn("private IDS file missing, this will likely cause many problems, make sure there is a resources/IDS_PRIVATE in the working directory")
		}

		names = Array(tempNames.size) {i -> tempNames[i]}
		values = Array(tempValues.size) {i -> tempValues[i]}
	}

	/**
	 * gets a value if there is an ID with the given name
	 * @param name name of the ID
	 * @return value of the ID, will be null if there is not an ID with the given name
	 */
	fun getID(name : String) : String? {
		names.forEachIndexed {i, n ->
			if (name == n) {
				return values[i]
			}
		}
		return null
	}
}
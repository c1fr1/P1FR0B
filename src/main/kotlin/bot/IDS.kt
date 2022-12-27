package bot

import java.io.FileNotFoundException
import java.io.FileReader

/**
 * object that stores constants, specifically Discord snowflakes for the most part
 * @author Varas#5480
 */
object IDS {
	private var map = HashMap<String, String>()

	private fun collectFromFile(filename: String) {
		val fileReader = FileReader(filename)

		fileReader.forEachLine { line ->
			if (line.contains(":"))
				map[line.substringBefore(':').trim()] = line.substringAfter(":").trim()
		}

		fileReader.close()
	}

	fun init(publicFile: String?) {
		val publicFilename = "resources/${publicFile ?: "IDS_PUBLIC"}"
		try {
			collectFromFile(publicFilename)
		} catch (_ : FileNotFoundException) {
			Logger.warn("public IDS file missing, this will likely cause many problems, make sure there is a $publicFilename in the working directory")
		}

		try {
			collectFromFile("resources/IDS_PRIVATE")
		} catch (_ : FileNotFoundException) {
			Logger.warn("private IDS file missing, this will likely cause many problems, make sure there is a resources/IDS_PRIVATE in the working directory")
		}
	}

	/**
	 * gets a value if there is an ID with the given name
	 * @param name name of the ID
	 * @return value of the ID, will be null if there is not an ID with the given name
	 */
	fun get(name : String) = map[name]
}
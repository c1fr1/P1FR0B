package bot.storage

import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import kotlin.io.path.pathString

inline fun <reified Data>createStorage(
	fileName: String,
	crossinline readData: (file: File) -> Data,
	crossinline writeData: (file: File, data: Data) -> Unit,
	crossinline writePlaceholder: (file: File) -> Unit = { },
	crossinline createDefault: () -> Data? = { null }
): Storage<Data> {
	val path = Path("resources/modules/").resolve(fileName)
	val errorName = "${Data::class.simpleName} (${path.pathString})"

	return object : Storage<Data>() {
		override fun load(): Data {
			val file = File(path.absolutePathString())

			if (file.isDirectory) {
				file.deleteRecursively()
			}

			if (!file.exists()) {
				path.parent.createDirectories()

				println("missing file for $errorName")

				val default = createDefault()

				if (default != null) {
					writeData(file, default)
					return default
				} else {
					writePlaceholder(file)
					throw Exception("could not load data for $errorName")
				}
			}

			return readData(file)
		}

		override fun save(data: Data) {
			path.parent.createDirectories()
			writeData(path.toFile(), data)
		}
	}
}

abstract class Storage<Data> {
	abstract fun load(): Data
	abstract fun save(data: Data)
}

/*abstract class Storage<StorageObject> {
	companion object {
		/* only one storage file can be written at a time */
		private val pool = Executors.newFixedThreadPool(1)
	}

	private fun getFile() = Path("resources/modules/").resolve(filename).toFile()

	protected abstract val filename: Path

	protected abstract fun default(): StorageObject
	protected abstract fun serialize(storageObject: StorageObject, writer: OutputStreamWriter)
	protected abstract fun deserialize(inputStream: FileInputStream): StorageObject

	private var currentStorageObject: StorageObject? = null

	private fun retrieve(): StorageObject {
		return try {
			val file = getFile()
			if (!file.exists()) return default()

			deserialize(file.inputStream())
		} catch (ex: Exception) {
			Logger.info("failed to read data for ${filename}\n${ex.message}")
			default()
		}
	}

	/**
	 * only actually loads once, return internal cache on repeated calls
	 */
	fun load(): StorageObject {
		val current = currentStorageObject
		return if (current == null) {
			val data = retrieve()
			this.currentStorageObject = data
			data
		} else {
			current
		}
	}

	/**
	 * no effect if you have not loaded
	 */
	fun save() {
		currentStorageObject?.let {
			pool.submit {
				val writer = getFile().writer()
				serialize(it, writer)
				writer.close()
			}
		}
	}
}

inline fun <reified StorageObject>createStorage(
	filename: String,
	gson: Gson,
	crossinline default: () -> StorageObject,
): Storage<StorageObject> {
	val filenamePath = try {
		val filenamePath = Path(filename)
		val filenamePart = filenamePath.fileName

		if (filenamePart.extension == "json") {
			filenamePart
		} else {
			Logger.info("Tried to save a \".${filenamePart.extension}\" file instead of a .json file for \"${filenamePart}\"")
			Path("${filenamePart.nameWithoutExtension}.json")
		}

	} catch (ex: Throwable) {
		Logger.error("Invalid filename \"${filename}\" for storage")
		Logger.logError(ex)
		exitProcess(-1)
	}

	return object : Storage<StorageObject>() {
		override val filename = filenamePath

		override fun default() = default()

		override fun deserialize(inputStream: FileInputStream): StorageObject {
			return gson.fromJson(inputStream.reader(), StorageObject::class.java)
		}

		override fun serialize(storageObject: StorageObject, writer: OutputStreamWriter) {
			gson.toJson(storageObject, StorageObject::class.java, writer)
		}
	}
}

inline fun <reified Type> GsonBuilder.smartRegisterType(
	crossinline serialize: (Type) -> JsonElement,
	crossinline deserialize: (JsonElement) -> Type,
) : GsonBuilder {
	this.registerTypeAdapter(Type::class.java, object : JsonSerializer<Type> {
		override fun serialize(
			src: Type,
			typeOfSrc: java.lang.reflect.Type,
			context: JsonSerializationContext
		) = serialize(src)
	})

	this.registerTypeAdapter(Type::class.java, object : JsonDeserializer<Type> {
		override fun deserialize(
			json: JsonElement,
			typeOfT: java.lang.reflect.Type,
			context: JsonDeserializationContext
		) = deserialize(json)
	})

	return this
}*/

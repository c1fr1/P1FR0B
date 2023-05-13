package bot.storage

import bot.Logger
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

abstract class Storage<StorageObject> {
	companion object {
		/* only one storage file can be written at a time */
		private val pool = Executors.newFixedThreadPool(1)
	}

	private var currentStorageObject: StorageObject? = null

	abstract val filename: String

	abstract fun default(): StorageObject
	abstract fun serialize(data: StorageObject): ByteBuffer
	abstract fun deserialize(buffer: ByteBuffer): StorageObject

	private fun getFile() = File("resources/modules/${filename}")

	private fun retrieve(): StorageObject {
		return try {
			val file = getFile()
			if (!file.exists()) return default()

			deserialize(ByteBuffer.wrap(getFile().readBytes()))
		} catch (ex: Exception) {
			Logger.warn("failed to read data for ${filename}\n${ex.message}")
			default()
		}
	}

	fun get(): StorageObject {
		val current = currentStorageObject
		return if (current == null) {
			val data = retrieve()
			this.currentStorageObject = data
			data
		} else {
			current
		}
	}

	fun save() {
		pool.submit {
			val stream = FileOutputStream(getFile()).channel
			val buffer = serialize(get())
			buffer.flip()
			stream.write(buffer)
			stream.close()
		}
	}
}

package bot

import bot.modules.IModule
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.Executors

abstract class Storage<E>(module: IModule) {
	private val pool = Executors.newFixedThreadPool(1)

	private var current: E? = null

	abstract val filename: String

	abstract fun default(): E
	abstract fun serialize(data: E): ByteBuffer
	abstract fun deserialize(buffer: ByteBuffer): E

	private fun getFile() = File("resources/${filename}")

	private fun retrieve(): E {
		return try {
			val file = getFile()
			if (!file.exists()) return default()

			deserialize(ByteBuffer.wrap(getFile().readBytes()))
		} catch (ex: Exception) {
			Logger.warn("failed to read data for ${filename}\n${ex.message}")
			default()
		}
	}

	fun get(): E {
		val current = current
		return if (current == null) {
			val data = retrieve()
			this.current = data
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
